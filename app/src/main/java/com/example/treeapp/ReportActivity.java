package com.example.treeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ReportActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE_IMAGES = 102;
    private GoogleMap mMap;
    private final int LOCATION_PERMISSION_CODE = 464;
    private final int SETTINGS_PERMISSION_CODE = 282;

    ImageView addTreeImage;
    EditText addTreeName, addTreeHeight, addTreeHealth;
    Button uploadTree;
    TextView Lat, Lng;
    String latitude;
    String longitude;

    Uri imageUri;
    boolean isTreeImageAdded = false;
    String treeName,treeHeight, treeHealth;

    DatabaseReference databaseReference;
    StorageReference storageReference;

    LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        addTreeImage = findViewById(R.id.imageNewTree);
        addTreeName = findViewById(R.id.inputTreeName);
        addTreeHeight = findViewById(R.id.inputExtHeight);
        addTreeHealth = findViewById(R.id.inputExtHealth);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ReportActivity.this);
        locationRequest = LocationRequest.create();

        uploadTree = findViewById(R.id.btnUploadTree);

        Lat = findViewById(R.id.latTv);
        Lng = findViewById(R.id.lngTv);

        SupportMapFragment tMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.reportTreeMap);
        tMapFragment.getMapAsync(this);

        latitude = getIntent().getStringExtra("lat");
        longitude = getIntent().getStringExtra("lng");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("TreeReported");
        storageReference = FirebaseStorage.getInstance().getReference().child("TreeImage");

        addTreeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGES);
            }
        });

        if (latitude != null && longitude != null){
            Lat.setText(latitude);
            Lng.setText(longitude);
        }

        uploadTree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                treeName = addTreeName.getText().toString();
                treeHeight = addTreeHeight.getText().toString();
                treeHealth = addTreeHealth.getText().toString();
                latitude = Lat.getText().toString();
                longitude = Lng.getText().toString();

                if (!treeName.isEmpty() && !treeHeight.isEmpty() && !treeHealth.isEmpty() && !latitude.isEmpty() && !longitude.isEmpty()) {
                    if (isTreeImageAdded != false) {
                        uploadImage(treeName, treeHeight, treeHealth, latitude, longitude);
                    }
                    else {
                        Toast.makeText(ReportActivity.this, "Insert Image", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(ReportActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Initialize and Assign Variables
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.report);

        //Perform Item Selected Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.map:
                        startActivity(new Intent(getApplicationContext()
                                , MapActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.catalogue:
                        startActivity(new Intent(getApplicationContext()
                                , HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.report:
                        return true;
                }

                return false;
            }

        });

    }

    private void uploadImage(final String treeName, final String treeHeight, final String treeHealth, final String latitude, final String longitude) {

        final String key = databaseReference.push().getKey();
        storageReference.child(key + ".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("TreeName", treeName);
                        hashMap.put("TreeHeight", treeHeight);
                        hashMap.put("TreeHealth", treeHealth);
                        hashMap.put("Lat", latitude);
                        hashMap.put("Lng", longitude);
                        hashMap.put("TreeImageUrl", uri.toString());

                        databaseReference.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ReportActivity.this, "Data Uploaded, We Will Get To You Shortly", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE_IMAGES && data!=null)
        {
            imageUri = data.getData();
            isTreeImageAdded = true;
            addTreeImage.setImageURI(imageUri);
        }
        if (requestCode == SETTINGS_PERMISSION_CODE) {
            if (resultCode == RESULT_OK) {
                getUSerLocation();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (latitude != null && longitude != null){
            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude))).title("Your Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 15F));
        }
        else{
            LatLng CityHall = new LatLng(0.314931, 32.586236);
            CameraPosition position = new CameraPosition.Builder().target(CityHall).zoom(12).bearing(0).tilt(0).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
            requestPermission();
        }
    }

    protected void locationRequest() {

        locationRequest.setInterval(20000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void changeSettings() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(ReportActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(ReportActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getUSerLocation();
            }
        });
        task.addOnFailureListener(ReportActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(ReportActivity.this, SETTINGS_PERMISSION_CODE);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }

    private void getUSerLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        } else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                mLastLocation = task.getResult();
                                if (mLastLocation != null) {
                                    CameraPosition position = new CameraPosition.Builder().target(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude())).zoom(15).bearing(0).tilt(0).build();
                                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
                                        String lat = String.valueOf((mLastLocation.getLatitude()));
                                        String lng = String.valueOf((mLastLocation.getLongitude()));
                                        Lat.setText(lat);
                                        Lng.setText(lng);
                                        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))).title("Your Location"));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), 15F));

                                } else {
                                    locationRequest();
                                    changeSettings();
                                }

                            } else {
                                Toast.makeText(ReportActivity.this, "Can't  get your location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            locationRequest();

            changeSettings();
        } else {
            ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Has Been Granted!", Toast.LENGTH_SHORT).show();
                requestPermission();
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(ReportActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("This is an Important Permission:");
                    dialog.setMessage("This Permission is Required to Update your Location");
                    dialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);

                        }
                    });
                    dialog.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ReportActivity.this, "Cant Find Your Location", Toast.LENGTH_SHORT).show();

                        }
                    });
                    dialog.show();
                } else {
                    Toast.makeText(this, "Cant Find Your Location", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }
}