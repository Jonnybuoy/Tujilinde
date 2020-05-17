package com.example.tujilinde;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class UserTypeActivity extends AppCompatActivity {
    private Button agent, civilian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usertype);

        agent = findViewById(R.id.agentButton);
        civilian = findViewById(R.id.civilianButton);

        agent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserTypeActivity.this, AgentRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        civilian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserTypeActivity.this, CivilianRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
