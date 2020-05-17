package com.example.tujilinde;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class CivilianRegisterActivity extends AppCompatActivity {
    EditText mPhone;
    Button mRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_civilian_register);

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


                Intent intent = new Intent(CivilianRegisterActivity.this, CivilianVerifyPhoneActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);

            }
        });
    }
}


//package com.example.tujilinde;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class CivilianRegisterActivity extends AppCompatActivity {
//    EditText mEmail, mPassword;
//    Button mRegister;
//
//    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener firebaseAuthListener;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//
//        mAuth = FirebaseAuth.getInstance();
//
//        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if(user!=null){
//                    Intent intent = new Intent(CivilianRegisterActivity.this, CivilianMapsActivity.class);
//                    startActivity(intent);
//                    finish();
//                    return;
//                }
//            }
//        };
//
//        mEmail = findViewById(R.id.emailText);
//        mPassword = findViewById(R.id.passwordText);
//        mRegister = findViewById(R.id.registerButton);
//
//        mRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String emailText = mEmail.getText().toString();
//                final String passwordText = mPassword.getText().toString();
//
//                if (!emailText.isEmpty() && !passwordText.isEmpty()) {
//
//
//                    mAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(CivilianRegisterActivity.this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//
//                            if (!task.isSuccessful()) {
//                                Toast.makeText(CivilianRegisterActivity.this, "sign up error", Toast.LENGTH_SHORT).show();
//
////                                Toast.makeText(CivilianRegisterActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
////                                String user_id = mAuth.getCurrentUser().getUid();
////                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Civilians").child(user_id);
////                                current_user_db.setValue(true);
//
//                            } else {
////                                Toast.makeText(CivilianRegisterActivity.this, "sign up error", Toast.LENGTH_SHORT).show();
//                                String user_id = mAuth.getCurrentUser().getUid();
//                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Civilians").child(user_id);
//                                current_user_db.setValue(true);
//                                Toast.makeText(CivilianRegisterActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//
//                }else {
//                    Toast.makeText(CivilianRegisterActivity.this, "Email or password empty", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(firebaseAuthListener);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mAuth.removeAuthStateListener(firebaseAuthListener);
//    }
//}
