package com.example.tujilinde.historyRecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tujilinde.R;

public class AlertHistoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mRefCode;
    public TextView mStatus;
    public TextView mReportDate;
    public AlertHistoryViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mRefCode= itemView.findViewById(R.id.crimeRefCode);
        mStatus = itemView.findViewById(R.id.textSetStatus);
        mReportDate = itemView.findViewById(R.id.dateReceived);




    }

    @Override
    public void onClick(View v) {


    }
}
