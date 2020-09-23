package com.example.treeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.EventLogTags;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class ViewActivity extends AppCompatActivity {

    private ImageView imageView;

    TextView textView, mTextView, oTextView, dTextView, uTextView, pTextView, mmTextView;

    DatabaseReference ref;

    CardView card_common_name, card_origin, descriptionCard, usesCard, propagationCard, managementCard;

    String DataName, ImageUrl, CommonName, FamilyOrigin, Description, Uses, Propagation, Management;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        imageView = findViewById(R.id.image_single_view_activity);
        textView = findViewById(R.id.textView_single_view_activity);
        mTextView = findViewById(R.id.textView_single_view_common_name_activity);
        oTextView = findViewById(R.id.textView_single_view_origin);

        dTextView = findViewById(R.id.descriptionTextView);
        uTextView = findViewById(R.id.usesTextView);
        pTextView = findViewById(R.id.propagationTextView);
        mmTextView = findViewById(R.id.managementTextView);

        card_common_name = findViewById(R.id.card_common_name);
        card_origin = findViewById(R.id.card_origin);

        descriptionCard = findViewById(R.id.descriptionCard);
        propagationCard = findViewById(R.id.propagationCard);
        usesCard = findViewById(R.id.usesCard);
        managementCard = findViewById(R.id.managementCard);

        ref = FirebaseDatabase.getInstance().getReference().child("Data");

        String DataKey = getIntent().getStringExtra("DataKey");

        ref.child(DataKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("DataName") != null) {
                        DataName = map.get("DataName").toString();
                        textView.setText(DataName);
                    }
                    if (map.get("ImageUrl") != null) {
                        ImageUrl = map.get("ImageUrl").toString();
                        Picasso.get().load(ImageUrl).into(imageView);
                    }
                    if (map.get("CommonName") != null) {
                        CommonName = map.get("CommonName").toString();
                        mTextView.setText(CommonName);
                        card_common_name.setVisibility(View.VISIBLE);
                    }
                    if (map.get("FamilyOrigin") != null) {
                        FamilyOrigin = map.get("FamilyOrigin").toString();
                        oTextView.setText(FamilyOrigin);
                        card_origin.setVisibility(View.VISIBLE);
                    }
                    if (map.get("Description") != null) {
                        Description = map.get("Description").toString();
                        dTextView.setText(Description);
                        descriptionCard.setVisibility(View.VISIBLE);
                    }
                    if (map.get("Uses") != null) {
                        Uses = map.get("Uses").toString();
                        uTextView.setText(Uses);
                        usesCard.setVisibility(View.VISIBLE);
                    }
                    if (map.get("Propagation") != null) {
                        Propagation = map.get("Propagation").toString();
                        pTextView.setText(Propagation);
                        propagationCard.setVisibility(View.VISIBLE);
                    }
                    if (map.get("Management") != null) {
                        Management = map.get("Management").toString();
                        mmTextView.setText(Management);
                        managementCard.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(ViewActivity.this, "Database Error", Toast.LENGTH_SHORT).show();

            }
        });
    }
}