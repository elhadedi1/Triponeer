package com.app.triponeer;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {
    private EditText emailField;
    private Button btnRestPassword;
    private ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        emailField = findViewById(R.id.edtTextEditProfileEmail);
        btnRestPassword = findViewById(R.id.btnResetPassword);
        progressBar = findViewById(R.id.progressBarReset);
        auth = FirebaseAuth.getInstance();
        btnRestPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restPassword();
            }
        });
    }

    private void restPassword() {
        String email = emailField.getText().toString().trim();
        if (email.isEmpty()) {
            emailField.setError("Email is required");
            emailField.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Enter the valid Email");
            emailField.requestFocus();
            return;
        } else {
            progressBar.setVisibility(View.VISIBLE);
            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgetPassword.this, "Check your email to reset password!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(ForgetPassword.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ForgetPassword.this, "Try again! Something wrong happened!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        Intent intent = new Intent(ForgetPassword.this, Login.class);
        startActivity(intent);

    }
}