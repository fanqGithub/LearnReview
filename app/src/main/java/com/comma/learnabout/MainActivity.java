package com.comma.learnabout;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.BuddhistCalendar;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static android.app.Notification.VISIBILITY_SECRET;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button normalNoti,withProNoti,downloadBtn,mutiDownBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        normalNoti=findViewById(R.id.normalnoti);
        withProNoti=findViewById(R.id.withpronoti);
        downloadBtn=findViewById(R.id.download);
        mutiDownBtn=findViewById(R.id.mutildown);
        normalNoti.setOnClickListener(this);
        withProNoti.setOnClickListener(this);
        downloadBtn.setOnClickListener(this);
        mutiDownBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.normalnoti:
                sendNormalNotification();
                break;
            case R.id.withpronoti:
                sendProgressNoti();
                break;
            case R.id.download:
                Intent intent=new Intent(MainActivity.this,SingleDownloadActivity.class);
                startActivity(intent);
                break;
            case R.id.mutildown:
                Intent intent1=new Intent(MainActivity.this,MutilDownloadActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    private NotificationManager getManager(){
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private Notification.Builder getNotificationBuilder(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("channel_id","channel_name", NotificationManager.IMPORTANCE_DEFAULT);
            //可否绕过请勿打扰模式
            channel.canBypassDnd();
            channel.enableLights(true);
            channel.setLockscreenVisibility(VISIBILITY_SECRET);
            channel.setLightColor(Color.RED);
            channel.canShowBadge();
//            channel.enableVibration(true);
//            channel.getAudioAttributes();
//            channel.getGroup();
//            channel.setBypassDnd(true);
//            channel.setVibrationPattern(new long[]{100,100,200});
//            channel.shouldShowLights();
            getManager().createNotificationChannel(channel);

            return new Notification.Builder(this)
                    .setAutoCancel(true)
                    .setChannelId("channel_id")
                    .setContentTitle("Android notification")
                    .setContentText("this is content , you need to read it")
                    .setSmallIcon(R.mipmap.ic_launcher);
        }
        return new Notification.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Android notification")
                .setContentText("this is content , you need to read it")
                .setSmallIcon(R.mipmap.ic_launcher);
    }


    private void sendNormalNotification(){
        Notification.Builder builder=getNotificationBuilder();
        getManager().notify(1,builder.build());
    }


    private void sendProgressNoti(){
        final Notification.Builder builder=getNotificationBuilder();
        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
        getManager().notify(2,builder.build());
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<=10;i++){
                    try {
                        Thread.sleep(1000);
                        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
                        builder.setProgress(10,i,false);
                        getManager().notify(2,builder.build());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getManager().cancelAll();
    }
}
