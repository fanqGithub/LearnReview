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
    public void onBindViewHolder(@NonNull MultiDownViewHolder holder, int position) {
        ThreadInfo info=list.get(position);
        int currentAll=info.getEnd()-(info.getStart()-1);
        float percent=(float)info.getFinisedSize()/currentAll*100;
        holder.partName.setText("线程："+info.getId());
        holder.progressBar.setMax(100);
        holder.progressBar.setProgress((int) percent);
    }

    @Override
    public int getItemCount() {
        if (list!=null){
            return list.size();
        }
        return 0;
    }

    class MultiDownViewHolder extends RecyclerView.ViewHolder{
        TextView partName;
        ProgressBar progressBar;
        Button startBtn;
        Button stopBtn;

        public MultiDownViewHolder(View itemView) {
            super(itemView);
            partName=itemView.findViewById(R.id.partname);
            progressBar=itemView.findViewById(R.id.progress);
            startBtn=itemView.findViewById(R.id.start_btn);
            stopBtn=itemView.findViewById(R.id.stop_btn);
        }
    }

    public void notifyThreadProgress(int id,int finishedsize){
        if (list!=null && list.size()>0) {
            ThreadInfo info = list.get(id);
            info.setFinisedSize(finishedsize);
            list.set(id,info);
            notifyDataSetChanged();
        }
    }


}
