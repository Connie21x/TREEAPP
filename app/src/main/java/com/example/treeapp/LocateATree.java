package com.example.treeapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.Toast;

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

public class LocateATree extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String treeNameKey;
    private String treeSpecies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_a_tree);

        treeNameKey = getIntent().getStringExtra("name");
        treeSpecies = getIntent().getStringExtra("species");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Data").child(treeNameKey);
        ValueEventListener listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = "";
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.child("DataName").getValue() != null) {
                        name = dataSnapshot.child("DataName").getValue().toString();
                    }

                    if (dataSnapshot.child("Lato").getValue() != null && dataSnapshot.child("Lngo").getValue() != null) {

                        Double latitude = Double.valueOf(dataSnapshot.child("Lato").getValue().toString());
                        Double longitude = Double.valueOf(dataSnapshot.child("Lngo").getValue().toString());
                        if (latitude != null && longitude != null) {
                            LatLng treeLocation = new LatLng(latitude, longitude);

                            if (name.equals("Anacardium occidentale") || name.equals("Alstonia boonei")) {
                                mMap.addMarker(new MarkerOptions().position(treeLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.treeinfoss)).title(name));
                            }
                            else {
                                mMap.addMarker(new MarkerOptions().position(treeLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.treeinfos)).title(name));
                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(treeLocation, 15F));
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