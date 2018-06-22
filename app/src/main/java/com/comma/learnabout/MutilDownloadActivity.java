package com.comma.learnabout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MutilDownloadActivity extends AppCompatActivity {

    private Spinner mSpinner=null;
    private Button startBtn=null;
    private RecyclerView mRecyclerView=null;

    private static final Integer[] m={1,2,3};
    private ArrayAdapter<Integer> sadapter;

    private int downThreadCount=1;

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
        //添加事件Spinner事件监听
        mSpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
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


}
