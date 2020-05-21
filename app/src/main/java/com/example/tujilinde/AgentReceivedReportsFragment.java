package com.example.tujilinde;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class AgentReceivedReportsFragment extends Fragment {

    View mView;
    Button mRefButton;
    TextView mStatus;
    TextView mReportDate;

    private String civilianId = "";



    public AgentReceivedReportsFragment(){
        // Required empty constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_agent_received_reports, container, false);


        mRefButton = mView.findViewById(R.id.crimeRefBtn);
        mStatus = mView.findViewById(R.id.textSetStatus);
        mReportDate = mView.findViewById(R.id.dateReceived);

        mRefButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AgentReceivedCrimeDetails fragment = new AgentReceivedCrimeDetails();
                fragmentTransaction.replace(R.id.agent_fragment_container, fragment);
                fragmentTransaction.commit();
                fragmentTransaction.addToBackStack(null);
            }
        });

        fetchReporterId();


        return mView;
    }

    public void fetchReporterId() {
        String agentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reporterIdRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Security Agents").child(agentID).child("reporterId");
        reporterIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    civilianId = dataSnapshot.getValue().toString();
                    fetchReportDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void fetchReportDetails(){
        DatabaseReference ReportRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Civilians").child(civilianId).child("Report details");
        ReportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map  = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Date of report")!= null){
                        mReportDate.setText(map.get("Date of report").toString());
                    }

                    if(map.get("Reference code")!= null){
                        mRefButton.setText(map.get("Reference code").toString());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
