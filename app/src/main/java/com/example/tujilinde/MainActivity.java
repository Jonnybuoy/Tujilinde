package com.example.tujilinde;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Button homeRegister;
    private DatabaseReference current_user_db;
    private String user_id;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        current_user_db = FirebaseDatabase.getInstance().getReference().child("Users");



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mAuth = FirebaseAuth.getInstance();
            user_id = mAuth.getCurrentUser().getUid();
            // User is signed in
            current_user_db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child("Security Agents").child(user_id).exists()){
                        startActivity(new Intent(MainActivity.this, AgentNavigationActivity.class));

                    }else if (dataSnapshot.child("Civilians").child(user_id).exists()){

                        startActivity(new Intent(MainActivity.this, CivilianNavigationActivity.class));
                    }else {
                        Toast.makeText(MainActivity.this, "User Type not saved", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else {
            // No user is signed in
        }

        homeRegister = findViewById(R.id.homeRegbtn);

        homeRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserTypeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
