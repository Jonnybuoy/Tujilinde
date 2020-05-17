package com.example.tujilinde;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class CivilianReportHistoryFragment extends Fragment {

    TextView mReportDate;
    Button mReferenceCode;
    View view;

    private FirebaseAuth mAuth;
    private DatabaseReference mCivilianDatabase;

    private String userId;


    public CivilianReportHistoryFragment(){
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_history, container, false);

        mReportDate = view.findViewById(R.id.date_of_report);
        mReferenceCode = view.findViewById(R.id.ref_button);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mReferenceCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                CivilianReportDetailsFragment fragment = new CivilianReportDetailsFragment();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();
                fragmentTransaction.addToBackStack(null);
            }
        });

        getReportData();

        return view;





    }

    public void getReportData(){
        mCivilianDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Civilians").child(userId).child("Report details");
        mCivilianDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map  = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Date of report")!= null){
                        mReportDate.setText(map.get("Date of report").toString());
                    }

                    if(map.get("Reference code")!= null){
                        mReferenceCode.setText(map.get("Reference code").toString());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
