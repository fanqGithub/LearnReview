package com.comma.learnabout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.comma.learnabout.entity.FileInfo;
import com.comma.learnabout.service.DownLoadService;

public class SingleDownloadActivity extends AppCompatActivity {

    private Button startBtn,stopBtn;
    private TextView fileName;
    private ProgressBar progressBar;

    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(DownLoadService.BROCAST_UPDATE_PROGRESS.equals(intent.getAction())){
                float finisedsize=intent.getFloatExtra("finishedsize",0);
                String filename=intent.getStringExtra("filename");
                fileName.setText("文件名："+filename+"；进度："+(int)finisedsize+"%");
                progressBar.setProgress((int)finisedsize);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_download);

        startBtn=findViewById(R.id.start_btn);
        stopBtn=findViewById(R.id.stop_btn);
        fileName=findViewById(R.id.filename);
        progressBar=findViewById(R.id.progress);

        progressBar.setMax(100);

        final FileInfo info=new FileInfo(1,"","http://gyxz.hwm6b6.cn/hk/rj_hq1/koudaiyoushu.apk",0,0,false);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SingleDownloadActivity.this, DownLoadService.class);
                intent.setAction(DownLoadService.ACTION_START);
                intent.putExtra("fileinfo",info);
                startService(intent);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SingleDownloadActivity.this, DownLoadService.class);
                intent.setAction(DownLoadService.ACTION_STOP);
                intent.putExtra("fileinfo",info);
                startService(intent);
            }
        });

        IntentFilter filter=new IntentFilter();
        filter.addAction(DownLoadService.BROCAST_UPDATE_PROGRESS);
        LocalBroadcastManager.getInstance(SingleDownloadActivity.this).registerReceiver(mReceiver,filter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(SingleDownloadActivity.this).unregisterReceiver(mReceiver);
    }
}
