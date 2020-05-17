package com.example.tujilinde;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentProfileActivity extends AppCompatActivity {

    EditText mNameField;
    EditText mIdField;
    EditText mDateField;
    EditText mAgentNumField;
    EditText mAgentPostField;
    Button mSubmitButton;
    Spinner mspinner;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private FirebaseAuth mAuth;
    private DatabaseReference mAgentDatabase;

    private String userId;
    private String mName;
    private String mID;
    private String mDOB;
    private String mAgentNum;
    private String mAgentPost;
    private String item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_profile);

        mNameField = findViewById(R.id.textLegalName);
        mIdField = findViewById(R.id.textIdNumber);
        mDateField = findViewById(R.id.textDate);
        mSubmitButton = findViewById(R.id.civSubmitButton);
        mspinner = findViewById(R.id.spinnerFormCategory);
        mAgentNumField = findViewById(R.id.textAgentNum);
        mAgentPostField = findViewById(R.id.textAgentPost);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mAgentDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Security Agents").child(userId);

        getUserInformation();

        mDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AgentProfileActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                mDOB = month + "/" + dayOfMonth + "/" + year;
                mDateField.setText(mDOB);
            }
        };

        List<String> categories = new ArrayList<>();
        categories.add(0, "Security Formation Category");
        categories.add("Diplomatic Police Unit");
        categories.add("General Service Unit(GSU)");
        categories.add("Directorate of Criminal Investigation(DCI)");
        categories.add("Kenya Railways Police");
        categories.add("Tourist Police Unit");

        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspinner.setAdapter(dataAdapter);

        mspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Security Formation Category")){
                    // do nothing
                }
                else{
                    item = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });









        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

    }

    private void getUserInformation(){
        mAgentDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map <String,Object> map  = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!= null){
                        mName = map.get("name").toString();
                        mNameField.setText(mName);
                    }

                    if(map.get("National ID")!= null){
                        mID = map.get("National ID").toString();
                        mIdField.setText(mID);
                    }

                    if(map.get("DOB")!= null){
                        mDOB = map.get("DOB").toString();
                        mDateField.setText(mDOB);
                    }
                    if(map.get("Agent Number")!= null){
                        mAgentNum = map.get("DOB").toString();
                        mAgentNumField.setText(mAgentNum);
                    }
                    if(map.get("Agent Post")!= null){
                        mAgentPost = map.get("Agent Post").toString();
                        mAgentPostField.setText(mAgentPost);
                    }
                    if(map.get("Formation")!= null){
                        item = map.get("Formation").toString();

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    private void saveUserInformation(){
        mName = mNameField.getText().toString();
        mID = mIdField.getText().toString();
        mDOB = mDateField.getText().toString();
        mAgentNum = mAgentNumField.getText().toString();
        mAgentPost = mAgentPostField.getText().toString();

        if(mName.isEmpty()){
            mNameField.setError("This field is required");
            return;
        }
        if(mID.isEmpty()){
            mIdField.setError("This field is required");
            return;
        }
        if(mDOB.isEmpty()){
            mDateField.setError("This field is required");
            return;
        }
        if(mAgentNum.isEmpty()){
            mAgentNumField.setError("This field is required");
            return;
        }
        if(mAgentPost.isEmpty()){
            mAgentPostField.setError("This field is required");
            return;
        }

        Map userInfo = new HashMap();
        userInfo.put("name",mName);
        userInfo.put("National ID",mID);
        userInfo.put("DOB",mDOB);
        userInfo.put("Officer Number",mAgentNum);
        userInfo.put("Security Post",mAgentPost);
        userInfo.put("Security Formation",item);
        mAgentDatabase.updateChildren(userInfo);

        Intent intent = new Intent(AgentProfileActivity.this, AgentNavigationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}
