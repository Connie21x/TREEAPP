package com.example.treeapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileActivity extends AppCompatActivity {

    TextView userName, phone, email, mPlace;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    Button logoutBtn;
    ImageView catalogue, map, report;
    private static final String MY_PREF_FILENAME ="com.example.treeapp.category";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        phone = findViewById(R.id.phoneProfile);
        userName = findViewById(R.id.userNameProfile);
        email = findViewById(R.id.emailProfile);
        logoutBtn = findViewById(R.id.logout);
        catalogue = findViewById(R.id.onCatalogue);
        mPlace = findViewById(R.id.placeProfile);
        map = findViewById(R.id.onMap);
        report = findViewById(R.id.onReport);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        if (fAuth.getCurrentUser() != null) {
            userID = fAuth.getCurrentUser().getUid();
        }
        catalogue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor= getSharedPreferences(MY_PREF_FILENAME,MODE_PRIVATE).edit();
                editor.putString("category","");
                editor.apply();

                FirebaseAuth.getInstance().signOut();
                Toast.makeText(ProfileActivity.this, "Thanks You for Using the TREE APP", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
        if (fAuth.getCurrentUser() != null) {

            DocumentReference documentReference = fStore.collection("users").document(userID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    assert value != null;
                    phone.setText(value.getString("phone"));
                    userName.setText(value.getString("uName"));
                    email.setText(value.getString("email"));
                    mPlace.setText(value.getString("address"));
                }
            });
        }
    }
}