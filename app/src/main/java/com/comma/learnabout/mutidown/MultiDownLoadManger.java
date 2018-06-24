package com.comma.learnabout.mutidown;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.comma.learnabout.entity.FileInfo;
import com.comma.learnabout.entity.ThreadInfo;
import com.comma.learnabout.greendao.DBManager;
import com.comma.learnabout.greendao.ThreadInfoDao;
import com.comma.learnabout.service.DownLoadService;
import com.comma.learnabout.service.DownLoadTask;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanqi on 2018/6/21.
 * Description:
 */

public class MultiDownLoadManger{

    public final static String ACTION_START="ACTION_START";
    public final static String ACTION_STOP="ACTION_STOP";
    public final static String BROCAST_UPDATE_PROGRESS="brocast_multi_update_progress";
    public final static String DOWNLOADPATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/LearnAbout/";
    private DownLoadTask task=null;
    private final static int MSG_INIT_DONE=1;

    private int threadCount=1;
    private FileInfo mInfo=null;
    private Context mContext=null;
    private ThreadAddListener mListener=null;

    private List<ThreadInfo> infos=new ArrayList<>();

    public MultiDownLoadManger(Context context,FileInfo fileInfo,int count){
        this.mInfo=fileInfo;
        this.threadCount=count;
        this.mContext=context;
    }

    private Handler mHandler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_INIT_DONE:
                    if (mListener!=null){
                        mListener.addThreadDone(infos);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void downLoad(){
        new InitThread().start();
    }

    class InitThread extends Thread{
        @Override
        public void run() {
            HttpURLConnection conn=null;
            RandomAccessFile raf=null;
            try {
                URL url=new URL(mInfo.getFileUrl());
                conn= (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                int length=-1;
                if (conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                    length=conn.getContentLength();
                }
                if (length<0){
                    return;
                }
                File dir=new File(DOWNLOADPATH);
                if (!dir.exists()){
                    dir.mkdirs();
                }
                String fileName = conn.getHeaderField("Content-Disposition");
                // 通过Content-Disposition获取文件名，这点跟服务器有关，需要灵活变通
                if (fileName == null || fileName.length() < 1) {
                    // 通过截取URL来获取文件名
                    // 获得实际下载文件的URL
                    URL downloadUrl = conn.getURL();
                    fileName = downloadUrl.getFile();
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                } else {
                    fileName = URLDecoder.decode(fileName.substring(fileName.indexOf("filename=") + 9), "UTF-8");
                    // 有些文件名会被包含在""里面，所以要去掉，不然无法读取文件后缀
                    fileName = fileName.replaceAll("\"", "");
                }
                mInfo.setFileName(fileName);
                File file=new File(dir,mInfo.getFileName());
                raf=new RandomAccessFile(file,"rwd");
                raf.setLength(length);
                mInfo.setLength(length);
                Log.d("down","获取到的文件长度:"+mInfo.getLength());
                //
                int blockSize = length/threadCount;
                for(int threadId = 0; threadId < threadCount; threadId++){
                    int startIndex = threadId * blockSize;
                    int endIndex = (threadId+1) * blockSize -1;
                    if(threadId == (threadCount - 1)){
                        //如果是最后一个线程,将剩下的文件全部交给这个线程完成
                        endIndex = length - 1;
                    }
                    ThreadInfo newThreadInfo= DBManager.get().getThreadInfoDao().queryBuilder().where(ThreadInfoDao.Properties.Id.eq(threadId)).unique();
                    if (newThreadInfo==null){
                        newThreadInfo=new ThreadInfo(threadId,mInfo.getFileUrl(),startIndex,endIndex,0);
                    }
                    infos.add(newThreadInfo);
                    new DownLoadThread(newThreadInfo,mInfo,mContext).start();
                }
                mHandler.sendEmptyMessage(1);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    raf.close();
                    conn.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface ThreadAddListener{
        void addThreadDone(List<ThreadInfo> infos);
    }

    public void setAddListener(ThreadAddListener listener){
        this.mListener=listener;
    }

}
