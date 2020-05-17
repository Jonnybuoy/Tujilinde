package com.example.tujilinde;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class CivilianNameHeader extends Fragment {

    View view;
    TextView mHeaderName;

    FirebaseAuth mAuth;
    DatabaseReference mCivilianDatabase;

    String userId;

    public CivilianNameHeader(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.nav_header, container, false);

        mHeaderName = view.findViewById(R.id.textHeaderName);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mCivilianDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Civilians").child(userId).child("Profile Details");
        mCivilianDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map  = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Name")!= null){
                        mHeaderName.setText(map.get("Name").toString());
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
}
