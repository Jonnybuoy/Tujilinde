package com.example.tujilinde;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SetPasscodeFragment extends Fragment {

    View mView;
    EditText mPasscode;
    Button mSave;
    TextView mReturn;

    private DatabaseReference current_user_db;
    private DatabaseReference agentProfileRef;
    private DatabaseReference civilianProfileRef;
    FirebaseAuth mAuth;

    String user_id;
    String pass_code;
    String hashed_passcode;

    public SetPasscodeFragment(){
        // empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_passcode, container, false);

        mPasscode = mView.findViewById(R.id.editPasscode);
        mSave = mView.findViewById(R.id.savePasscodeBtn);
        mReturn =  mView.findViewById(R.id.textView);

        current_user_db = FirebaseDatabase.getInstance().getReference().child("Users");

        getPassCode();

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                computeHashPassword(mPasscode.toString());
                savePassCode();
            }
        });

        return mView;
    }

    private void getPassCode(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mAuth = FirebaseAuth.getInstance();
            user_id = mAuth.getCurrentUser().getUid();
            agentProfileRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Security Agents").child(user_id).child("Profile Details");
            civilianProfileRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Civilians").child(user_id).child("Profile Details");
            // User is signed in
            current_user_db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child("Security Agents").child(user_id).exists()){
                        agentProfileRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                                    Map<String,Object> map  = (Map<String, Object>) dataSnapshot.getValue();
                                    if(map.get("PassCode")!= null){
                                        hashed_passcode = map.get("PassCode").toString();
                                        mReturn.setText(hashed_passcode);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }else if (dataSnapshot.child("Civilians").child(user_id).exists()){

                        civilianProfileRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                                    Map<String,Object> map  = (Map<String, Object>) dataSnapshot.getValue();
                                    if(map.get("PassCode")!= null){
                                        hashed_passcode = map.get("PassCode").toString();
                                        mReturn.setText(hashed_passcode);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }else {
                        Toast.makeText(getActivity(), "User Type not saved", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else {
            // No user is signed in
        }


    }

    public void computeHashPassword(String pass_code){

        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(pass_code.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++){
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2){
                    h = "0" + h;
                    MD5Hash.append(h);
                }
            }


            mReturn.setText(MD5Hash);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void savePassCode(){

        pass_code = mPasscode.getText().toString();
        hashed_passcode = mReturn.getText().toString();

        if (pass_code.isEmpty()){
            mPasscode.setError("This field is required!");
            return;
        }

        Map userPin = new HashMap();
        userPin.put("PassCode", hashed_passcode);
        agentProfileRef.updateChildren(userPin);
        civilianProfileRef.updateChildren(userPin);
        getActivity().finish();
        Toast.makeText(getActivity(), "Passcode set successfully!", Toast.LENGTH_SHORT).show();



    }
}
