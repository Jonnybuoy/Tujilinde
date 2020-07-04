package com.example.tujilinde.historyRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tujilinde.R;

import java.util.List;

public class AlertHistoryAdapter extends RecyclerView.Adapter<AlertHistoryViewHolders> {

    private List<AlertHistoryObject> itemList;
    private Context context;

    public AlertHistoryAdapter(List<AlertHistoryObject> itemList, Context context){
        this.itemList = itemList;
        this.context = context;
    }
    @NonNull
    @Override
    public AlertHistoryViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_items, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        AlertHistoryViewHolders rev = new AlertHistoryViewHolders(layoutView);
        return rev;

    }

    @Override
    public void onBindViewHolder(@NonNull AlertHistoryViewHolders holder, int position) {
        holder.mReportDate.setText(itemList.get(position).getDate());
        holder.mRefCode.setText(itemList.get(position).getRefCode());

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
