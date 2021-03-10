package com.app.triponeer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    Button btnSignup;
    EditText edtTextSignupName, edtTextSignupEmail, edtTextSignupPassword, edtTextSignupConfirmPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    String email;
    String password;
    String name;
    NormalUser normalUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        edtTextSignupName = findViewById(R.id.edtTextSignupName);
        edtTextSignupEmail = findViewById(R.id.edtTextSignupEmail);
        edtTextSignupPassword = findViewById(R.id.edtTextSignupPassword);
        edtTextSignupConfirmPassword = findViewById(R.id.edtTextSignupConfirmPassword);
        btnSignup = findViewById(R.id.btnSignup);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        name = "";
        email = "";
        password = "";
        normalUser = NormalUser.getInstance();
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTextSignupName.getText().toString().isEmpty() ||
                        edtTextSignupEmail.getText().toString().isEmpty() ||
                        edtTextSignupPassword.getText().toString().isEmpty() ||
                        edtTextSignupConfirmPassword.getText().toString().isEmpty()
                ) {
                    if (edtTextSignupName.getText().toString().isEmpty()) {
                        edtTextSignupName.setError("Full Name");
                        edtTextSignupName.requestFocus();
                    }
                    if (edtTextSignupEmail.getText().toString().isEmpty()) {
                        edtTextSignupEmail.setError("Valid Email address");
                        edtTextSignupEmail.requestFocus();
                    }
                    if (edtTextSignupPassword.getText().toString().isEmpty()) {
                        edtTextSignupPassword.setError("Password should be 6 characters at least");
                        edtTextSignupPassword.requestFocus();
                    }
                    if (edtTextSignupConfirmPassword.getText().toString().isEmpty()) {
                        edtTextSignupConfirmPassword.setError("Confirm Password");
                        edtTextSignupConfirmPassword.requestFocus();
                    }

                } else if (!Patterns.EMAIL_ADDRESS.matcher(edtTextSignupEmail.getText().toString()).matches()) {
                    edtTextSignupEmail.setError("Invalid Email!");
                    edtTextSignupEmail.requestFocus();
                } else if (edtTextSignupPassword.getText().toString().length() < 6) {
                    edtTextSignupPassword.setError("Password should be 6 characters at least");
                    edtTextSignupPassword.requestFocus();
                } else if (!(edtTextSignupPassword.getText().toString().equals(edtTextSignupConfirmPassword.getText().toString()))) {
                    edtTextSignupConfirmPassword.setError("Password doesn't match");
                    edtTextSignupConfirmPassword.requestFocus();
                } else {
                    email = edtTextSignupEmail.getText().toString();
                    password = edtTextSignupPassword.getText().toString();
                    name = edtTextSignupName.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                normalUser.setName(name);
                                normalUser.setEmail(email);
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                                        .getCurrentUser().getUid()).setValue(normalUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                        if (!user.isEmailVerified()) {
                                                            user.sendEmailVerification();
                                                            Toast.makeText(SignUp.this, "Verification email has been set", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(SignUp.this, Login.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(SignUp.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                        }
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            } else {
                                edtTextSignupEmail.setError("Email address already exist, try with another");
                                edtTextSignupEmail.requestFocus();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(false);
    }
}