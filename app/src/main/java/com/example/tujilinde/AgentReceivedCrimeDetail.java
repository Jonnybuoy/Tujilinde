package com.example.tujilinde;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

public class AgentReceivedCrimeDetail extends AppCompatActivity implements OnMapReadyCallback {

    TextView mCategoryCrime;
    TextView mTypeCrime;
    TextView mDescriptionCrime;
    TextView mReporterType, mReporterName;
    EditText mAgentFindings;
    Spinner mStatusCrime;
    Button mFindingsSubmit;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    DatabaseReference mCivilianDatabase;
    DatabaseReference reporterIdRef;


    private String civilianId = "";
    private String agentID;
    private String statusItem;
    private String agent_findings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agent_received_crime_details);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mMapFragment.getMapAsync(this);

        agentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mCategoryCrime = findViewById(R.id.text_crime_category);
        mTypeCrime = findViewById(R.id.text_crime_type);
        mDescriptionCrime = findViewById(R.id.text_crime_description);
        mReporterName = findViewById(R.id.txtReporterName);
        mReporterType = findViewById(R.id.text_reported_as);
        mAgentFindings = findViewById(R.id.editAgentReport);
        mStatusCrime = findViewById(R.id.spinnerStatus);
        mFindingsSubmit = findViewById(R.id.findingsSubmitBtn);

        final List<String> reportStatus = new ArrayList<>();
        reportStatus.add(0, "Crime Scene Status");
        reportStatus.add(1, "Under Investigation");
        reportStatus.add(2, "False Crime Scene");
        reportStatus.add(3, "Investigation Closed");

        ArrayAdapter<String> dataAdapter1;
        dataAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reportStatus);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStatusCrime.setAdapter(dataAdapter1);

        mStatusCrime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Crime Scene Status")){
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

        fetchReportHistoryId();
        fetchAgentReport();

    }

    public void fetchReportHistoryId() {
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

    private void FetchReportDetails(String key) {
        DatabaseReference mReportRef= FirebaseDatabase.getInstance().getReference().child("responseHistory").child(key);
        mReportRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

        mReportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot child:dataSnapshot.getChildren()){
                        if (child.getKey().equals("Crime Reporter")){
                            civilianId = child.getValue().toString();
                            getReporterInfo("Civilians", civilianId);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getReporterInfo(String Civilians, String civilianId) {
        mCivilianDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(Civilians).child(civilianId).child("Profile Details");
        mCivilianDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map  = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Name")!= null){
                        mReporterName.setText(map.get("Name").toString());
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

//    public void fetchCrimeDetails(){
//        mCivilianDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Civilians").child(civilianId).child("Report details");
//        mCivilianDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
//                    Map<String,Object> map  = (Map<String, Object>) dataSnapshot.getValue();
//                    if(map.get("Crime Category")!= null){
//                        mCategoryCrime.setText(map.get("Crime Category").toString());
//                    }
//
//                    if(map.get("Crime Type")!= null){
//                        mTypeCrime.setText(map.get("Crime Type").toString());
//                    }
//                    if(map.get("Reporter")!= null){
//                        mReporterType.setText(map.get("Reporter").toString());
//                    }
//                    if(map.get("Crime Description")!= null){
//                        mDescriptionCrime.setText(map.get("Crime Description").toString());
//                    }
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}