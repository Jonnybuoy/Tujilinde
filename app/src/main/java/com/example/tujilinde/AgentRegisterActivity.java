package com.example.tujilinde;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AgentRegisterActivity extends AppCompatActivity {
    EditText mPhone;
    Button mRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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


                Intent intent = new Intent(AgentRegisterActivity.this, AgentVerifyPhoneActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);

            }
        });
    }
}
