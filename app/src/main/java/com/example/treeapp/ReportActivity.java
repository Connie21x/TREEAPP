package com.example.treeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.type.LatLng;

import java.util.HashMap;

public class ReportActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_IMAGES = 102;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        addTreeImage = findViewById(R.id.imageNewTree);
        addTreeName = findViewById(R.id.inputTreeName);
        addTreeHeight = findViewById(R.id.inputExtHeight);
        addTreeHealth = findViewById(R.id.inputExtHealth);

        uploadTree = findViewById(R.id.btnUploadTree);

        Lat = findViewById(R.id.latTv);
        Lng = findViewById(R.id.lngTv);

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
        }else{
            Toast.makeText(this, "Press Button in Map Activity to Set Lat Lng", Toast.LENGTH_SHORT).show();
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
                    } else {
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
    }
}