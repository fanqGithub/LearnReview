package com.comma.learnabout.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.comma.learnabout.entity.FileInfo;
import com.comma.learnabout.entity.ThreadInfo;
import com.comma.learnabout.greendao.DBManager;
import com.comma.learnabout.greendao.ThreadInfoDao;

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

public class DownLoadTask {

    private Context mContext=null;
    private FileInfo info=null;
    private ThreadInfoDao threadInfoDao=null;
    private int mFinishedSize=0;
    public boolean mPause=false;

    public DownLoadTask(Context mContext, FileInfo info) {
        this.mContext = mContext;
        this.info = info;
        this.threadInfoDao=DBManager.get().getThreadInfoDao();
    }

    public void download(){
        ThreadInfo exiInfo=threadInfoDao.queryBuilder().where(ThreadInfoDao.Properties.Url.eq(info.getFileUrl())).unique();
        if (exiInfo==null){
            exiInfo=new ThreadInfo(1,info.getFileUrl(),0,info.getLength(),0);
        }
        new DownThread(exiInfo).start();
    }


    //
    class DownThread extends Thread{

        private ThreadInfo threadInfo=null;

        public DownThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {
            ThreadInfo exiInfo=threadInfoDao.queryBuilder().where(ThreadInfoDao.Properties.Url.eq(threadInfo.getUrl())).unique();
            if (exiInfo==null){
                threadInfoDao.insert(threadInfo);
            }
            HttpURLConnection conn=null;
            RandomAccessFile raf=null;
            InputStream is=null;
            try {
                URL url=new URL(info.getFileUrl());
                conn= (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                //设置从哪里开始下载
                int start=threadInfo.getStart()+threadInfo.getFinisedSize();
                conn.setRequestProperty("Range","bytes="+start+"-"+threadInfo.getEnd());
                File file=new File(DownLoadService.DOWNLOADPATH,info.getFileName());
                raf=new RandomAccessFile(file,"rwd");
                raf.seek(start);
                Intent intent=new Intent(DownLoadService.BROCAST_UPDATE_PROGRESS);
                mFinishedSize+=threadInfo.getFinisedSize();
                //down and write
                if (conn.getResponseCode()==HttpURLConnection.HTTP_PARTIAL){
                    is=conn.getInputStream();
                    byte[] buffer=new byte[1024*4];
                    int len=-1;
                    long time=System.currentTimeMillis();
                    Log.d("down","总size："+info.getLength());
                    while ((len=is.read(buffer))!=-1){
                        raf.write(buffer,0,len);
                        mFinishedSize+=len;
                        Log.d("down","已经下载size："+mFinishedSize);
                        Log.d("down","已经下载进度："+(float)mFinishedSize/info.getLength()*100);
                        if (System.currentTimeMillis()-time>500) {
                            intent.putExtra("finishedsize",(float) mFinishedSize/info.getLength()*100);
                            intent.putExtra("filename",info.getFileName());
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                        }
                        //暂停--》更新
                        if (mPause){
                            Log.d("down","暂停下载");
                            threadInfo.setFinisedSize(mFinishedSize);
                            threadInfoDao.update(threadInfo);
                            return;
                        }
                    }
                    //下完，删除保存的信息
                    Log.d("down","已完成");
                    threadInfoDao.delete(threadInfo);
                }


            } catch (java.io.IOException e) {
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
}
