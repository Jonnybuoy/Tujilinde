package com.example.tujilinde;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import java.util.Map;

public class CivilianReportDetailsFragment extends Fragment {

    View mView;
    TextView mCategory, mType, mReporter, mDescription;

    private FirebaseAuth mAuth;
    private DatabaseReference mCivilianDatabase;

    private String userId;

    public CivilianReportDetailsFragment(){
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView =  inflater.inflate(R.layout.civilian_report_details_view, container, false);

        mCategory = mView.findViewById(R.id.editTextCategory);
        mType = mView.findViewById(R.id.editTextType);
        mReporter = mView.findViewById(R.id.editTextReporter);
        mDescription = mView.findViewById(R.id.editTextDescription);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        getReportDetails();




        return mView;
    }



    public void getReportDetails(){
        mCivilianDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Civilians").child(userId).child("Report details");
        mCivilianDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map  = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Crime Category")!= null){
                        mCategory.setText(map.get("Crime Category").toString());
                    }

                    if(map.get("Crime Type")!= null){
                        mType.setText(map.get("Crime Type").toString());
                    }
                    if(map.get("Reporter")!= null){
                        mReporter.setText(map.get("Reporter").toString());
                    }
                    if(map.get("Crime Description")!= null){
                        mDescription.setText(map.get("Crime Description").toString());
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}

