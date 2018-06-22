package com.comma.learnabout.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.comma.learnabout.entity.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by fanqi on 2018/6/21.
 * Description:
 */

public class DownLoadService extends Service {

    public final static String ACTION_START="ACTION_START";
    public final static String ACTION_STOP="ACTION_STOP";
    public final static String BROCAST_UPDATE_PROGRESS="brocast_update_progress";
    public final static String DOWNLOADPATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/LearnAbout/";
    private DownLoadTask task=null;
    private final static int MSG_WHAT_START=1;
    private final static int MSG_WHAT_STOP=2;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FileInfo fileInfo= (FileInfo) intent.getSerializableExtra("fileinfo");
        if (ACTION_START.equals(intent.getAction())){
            new InitThread(fileInfo).start();
        }else if (ACTION_STOP.equals(intent.getAction())){
            if (task!=null){
                task.mPause=true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private Handler mHandler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_WHAT_START:
                    FileInfo fileInfo= (FileInfo) msg.obj;
                    Log.d("down","task的文件长度:"+fileInfo.getLength());
                    task=new DownLoadTask(DownLoadService.this,fileInfo);
                    task.download();
                    break;
                case MSG_WHAT_STOP:
                    break;
                default:
                    break;
            }
        }
    };



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class InitThread extends Thread{
        private FileInfo info=null;

        public InitThread(FileInfo info) {
            this.info = info;
        }

        @Override
        public void run() {
            HttpURLConnection conn=null;
            RandomAccessFile raf=null;
            try {
                URL url=new URL(info.getFileUrl());
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
                    fileName = URLDecoder.decode(fileName.substring(
                            fileName.indexOf("filename=") + 9), "UTF-8");
                    // 有些文件名会被包含在""里面，所以要去掉，不然无法读取文件后缀
                    fileName = fileName.replaceAll("\"", "");
                }
                info.setFileName(fileName);
                File file=new File(dir,info.getFileName());
                raf=new RandomAccessFile(file,"rwd");
                raf.setLength(length);
                info.setLength(length);
                Log.d("down","获取到的文件长度:"+info.getLength());
                mHandler.obtainMessage(MSG_WHAT_START,info).sendToTarget();
            } catch (java.io.IOException e) {
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



}
