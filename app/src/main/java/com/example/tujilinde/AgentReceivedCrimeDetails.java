package com.example.tujilinde;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentReceivedCrimeDetails extends Fragment {

    View mView;
    TextView mCategoryCrime;
    TextView mTypeCrime;
    TextView mDescriptionCrime;
    TextView mReporterType;
    EditText mAgentFindings;
    Spinner mStatusCrime;
    Button mFindingsSubmit;

    DatabaseReference mCivilianDatabase;
    DatabaseReference reporterIdRef;


    private String civilianId = "";
    private String agentID;
    private String statusItem;
    private String agent_findings;


    public AgentReceivedCrimeDetails(){
        //Empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.agent_received_crime_details, container, false);


        agentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mCategoryCrime = mView.findViewById(R.id.text_crime_category);
        mTypeCrime = mView.findViewById(R.id.text_crime_type);
        mDescriptionCrime = mView.findViewById(R.id.text_crime_description);
        mReporterType = mView.findViewById(R.id.text_reported_as);
        mAgentFindings = mView.findViewById(R.id.editAgentReport);
        mStatusCrime = mView.findViewById(R.id.spinnerStatus);
        mFindingsSubmit = mView.findViewById(R.id.findingsSubmitBtn);

        final List<String> reportStatus = new ArrayList<>();
        reportStatus.add(0, "Set Crime Case Status");
        reportStatus.add(1, "Under Investigation");
        reportStatus.add(2, "Investigation Pending Outcome");
        reportStatus.add(3, "Investigation Closed");

        ArrayAdapter<String> dataAdapter1;
        dataAdapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, reportStatus);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStatusCrime.setAdapter(dataAdapter1);

        mStatusCrime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Set Crime Case Status")){
                    mStatusCrime.setPrompt("You need to choose a selection");
                }
                else {
                    statusItem = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mFindingsSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAgentFindings();
            }
        });

        fetchReporterId();
        fetchAgentReport();


        return mView;
    }

    public void fetchReporterId() {
        reporterIdRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Security Agents").child(agentID).child("reporterId");
        reporterIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    civilianId = dataSnapshot.getValue().toString();
                    fetchCrimeDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void fetchCrimeDetails(){
        mCivilianDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Civilians").child(civilianId).child("Report details");
        mCivilianDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map  = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Crime Category")!= null){
                        mCategoryCrime.setText(map.get("Crime Category").toString());
                    }

                    if(map.get("Crime Type")!= null){
                        mTypeCrime.setText(map.get("Crime Type").toString());
                    }
                    if(map.get("Reporter")!= null){
                        mReporterType.setText(map.get("Reporter").toString());
                    }
                    if(map.get("Crime Description")!= null){
                        mDescriptionCrime.setText(map.get("Crime Description").toString());
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void fetchAgentReport(){
        reporterIdRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Security Agents").child(agentID).child("reporterId");
        reporterIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0 ){
                    Map <String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Agent Findings Description") != null){
                        agent_findings = map.get("Agent Findings Description").toString();

                    }
                    if(map.get("Investigation Status") != null){
                        statusItem = map.get("Investigation Status").toString();

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void saveAgentFindings(){
        TextView errorText = (TextView)mStatusCrime.getSelectedView();
        agent_findings = mAgentFindings.getText().toString();
        statusItem = mStatusCrime.getSelectedItem().toString();

        if (agent_findings.isEmpty()){
            mAgentFindings.setError("This field is required");
            return;
        }
        if (statusItem.isEmpty()){
            errorText.setError("This is required");

        }

        Map findingsDetails = new HashMap();
        findingsDetails.put("Agent Findings Description", agent_findings);
        findingsDetails.put("Investigation Status", statusItem);
        reporterIdRef.updateChildren(findingsDetails);
    }
}
