package com.example.tujilinde;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AgentMapsFragment extends Fragment implements OnMapReadyCallback{

    View mView;
    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Location mLastLocation;

    private String civilianReporterId = "";


    public AgentMapsFragment(){
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_agent_maps, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.agent_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync((OnMapReadyCallback) this);
        }


        getAssignedReporter();

        return mView;
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*Get the location of the civilian making the crime report*/
    private void getAssignedReporterLocation(){
        DatabaseReference assignedReporterLocation = FirebaseDatabase.getInstance().getReference().child("crimeAlert").child(civilianReporterId).child("l");
        assignedReporterLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
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
                    mMap.addMarker(new MarkerOptions().position(agentLatLng).title("Crime Reporter Location"));

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


    @Override
    public void onStop() {
        super.onStop();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("agentsAvailable");

        GeoFire geoFire = new GeoFire(refAvailable);
        geoFire.removeLocation(userId);

    }

}
