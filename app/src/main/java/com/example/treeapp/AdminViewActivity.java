package com.example.treeapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminViewActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap sMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.aMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        sMap = googleMap;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Species");

        ValueEventListener valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot speciesKey : dataSnapshot.getChildren()) {
                        addMarker(speciesKey.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        DatabaseReference dataReference = FirebaseDatabase.getInstance().getReference().child("TreeReported");
        ValueEventListener eventListener = dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot treeReportedKey : dataSnapshot.getChildren()) {
                        addMarker(treeReportedKey.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Add a marker in Sydney and move the camera
        LatLng city = new LatLng(0.310894, 32.589024);
        sMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city, 12F));
    }

    private void addMarker(final String key) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Species").child(key);
        ValueEventListener valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = "";
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("SpeciesName").getValue() != null) {
                        name = dataSnapshot.child("SpeciesName").getValue().toString();
                    }
                    if (dataSnapshot.child("Lats").getValue() != null && dataSnapshot.child("Lngs").getValue() != null) {

                        Double latitude = Double.valueOf(dataSnapshot.child("Lats").getValue().toString());
                        Double longitude = Double.valueOf(dataSnapshot.child("Lngs").getValue().toString());
                        if (latitude != null && longitude != null) {
                            LatLng speciesLocation = new LatLng(latitude, longitude);

                            sMap.addMarker(new MarkerOptions().position(speciesLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.treeinfos)).title(name));
                            //sMap.moveCamera(CameraUpdateFactory.newLatLngZoom(treeLocation, 15F));

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference dataReference = FirebaseDatabase.getInstance().getReference().child("TreeReported").child(key);
        ValueEventListener eventListener = dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String sname = "";
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("TreeName").getValue() != null) {
                        sname = dataSnapshot.child("TreeName").getValue().toString();
                    }
                    if (dataSnapshot.child("Lat").getValue() != null && dataSnapshot.child("Lng").getValue() != null) {

                        Double slatitude = Double.valueOf(dataSnapshot.child("Lat").getValue().toString());
                        Double slongitude = Double.valueOf(dataSnapshot.child("Lng").getValue().toString());
                        if (slatitude != null && slongitude != null) {
                            LatLng newTreeLocation = new LatLng(slatitude, slongitude);

                            sMap.addMarker(new MarkerOptions().position(newTreeLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.treeinfoss)).title(sname));
                            //sMap.moveCamera(CameraUpdateFactory.newLatLngZoom(treeLocation, 15F));

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}