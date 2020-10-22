package com.example.treeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mUserName, mEmail, mPassword, mPhone, mAddress;
    Button mRegisterBtn;
    TextView loginTv;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;

    private static final String MY_PREF_FILENAME ="com.example.treeapp.category";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUserName = findViewById(R.id.userName);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.phone);
        mRegisterBtn = findViewById(R.id.registerBtn);
        loginTv = findViewById(R.id.createText);
        mAddress = findViewById(R.id.address);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBars);

        SharedPreferences preferences =getSharedPreferences(MY_PREF_FILENAME,MODE_PRIVATE);
        String category = preferences.getString("category","");


        if (fAuth.getCurrentUser() != null){
            if (category.equals("admin")){

            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("type","admin");
            startActivity(intent);
            }
            else {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("type","user");
                startActivity(intent);
            }

            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                hideKeyboard(RegisterActivity.this);
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String userName = mUserName.getText().toString();
                final String phone = mPhone.getText().toString();
                final String address = mAddress.getText().toString();


                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required!");
                    return;
                }
                if (password.length() <6){
                    mPassword.setError("Password Must be >= 6 Characters!");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "User Created!", Toast.LENGTH_SHORT).show();

                            mEmail.setText("");
                            mPassword.setText("");
                            mUserName.setText("");
                            mPhone.setText("");
                            mAddress.setText("");

                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("uName", userName);
                            user.put("email", email);
                            user.put("phone", phone);
                            user.put("address", address);
                            user.put("category", "user");
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: User Profile is Created for" + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
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