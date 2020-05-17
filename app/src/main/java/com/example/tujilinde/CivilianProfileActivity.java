package com.example.tujilinde;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CivilianProfileActivity extends AppCompatActivity {

    EditText mNameField;
    EditText mIdField;
    EditText mDateField;
    Button mSubmitButton;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private FirebaseAuth mAuth;
    private DatabaseReference mCivilianDatabase;

    private String userId;
    private String mName;
    private String mID;
    private String mDOB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_civilian_profile);

        mNameField = findViewById(R.id.textLegalName);
        mIdField = findViewById(R.id.textIdNumber);
        mDateField = findViewById(R.id.textDate);
        mSubmitButton = findViewById(R.id.civSubmitButton);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mCivilianDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Civilians").child(userId).child("Profile Details");

        getUserInformation();

        mDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CivilianProfileActivity.this,
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

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

    }

    private void getUserInformation(){
        mCivilianDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map <String,Object> map  = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Name")!= null){
                        mName = map.get("Name").toString();
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

        Map userInfo = new HashMap();
        userInfo.put("Name",mName);
        userInfo.put("National ID",mID);
        userInfo.put("DOB",mDOB);
        mCivilianDatabase.updateChildren(userInfo);

        Intent intent = new Intent(CivilianProfileActivity.this, CivilianNavigationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}
