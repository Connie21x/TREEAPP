package com.example.treeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
    TextView textView;
    TextView mTextView;
    TextView oTextView;

    DatabaseReference ref;

    String DataName, ImageUrl, CommonName, FamilyOrigin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        imageView = findViewById(R.id.image_single_view_activity);
        textView = findViewById(R.id.textView_single_view_activity);
        mTextView = findViewById(R.id.textView_single_view_common_name_activity);
        oTextView = findViewById(R.id.textView_single_view_origin);
        ref = FirebaseDatabase.getInstance().getReference().child("Data");

        String DataKey = getIntent().getStringExtra("DataKey");

        ref.child(DataKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0)
                {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("DataName") != null){
                    DataName = map.get("DataName") .toString();
                    }
                    if (map.get("ImageUrl") != null){
                    ImageUrl = map.get("ImageUrl") .toString();
                    }
                    if (map.get("CommonName") != null){
                    CommonName = map.get("CommonName").toString();

                    }
                    if (map.get("FamilyOrigin") != null){
                    FamilyOrigin = map.get("FamilyOrigin").toString();
                    }

                    Picasso.get().load(ImageUrl).into(imageView);
                    textView.setText(DataName);
                    mTextView.setText(CommonName);
                    oTextView.setText(FamilyOrigin);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(ViewActivity.this, "Database Error", Toast.LENGTH_SHORT).show();

            }
        });
    }
}