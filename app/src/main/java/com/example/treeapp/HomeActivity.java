package com.example.treeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    private String editBtn;

    EditText inputSearch;
    RecyclerView recyclerView;
    FloatingActionButton floatingbtn, floatingActionButton;
    FirebaseRecyclerOptions<Data> options;
    FirebaseRecyclerAdapter<Data, MyViewHolder> adapter;
    DatabaseReference Dataref;
    ImageView imageViewButton;

    private static final String MY_PREF_FILENAME ="com.example.treeapp.category";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences preferences =getSharedPreferences(MY_PREF_FILENAME,MODE_PRIVATE);
        String category = preferences.getString("category","");

        //Initialize and Assign Variables
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.catalogue);

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
                        return true;

                    case R.id.report:
                        startActivity(new Intent(getApplicationContext()
                                , ReportActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }

                return false;
            }
        });

        Dataref = FirebaseDatabase.getInstance().getReference().child("Data");
        inputSearch = findViewById(R.id.inputSearch);
        recyclerView = findViewById(R.id.recyclerView);
        floatingbtn = findViewById(R.id.floatingbtn);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        imageViewButton = findViewById(R.id.editButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        editBtn = getIntent().getStringExtra("type");
        if ((editBtn !=null && editBtn.equals("admin"))||category.equals("admin")){
            imageViewButton.setVisibility(View.VISIBLE);
        }




        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext (), ProfileActivity.class));
            }
        });

        floatingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        LoadData("");

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString()!=null)
                {
                    LoadData(editable.toString());
                }
                else
                {
                    LoadData("");
                }
            }
        });

        imageViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AdminViewActivity.class);
                startActivity(intent);
            }
        });
    }


    //Logout method
    public void logout (View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    private void LoadData(String data) {

        Query query = Dataref.orderByChild("DataName").startAt(data).endAt(data + "\uf8ff");

        options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(query, Data.class).build();
        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull Data model) {
                holder.textView.setText(model.getDataName());
                holder.mTextView.setText(model.getCommonName());
                Picasso.get().load(model.getImageUrl()).into(holder.imageView);
                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this, ViewActivity.class);
                        intent.putExtra("DataKey", getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view, parent, false);
                return new MyViewHolder(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}