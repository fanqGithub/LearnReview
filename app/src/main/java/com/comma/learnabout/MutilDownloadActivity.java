package com.comma.learnabout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.comma.learnabout.entity.FileInfo;
import com.comma.learnabout.entity.ThreadInfo;
import com.comma.learnabout.mutidown.DownLoadAdapter;
import com.comma.learnabout.mutidown.DownLoadThread;
import com.comma.learnabout.mutidown.MultiDownLoadManger;
import com.comma.learnabout.service.DownLoadService;

import java.util.ArrayList;
import java.util.List;

public class MutilDownloadActivity extends AppCompatActivity {

    private Spinner mSpinner=null;
    private Button startBtn=null;
    private RecyclerView mRecyclerView=null;

    private static final Integer[] m={1,2,3};
    private ArrayAdapter<Integer> sadapter;

    private int downThreadCount=1;

    private DownLoadAdapter mAdapter=null;
    private List<ThreadInfo> mLits=new ArrayList<>();
    private MultiDownLoadManger multiDownLoadManger;
    private List<DownLoadThread> downLoadThreads=new ArrayList<>();

    final FileInfo info=new FileInfo(1,"","http://gyxz.hwm6b6.cn/yq/yx_lm1/gat5sjb.apk",0,0,false);

    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(MultiDownLoadManger.BROCAST_UPDATE_PROGRESS.equals(intent.getAction())){
                int finisedsize=intent.getIntExtra("finisedsize",0);
                long threadid=intent.getLongExtra("threadid",0);
                if (mLits!=null && mLits.size()>0) {
                    ThreadInfo info = mLits.get((int)threadid);
                    info.setFinisedSize(finisedsize);
                    Log.d("更新后的info","thread="+info.toString());
                    mLits.set((int) threadid,info);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutil_download);

        mSpinner=findViewById(R.id.spinner);
        startBtn=findViewById(R.id.startAll);
        mRecyclerView=findViewById(R.id.down_thread_list);

        sadapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item,m);
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(sadapter);

        final LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayout.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter=new DownLoadAdapter(this,mLits);
        mRecyclerView.setAdapter(mAdapter);

        //添加事件Spinner事件监听
        mSpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTask();
            }
        });
        IntentFilter filter=new IntentFilter();
        filter.addAction(MultiDownLoadManger.BROCAST_UPDATE_PROGRESS);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,filter);

        mAdapter.setButtonClickListener(new DownLoadAdapter.OnButtonClickListener() {

            @Override
            public void startDownLoad(int position) {
                DownLoadThread downLoadThread=downLoadThreads.get(position);
                downLoadThread.mPause=false;
            }

            @Override
            public void stopDownLoad(int position) {
                DownLoadThread downLoadThread=downLoadThreads.get(position);
                downLoadThread.mPause=true;
            }
        });


    }

    private void startTask() {
        multiDownLoadManger=new MultiDownLoadManger(MutilDownloadActivity.this,info,downThreadCount);
        multiDownLoadManger.setAddListener(new MultiDownLoadManger.ThreadAddListener() {
            @Override
            public void addThreadDone(List<ThreadInfo> infos) {
                mLits.addAll(infos);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void callBackAllDownThreads(List<DownLoadThread> threads) {
                downLoadThreads.addAll(threads);
            }

        });
        multiDownLoadManger.downLoad();
    }

    private class SpinnerSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            downThreadCount=m[position];
            Log.d("downcount","downcount="+downThreadCount);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(MutilDownloadActivity.this).unregisterReceiver(mReceiver);
    }
}
