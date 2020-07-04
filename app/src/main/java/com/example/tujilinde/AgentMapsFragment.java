package com.example.tujilinde;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.tujilinde.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AgentMapsFragment extends Fragment implements OnMapReadyCallback{

    View mView;
    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Location mLastLocation;

    Marker locationMarker;

    private String civilianReporterId = "";
    private Random random;
    private int reference_number;
    private String currentTime;


    DatabaseReference assignedReporterLocation;
    private ValueEventListener assignedReporterLocationListener;

    private LinearLayout mCrimeAlertInfo;
    private TextView mCrimeTypeInfo, mCrimeDescriptionInfo;
    private Button mResponseBtn;


    public AgentMapsFragment(){
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_agent_maps, container, false);

        mCrimeAlertInfo = mView.findViewById(R.id.crimeAlertInfo);
        mCrimeTypeInfo = mView.findViewById(R.id.typeCrime);
        mCrimeDescriptionInfo = mView.findViewById(R.id.descriptionCrime);
        mResponseBtn = mView.findViewById(R.id.responseBtn);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.agent_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        getAssignedReporter();

        mResponseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordAlertResponse();
            }
        });

        return mView;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth() + -40, vectorDrawable.getIntrinsicHeight() + -40);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /*Get the id of the reporter mapped to the available security agent*/
    private void getAssignedReporter(){
        String agentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedReporterRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Security Agents").child(agentId).child("reporterId");
        assignedReporterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    civilianReporterId = dataSnapshot.getValue().toString();
                    getAssignedReporterLocation();
                    getAssignedReportInfo();
                }else {
                    civilianReporterId = "";
                    if (locationMarker != null){
                        locationMarker.remove();
                    }
                    if (assignedReporterLocationListener != null){
                        assignedReporterLocation.removeEventListener(assignedReporterLocationListener);
                    }
                    mCrimeAlertInfo.setVisibility(View.GONE);
                    mCrimeTypeInfo.setText("");
                    mCrimeDescriptionInfo.setText("");


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*Get the location of the civilian making the crime report*/
    private void getAssignedReporterLocation(){
        assignedReporterLocation = FirebaseDatabase.getInstance().getReference().child("crimeAlert").child(civilianReporterId).child("l");
        assignedReporterLocationListener = assignedReporterLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && !civilianReporterId.equals("")){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }

                    LatLng agentLatLng = new LatLng(locationLat, locationLng);
                    locationMarker = mMap.addMarker(new MarkerOptions().position(agentLatLng).title("Crime Reporter Location").icon(bitmapDescriptorFromVector(getContext(), R.mipmap.ic_civilian)));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getAssignedReportInfo(){
        DatabaseReference mCivilianDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Civilians").child(civilianReporterId).child("Report details");
        mCivilianDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    mCrimeAlertInfo.setVisibility(View.VISIBLE);
                    Map<String,Object> map  = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Crime Type")!= null){
                        mCrimeTypeInfo.setText(map.get("Crime Category").toString());
                    }
                    if(map.get("Crime Description")!= null){
                        mCrimeDescriptionInfo.setText(map.get("Crime Description").toString());
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(2000);
        locationRequest.setInterval(4000);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{
                callPermissions();
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);


    }




    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getContext()!=null){
                    mLastLocation = location;
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                }



                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("agentsAvailable");
                DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("agentsWorking");
                GeoFire geoFireAvailable = new GeoFire(refAvailable);
                GeoFire geoFireWorking = new GeoFire(refWorking);

                switch (civilianReporterId){
                    case "":
                        geoFireWorking.removeLocation(userId);
                        geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                        break;

                    default:
                        geoFireAvailable.removeLocation(userId);
                        geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                        break;

                }



            }

        }
    };



    public void callPermissions(){

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case 1:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else{
                    Toast.makeText(getContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    public void generateReferenceNumber(){
        random = new Random();
        reference_number = random.nextInt(10000);

    }

    public void generateCurrentResponseTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyy hh:mm:ss a");
        currentTime = simpleDateFormat.format(calendar.getTime());
    }

    /* record the history of crime alerts responded to by agent */
    public void recordAlertResponse(){
        generateReferenceNumber();
        generateCurrentResponseTime();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Security Agents").child(userId).child("responseHistory");
        DatabaseReference reporterRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Civilians").child(civilianReporterId).child("responseHistory");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("responseHistory");
        String responseId = historyRef.push().getKey();
        agentRef.child(responseId).setValue(true);
        reporterRef.child(responseId).setValue(true);


        HashMap map = new HashMap();
        map.put("Security Agent", userId);
        map.put("Civilian", civilianReporterId);
        map.put("Reference code", "RF" + reference_number);
        map.put("Response datetime", currentTime);
        historyRef.child(responseId).updateChildren(map);


    }


    @Override
    public void onStop() {
        super.onStop();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("agentsAvailable");

        GeoFire geoFire = new GeoFire(refAvailable);
        geoFire.removeLocation(userId);

    }

}
