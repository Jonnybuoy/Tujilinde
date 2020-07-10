package com.example.tujilinde;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tujilinde.historyRecyclerView.AlertHistoryAdapter;
import com.example.tujilinde.historyRecyclerView.AlertHistoryObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class  AgentReceivedReportsFragment extends Fragment {

    View mView;

    RecyclerView mHistoryRecyclerView;
    private RecyclerView.LayoutManager mHistoryLayoutManager;
    private RecyclerView.Adapter mHistoryAdapter;

    private String civilianId = "";
    String agentID;



    public AgentReceivedReportsFragment(){
        // Required empty constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_agent_received_reports, container, false);



        mHistoryRecyclerView = mView.findViewById(R.id.historyRecyclerView);
        mHistoryRecyclerView.setNestedScrollingEnabled(false);
        mHistoryRecyclerView.setHasFixedSize(true);
        mHistoryLayoutManager = new LinearLayoutManager(getActivity());
        mHistoryRecyclerView.setLayoutManager(mHistoryLayoutManager);
        mHistoryAdapter = new AlertHistoryAdapter(getDataSetHistory(), getActivity());
        mHistoryRecyclerView.setAdapter(mHistoryAdapter);

        agentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fetchResponseHistoryId();

        return mView;
    }



    public void fetchResponseHistoryId() {
        DatabaseReference reporterIdRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Security Agents").child(agentID).child("responseHistory");
        reporterIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot history : dataSnapshot.getChildren()){
                        FetchReportDetails(history.getKey());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void FetchReportDetails(String responseKey) {
        DatabaseReference responseHistoryRef = FirebaseDatabase.getInstance().getReference().child("responseHistory").child(responseKey);
        responseHistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String date = "";
                    String refCode = "";

                    if (dataSnapshot.child("Response datetime").getValue() != null){
                        date = dataSnapshot.child("Response datetime").getValue().toString();
                    }
                    if (dataSnapshot.child("Reference code").getValue() != null){
                        refCode = dataSnapshot.child("Reference code").getValue().toString();
                    }
                    AlertHistoryObject object = new AlertHistoryObject(date, refCode);
                    resultsHistory.add(object);
                    mHistoryAdapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList resultsHistory = new ArrayList<AlertHistoryObject>();
    private List<AlertHistoryObject> getDataSetHistory() {
        return resultsHistory;
    }


}
