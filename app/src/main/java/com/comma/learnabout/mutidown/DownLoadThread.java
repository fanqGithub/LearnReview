package com.comma.learnabout.mutidown;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.comma.learnabout.entity.FileInfo;
import com.comma.learnabout.entity.ThreadInfo;
import com.comma.learnabout.greendao.DBManager;
import com.comma.learnabout.greendao.ThreadInfoDao;
import com.comma.learnabout.service.DownLoadService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fanqi on 2018/6/21.
 * Description:
 */

public class DownLoadThread extends Thread{

    private ThreadInfoDao threadInfoDao=null;
    private int mFinishedSize=0;
    public boolean mPause=false;
    private ThreadInfo newThreadInfo;
    private FileInfo mFileInfo=null;
    private Context mContext=null;

    public DownLoadThread(ThreadInfo info,FileInfo fileInfo,Context context) {
        this.threadInfoDao=DBManager.get().getThreadInfoDao();
        this.newThreadInfo=info;
        this.mFileInfo=fileInfo;
        this.mContext=context;
    }

    @Override
    public void run() {
        Log.d("down","线程"+newThreadInfo.getId()+"开启");
        ThreadInfo exiInfo=threadInfoDao.queryBuilder().where(ThreadInfoDao.Properties.Id.eq(newThreadInfo.getId())).unique();
        if (exiInfo==null){
            threadInfoDao.insert(newThreadInfo);
        }
        HttpURLConnection conn=null;
        RandomAccessFile raf=null;
        InputStream is=null;
        try {
            URL url=new URL(mFileInfo.getFileUrl());
            conn= (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            //设置从哪里开始下载
            int start=newThreadInfo.getStart()+newThreadInfo.getFinisedSize();
            conn.setRequestProperty("Range","bytes="+start+"-"+newThreadInfo.getEnd());
            File file=new File(MultiDownLoadManger.DOWNLOADPATH,mFileInfo.getFileName());
            raf=new RandomAccessFile(file,"rwd");
            raf.seek(start);
            mFinishedSize+=newThreadInfo.getFinisedSize();
            int currentAll=newThreadInfo.getEnd()-(newThreadInfo.getStart()-1);
            Intent intent=new Intent(MultiDownLoadManger.BROCAST_UPDATE_PROGRESS);
            if (conn.getResponseCode()==HttpURLConnection.HTTP_PARTIAL){
                is=conn.getInputStream();
                byte[] buffer=new byte[1024*4];
                int len=-1;
                long time=System.currentTimeMillis();
                while ((len=is.read(buffer))!=-1){
                    raf.write(buffer,0,len);
                    mFinishedSize+=len;
                    if (System.currentTimeMillis()-time>1500) {
                        Log.d("down","线程"+newThreadInfo.getId()+";"+"已经下载进度:"+(float)mFinishedSize/currentAll*100);
                        intent.putExtra("threadid",newThreadInfo.getId());
                        intent.putExtra("finisedsize",mFinishedSize);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    }
                    //暂停--》更新
                    if (mPause){
                        Log.d("down","线程["+newThreadInfo.getId()+"]暂停下载");
                        newThreadInfo.setFinisedSize(mFinishedSize);
                        threadInfoDao.update(newThreadInfo);
                        return;
                    }
                }
                //下完，删除保存的信息
                Log.d("down","线程"+newThreadInfo.getId()+"已完成");
                threadInfoDao.delete(newThreadInfo);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                raf.close();
                is.close();
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
