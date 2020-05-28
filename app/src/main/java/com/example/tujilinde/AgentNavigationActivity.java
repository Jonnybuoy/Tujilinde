package com.example.tujilinde;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class AgentNavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toogle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_navigation);

        changeFragment();


        drawerLayout = findViewById(R.id.agentDrawer);
        toolbar = findViewById(R.id.agent_toolbar);
        navigationView = findViewById(R.id.agentNavigationView);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.reportHistory);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toogle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawerOpen,R.string.drawerClose);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();





    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.agent_fragment_container,
                        new AgentProfileFragment()).commit();
                break;
            case R.id.receivedCrimes:
                getSupportFragmentManager().beginTransaction().replace(R.id.agent_fragment_container,
                        new AgentReceivedReportsFragment()).commit();
                break;
            case R.id.about:
                getSupportFragmentManager().beginTransaction().replace(R.id.agent_fragment_container,
                        new AboutFragment()).commit();
                break;
            case R.id.contact:
                getSupportFragmentManager().beginTransaction().replace(R.id.agent_fragment_container,
                        new CivilianContactFragment()).commit();                break;

            case R.id.securityPin:
                getSupportFragmentManager().beginTransaction().replace(R.id.agent_fragment_container,
                        new SetPasscodeFragment()).commit();                break;

            case R.id.logoff:
                getSupportFragmentManager().beginTransaction().replace(R.id.agent_fragment_container,
                        new SignOutFragment()).commit();                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeFragment () {

        FragmentTransaction transaction;
        transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.agent_fragment_container, new AgentMapsFragment());
        transaction.commit();


    }
}
