package com.example.tujilinde;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class SignInActivity extends AppCompatActivity {
    EditText mPhone;
    Button mRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in2);

        mPhone = findViewById(R.id.civphoneNumberText);
        mRegister = findViewById(R.id.civregisterButton);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mobile = mPhone.getText().toString().trim();

                if (mobile.isEmpty() || mobile.length() < 10) {

                    mPhone.setError("Enter a valid mobile number");
                    mPhone.requestFocus();
                    return;
                }


                Intent intent = new Intent(SignInActivity.this, SignInVerifyPhoneActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);


            }
        });
    }
}