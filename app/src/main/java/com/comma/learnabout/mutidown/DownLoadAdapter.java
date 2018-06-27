package com.comma.learnabout.mutidown;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.comma.learnabout.R;
import com.comma.learnabout.entity.ThreadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:范启 Created on 2018/6/24.
 * Description:
 */

public class DownLoadAdapter extends RecyclerView.Adapter<DownLoadAdapter.MultiDownViewHolder> {


    private Context mContext=null;
    private List<ThreadInfo> list=null;
    private OnButtonClickListener mListener=null;

    public DownLoadAdapter(Context context, List<ThreadInfo> infos){
        this.mContext=context;
        this.list=infos;
    }

    @NonNull
    @Override
    public MultiDownViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_list_item, parent, false);
        MultiDownViewHolder holder = new MultiDownViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MultiDownViewHolder holder, final int position) {
        ThreadInfo info=list.get(position);
        int currentAll=info.getEnd()-(info.getStart()-1);
        float percent=(float)info.getFinisedSize()/currentAll*100;
        holder.partName.setText("线程编号："+info.getId()+"；下载区间：["+info.getStart()+"-"+info.getEnd()+"]");
        holder.progressBar.setMax(100);
        holder.progressBar.setProgress((int) percent);
        if (percent==100){
            holder.doneText.setVisibility(View.VISIBLE);
        }else {
            holder.doneText.setVisibility(View.GONE);
        }

        holder.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener!=null){
                    mListener.startDownLoad(position);
                }
            }
        });

        holder.stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener!=null){
                    mListener.stopDownLoad(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list!=null){
            return list.size();
        }
        return 0;
    }

    class MultiDownViewHolder extends RecyclerView.ViewHolder{
        TextView partName,doneText;
        ProgressBar progressBar;
        Button startBtn;
        Button stopBtn;

        public MultiDownViewHolder(View itemView) {
            super(itemView);
            partName=itemView.findViewById(R.id.partname);
            doneText=itemView.findViewById(R.id.done);
            progressBar=itemView.findViewById(R.id.progress);
            startBtn=itemView.findViewById(R.id.start_btn);
            stopBtn=itemView.findViewById(R.id.stop_btn);
        }
    }

    public interface OnButtonClickListener{
        void startDownLoad(int position);
        void stopDownLoad(int position);
    }

    public void setButtonClickListener(OnButtonClickListener listener){
        this.mListener=listener;
    }

}
