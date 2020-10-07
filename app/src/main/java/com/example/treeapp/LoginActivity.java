package com.example.treeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class LoginActivity extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mCreateBtn, forgotTextLink;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    CheckBox checkBox;
    String status="";
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBars);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = findViewById(R.id.createText);
        forgotTextLink = findViewById(R.id.forgotPassword);
        checkBox = findViewById(R.id.adminCheckBox);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mLoginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

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

                status = onCheckboxClicked(checkBox);
                progressBar.setVisibility(View.VISIBLE);

                //authenticate the user
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            userID = fAuth.getCurrentUser().getUid();
                            mPassword.setText("");
                            check(userID);

                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }



        });


         mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

         forgotTextLink.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 final EditText resetMail = new EditText(view.getContext());
                 AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                 passwordResetDialog.setTitle("Reset Password?");
                 passwordResetDialog.setMessage("Enter Your Email to Receive Reset Link.");
                 passwordResetDialog.setIcon(R.drawable.ic_flower);
                 passwordResetDialog.setView(resetMail);

                 passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                         String mail = resetMail.getText().toString();
                         fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void aVoid) {
                                 Toast.makeText(LoginActivity.this, "Reset Link Sent To Your Email", Toast.LENGTH_SHORT).show();
                             }
                         }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 Toast.makeText(LoginActivity.this, "Error! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                             }
                         });
                     }
                 });

                 passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {

                     }
                 });

                 passwordResetDialog.create().show();
             }
         });


    }
    private void check(String userID){
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value!=null && (value.getString("category")).equals("admin"))
                {
                    if (status.equals("Checked"))
                    {
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("type", "admin");
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "admin", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(LoginActivity.this, "Please check admin box", Toast.LENGTH_LONG).show();
                    }

                }
                else if (value!=null && (value.getString("category")).equals("user"))
                {
                    if (status.equals("Checked"))
                    {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(LoginActivity.this, "You are not a registered admin!!!", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("type", "user");
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "user", Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });

    }
    public String onCheckboxClicked (View view){
        boolean checked = ((CheckBox) view).isChecked();
        String adminCheck;
        if (checked){
            adminCheck= "Checked";
        }
        else{
            adminCheck= "Not Checked";
        }
        return  adminCheck;
    }
}