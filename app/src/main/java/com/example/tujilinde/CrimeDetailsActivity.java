package com.example.tujilinde;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CrimeDetailsActivity extends AppCompatActivity {


    Spinner mCrimeCategory;
    Spinner mCrimeType;
    Spinner mReporterType;
    EditText mCrimeDesc;
    Button mDetailsBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference reportDetailsRef;

    private String userId;
    private String crime_category;
    private String crime_type;
    private String reporter_type;
    private String crime_description;
    private String currentTime;
    private Random random;
    private int reference_number;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_details);


        mCrimeCategory = findViewById(R.id.spinnerCrimeCategory);
        mCrimeType = findViewById(R.id.spinnerCrimeType);
        mReporterType = findViewById(R.id.spinnerReporterType);
        mCrimeDesc = findViewById(R.id.textCrimeDesc);
        mDetailsBtn = findViewById(R.id.crime_details_submit_btn);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCrimeDetails();
            }
        });

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyy hh:mm:ss a");
        currentTime = simpleDateFormat.format(calendar.getTime());

        List<String> crimeCat = new ArrayList<>();
        crimeCat.add(0, "Crime Category");
        crimeCat.add(1, "Personal Crime");
        crimeCat.add(2, "Property Crime");
        crimeCat.add(3, "Statutory Crime");
        crimeCat.add(4, "Financial Crime");

        ArrayAdapter<String> dataAdapter1;
        dataAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, crimeCat);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCrimeCategory.setAdapter(dataAdapter1);

        List<String> crimeType1 = new ArrayList<>();
        crimeType1.add(0, "Type of crime");
        crimeType1.add("Assault and battery");
        crimeType1.add("Child Abuse");
        crimeType1.add("Kidnapping");
        crimeType1.add("Arson");
        crimeType1.add("Rape");
        crimeType1.add("Murder/homicide");

        final ArrayAdapter<String> dataAdapter2;
        dataAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, crimeType1);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCrimeType.setAdapter(dataAdapter2);

        List<String> crimeType2 = new ArrayList<>();
        crimeType2.add(0, "Type of Crime");
        crimeType2.add("Burglary/housebreaking");
        crimeType2.add("Shoplifting");
        crimeType2.add("Pickpocketing");
        crimeType2.add("Larceny");

        final ArrayAdapter<String> dataAdapter3;
        dataAdapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, crimeType2);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mCrimeType.setAdapter(dataAdapter3);

        List<String> crimeType3 = new ArrayList<>();
        crimeType3.add(0, "Type of Crime");
        crimeType3.add("Drug trafficking");
        crimeType3.add("Drunk driving");
        crimeType3.add("Public Intoxication");
        crimeType3.add("Underage Intoxication");

        final ArrayAdapter<String> dataAdapter4;
        dataAdapter4 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, crimeType3);
        dataAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        List<String> crimeType4 = new ArrayList<>();
        crimeType4.add(0, "Type of Crime");
        crimeType4.add("Blackmail");
        crimeType4.add("Fraud");
        crimeType4.add("Money laundering");

        final ArrayAdapter<String> dataAdapter5;
        dataAdapter5 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, crimeType4);
        dataAdapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        List<String> reporterType = new ArrayList<>();
        reporterType.add(0, "Reporting as a");
        reporterType.add("Victim");
        reporterType.add("Witness");

        ArrayAdapter<String> dataAdapter6;
        dataAdapter6 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reporterType);
        dataAdapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mReporterType.setAdapter(dataAdapter6);

        mCrimeCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Crime Category")){
                    mCrimeType.setPrompt("You need to choose a selection");
                }
                else if(parent.getItemAtPosition(position).equals("Personal Crime")){

                    crime_category = parent.getItemAtPosition(position).toString();
                    mCrimeType.setAdapter(dataAdapter2);
                }
                else if(parent.getItemAtPosition(position).equals("Property Crime")){

                    crime_category = parent.getItemAtPosition(position).toString();
                    mCrimeType.setAdapter(dataAdapter3);
                }
                else if(parent.getItemAtPosition(position).equals("Statutory Crime")){

                    crime_category = parent.getItemAtPosition(position).toString();
                    mCrimeType.setAdapter(dataAdapter4);
                }
                else if(parent.getItemAtPosition(position).equals("Financial Crime")){

                    crime_category = parent.getItemAtPosition(position).toString();
                    mCrimeType.setAdapter(dataAdapter5);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCrimeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Type of Crime")){
                    // do nothing
                }
                else{
                    crime_type = parent.getItemAtPosition(position).toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mReporterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Reporting as a")){
                    // do nothing
                }

                else{
                    reporter_type = parent.getItemAtPosition(position).toString();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getCrimeReportDetails();



    }

    public void generateReferenceNumber(){
        random = new Random();
        reference_number = random.nextInt(10000);

    }


    public void getCrimeReportDetails(){
        reportDetailsRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Civilians").child(userId).child("Report details");
        reportDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0 ){
                    Map <String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Crime Category") != null){
                        crime_category = map.get("Crime Category").toString();

                    }
                    if(map.get("Crime Type") != null){
                        crime_type = map.get("Crime Type").toString();

                    }
                    if(map.get("Reporter") != null){
                        reporter_type = map.get("Reporter").toString();

                    }
                    if(map.get("Crime Description") != null){
                        crime_description = map.get("Crime Description").toString();
                        mCrimeDesc.setText(crime_description);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void saveCrimeDetails(){
        generateReferenceNumber();
        crime_description = mCrimeDesc.getText().toString();

        if (crime_description.isEmpty()){
            mCrimeDesc.setError("This field is required");
        }
        Map crimeDetails = new HashMap();
        crimeDetails.put("Crime Category", crime_category);
        crimeDetails.put("Crime Type", crime_type);
        crimeDetails.put("Reporter", reporter_type);
        crimeDetails.put("Crime Description", crime_description);
        crimeDetails.put("Date of report", currentTime);
        crimeDetails.put("Reference code", "RF" + reference_number);
        reportDetailsRef.updateChildren(crimeDetails);

        finish();


    }

}
