package com.example.treeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE_IMAGE = 101 ;
    private GoogleMap mMap;
    private final int LOCATION_PERMISSION_CODE = 4643;
    private final int SETTINGS_PERMISSION_CODE = 2823;

    private ImageView imageViewAdd;
    private EditText inputImageName;
    private EditText inputImageCommonName;
    private TextView textViewProgress;
    private ProgressBar progressBar;
    private Button btnUpload;

    TextView Lat, Lng;

    String latitude;
    String longitude;

    Uri imageUri;
    boolean isImageAdded = false;
    String imageName,imageCommonName;

    DatabaseReference Dataref;
    StorageReference StorageRef;

    LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewAdd = findViewById(R.id.imageViewAdd);
        inputImageName = findViewById(R.id.inputImageName);
        inputImageCommonName = findViewById(R.id.inputImageCommonName);
        textViewProgress = findViewById(R.id.textViewProgress);
        progressBar = findViewById(R.id.progressBar);
        btnUpload = findViewById(R.id.btnUpload);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        locationRequest = LocationRequest.create();

        SupportMapFragment sMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.reportSpeciesMap);
        sMapFragment.getMapAsync(this);

        Lat = findViewById(R.id.latSpeciesTv);
        Lng = findViewById(R.id.lngSpeciesTv);

        textViewProgress.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        Dataref = FirebaseDatabase.getInstance().getReference().child("Species");
        StorageRef = FirebaseStorage.getInstance().getReference().child("SpeciesImage");

        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(MainActivity.this);

                imageName = inputImageName.getText().toString();
                imageCommonName = inputImageCommonName.getText().toString();
                latitude = Lat.getText().toString();
                longitude = Lng.getText().toString();
                if (!imageName.isEmpty() && !imageCommonName.isEmpty() && !latitude.isEmpty() && !longitude.isEmpty()) {
                    if (isImageAdded != false) {
                        uploadImage(imageName,imageCommonName, latitude, longitude);
                        inputImageName.setText("");
                        inputImageCommonName.setText("");
                        imageViewAdd.setImageResource(R.drawable.add_a_photo);
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                    } else {
                        Toast.makeText(MainActivity.this, "Insert Image", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

        private void uploadImage(final String imageName, final String imageCommonName, final String latitude, final String longitude ) {
        textViewProgress.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
            final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String key = Dataref.push().getKey();
        StorageRef.child(key + ".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                StorageRef.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("SpeciesName", imageName);
                        hashMap.put("CommonName", imageCommonName);
                        hashMap.put("Lats", latitude);
                        hashMap.put("Lngs", longitude);
                        hashMap.put("ImageUrl", uri.toString());
                        hashMap.put("userID", userID);

                        Dataref.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                Toast.makeText(MainActivity.this, "Species Succesfully Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress=(taskSnapshot.getBytesTransferred()*100)/taskSnapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
                textViewProgress.setText(progress + " %");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE_IMAGE && data!=null)
        {
            imageUri = data.getData();
            isImageAdded = true;
            imageViewAdd.setImageURI(imageUri);
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
        SettingsClient settingsClient = LocationServices.getSettingsClient(MainActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getUSerLocation();
            }
        });
        task.addOnFailureListener(MainActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(MainActivity.this, SETTINGS_PERMISSION_CODE);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }

    private void getUSerLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        } else { fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
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
                                Toast.makeText(MainActivity.this, "Can't Locate You", Toast.LENGTH_SHORT).show();
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
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Has Been Granted!", Toast.LENGTH_SHORT).show();
                requestPermission();
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("This is an Important Permission:");
                    dialog.setMessage("This Permission is Required to Update your Location");
                    dialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);

                        }
                    });
                    dialog.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "Cant Find Your Location", Toast.LENGTH_SHORT).show();

                        }
                    });
                    dialog.show();
                } else {
                    Toast.makeText(this, "Cant Find Your Location", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }
}