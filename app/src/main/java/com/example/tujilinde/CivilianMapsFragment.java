package com.example.tujilinde;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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

import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CivilianMapsFragment extends Fragment implements OnMapReadyCallback {

    private View mView;
    private GoogleMap mMap;


    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Location mLastLocation;
    private LatLng alertLocation;
    GeoQuery geoQuery;
    private DatabaseReference agentLocationRef;
    private ValueEventListener agentLocationRefListener;
    private Marker locationMarker;



    private Button mReportBtn, mCancelBtn;

    private int radius = 1;
    private Boolean agentFound = false;
    private String agentFoundID;
    private Boolean requestBol = false;

    public CivilianMapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_civilian_maps, container, false);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        mReportBtn = mView.findViewById(R.id.reportBtn);
//        mCancelBtn = mView.findViewById(R.id.cancelReportBtn);

        mReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestBol){
                    requestBol = false;
                    geoQuery.removeAllListeners();
                    agentLocationRef.removeEventListener(agentLocationRefListener);

                    if (agentFoundID != null){
                        DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Security Agents").child(agentFoundID).child("reporterId");
                        agentRef.removeValue();
                        agentFoundID = null;
                    }

                    agentFound = false;
                    radius = 1;

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("crimeAlert");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.removeLocation(userId);

                    if (locationMarker != null){
                        locationMarker.remove();
                    }
                    if (mAgentMarker != null) {
                        mAgentMarker.remove();
                    }
                    mReportBtn.setText("REPORT A CRIME");
                }else {
                    requestBol = true;
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("crimeAlert");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                    alertLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    locationMarker = mMap.addMarker(new MarkerOptions().position(alertLocation).title("My location").icon(bitmapDescriptorFromVector(getContext(), R.mipmap.ic_civilian)));

                    mReportBtn.setText("ALERT NOW IN PROGRESS...");

//                    Intent intent = new Intent(getActivity(), CrimeDetailsActivity.class);
//
//                    startActivity(intent);


                    getClosestAgents();
                }

            }
        });

//        mCancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (requestBol){
//                    requestBol = false;
//                    geoQuery.removeAllListeners();
//                    agentLocationRef.removeEventListener(agentLocationRefListener);
//
//                    if (agentFoundID != null){
//                        DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Security Agents").child(agentFoundID);
//                        agentRef.setValue(true);
//                        agentFoundID = null;
//                    }
//
//                    agentFound = false;
//                    radius = 1;
//
//                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("crimeAlert");
//                    GeoFire geoFire = new GeoFire(ref);
//                    geoFire.removeLocation(userId);
//
//                    if (locationMarker != null){
//                        locationMarker.remove();
//                    }
//                    mReportBtn.setText("REPORT A CRIME");
//                }
//
//            }
//        });

        return mView;
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth() + -60, vectorDrawable.getIntrinsicHeight() + -60);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    /*Check for security agents within the radius of the civilian making the report*/
    private void getClosestAgents(){
        DatabaseReference agentLocation = FirebaseDatabase.getInstance().getReference().child("agentsAvailable");

        GeoFire geoFire = new GeoFire(agentLocation);

        geoQuery = geoFire.queryAtLocation(new GeoLocation(alertLocation.latitude, alertLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            /*when a security agent near location is found, set agentFound to true and get their key id*/
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!agentFound && requestBol){
                    agentFound = true;
                    agentFoundID = key;


                    // map found security agent to reporter to attend to
                    DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Security Agents").child(agentFoundID);
                    String civilianReporterId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HashMap map = new HashMap();
                    map.put("reporterId", civilianReporterId);
                    agentRef.updateChildren(map);

                    getAgentLocation();
                    mReportBtn.setText("Looking for Security Agent location...");

                }
//                Intent intent = new Intent(getActivity(), CrimeDetailsActivity.class);
//
//                startActivity(intent);

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!agentFound){
                    radius++;
                    getClosestAgents();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    // Alert reporter on agent location
    private Marker mAgentMarker;
    private void getAgentLocation(){
        agentLocationRef = FirebaseDatabase.getInstance().getReference().child("agentsWorking").child(agentFoundID).child("l");
        agentLocationRefListener = agentLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol){
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
                    if(mAgentMarker != null){
                        mAgentMarker.remove();
                    }

                    Location loc1 = new Location("");
                    loc1.setLatitude(alertLocation.latitude);
                    loc1.setLongitude(alertLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(agentLatLng.latitude);
                    loc2.setLongitude(agentLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);

                    if (distance < 100){
                        mReportBtn.setText("Security Agent has arrived");
                    }
                    else {
                        mReportBtn.setText("Security Agent Found: " + (distance));
                        Intent intent = new Intent(getActivity(), CrimeDetailsActivity.class);

                        startActivity(intent);
                    }


                    mAgentMarker = mMap.addMarker(new MarkerOptions().position(agentLatLng).title("Available Agent").icon(bitmapDescriptorFromVector(getContext(), R.mipmap.ic_agent)));





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

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(2000);
        locationRequest.setInterval(4000);




        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{

                checkLocationPermission();

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


            }

        }
    };



    private void checkLocationPermission(){

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getContext())
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
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
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
}