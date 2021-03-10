package com.app.triponeer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Login extends AppCompatActivity {
    private static final String TAG = "FB Auth";
    Button btnLogin, btnSignup;
    EditText edtTextLoginEmail, edtTextLoginPassword;
    TextView forgetPassword;
    private CallbackManager callbackManager;
    LoginButton loginButton;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private DatabaseReference reference;
    SocialMediaUser socialMediaUser;
    NormalUser normalUser;
    SharedPreferences saving;
    SharedPreferences.Editor edit;
    public static final String LOGIN_DATA = "Login";
    public static final String LOGIN_EMAIL = "Email";
    public static final String LOGIN_NAME = "Name";
    public static final String LOGIN_PICTURE = "Picture";
    public static final String IS_NEW_PICTURE = "isNewPicture";
    public static final String IS_LOGIN = "isLogin";
    public static final String IS_FACEBOOK_LOGIN = "isFacebookLogin";
    public static final String IS_GOOGLE_LOGIN = "isGoogleLogin";
    ProgressBar progressBar;


    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = findViewById(R.id.btnlogin);
        btnSignup = findViewById(R.id.btnSignup);
        edtTextLoginEmail = findViewById(R.id.edtTextSignupEmail);
        edtTextLoginPassword = findViewById(R.id.edtTextSignupPassword);
        forgetPassword = findViewById(R.id.forgetPassword);
        progressBar = findViewById(R.id.progressBar3);
        user = mAuth.getCurrentUser();
        socialMediaUser = SocialMediaUser.getInstance();
        createRequest();

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTextLoginEmail.getText().toString().isEmpty() && edtTextLoginPassword.getText().toString().isEmpty()) {
                    edtTextLoginEmail.setError("Enter the required Email");
                    edtTextLoginEmail.requestFocus();
                    edtTextLoginPassword.setError("Enter the required password");
                    edtTextLoginPassword.requestFocus();
                } else if (edtTextLoginEmail.getText().toString().isEmpty()) {
                    edtTextLoginEmail.setError("Enter the required Email");
                    edtTextLoginEmail.requestFocus();
                } else if (edtTextLoginPassword.getText().toString().isEmpty()) {
                    edtTextLoginPassword.setError("Enter the required password");
                    edtTextLoginPassword.requestFocus();
                } else {
                    if (!isValid(edtTextLoginEmail.getText().toString())) {
                        edtTextLoginEmail.setError("Enter the valid email");
                        edtTextLoginEmail.requestFocus();
                        return;
                    } else if (edtTextLoginPassword.getText().toString().isEmpty()) {
                        edtTextLoginPassword.setError("Password is required");
                        edtTextLoginPassword.requestFocus();
                        return;
                    } else {
                        String email = edtTextLoginEmail.getText().toString();
                        String password = edtTextLoginPassword.getText().toString();
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user.isEmailVerified()) {
                                        progressBar.setVisibility(View.VISIBLE);

                                        // Get data from Realtime database
                                        reference = FirebaseDatabase.getInstance().getReference("Users");
                                        reference.keepSynced(true);
                                        reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                normalUser = snapshot.getValue(NormalUser.class);
                                                NormalUser n = NormalUser.getInstance();
                                                n.setName(normalUser.getName());
                                                n.setEmail(normalUser.getEmail());
                                                n.setImageUrl(normalUser.getImageUrl());
                                                saving = getSharedPreferences(LOGIN_DATA, 0);
                                                edit = saving.edit();
                                                try {
                                                    edit.putString(LOGIN_NAME, normalUser.getName());
                                                    edit.putString(LOGIN_EMAIL, normalUser.getEmail());
                                                    if (normalUser.getImageUrl() == null) {
                                                        edit.putString(LOGIN_PICTURE, "");
                                                    } else {
                                                        edit.putString(LOGIN_PICTURE, normalUser.getImageUrl());
                                                    }
                                                    edit.putBoolean(IS_NEW_PICTURE, true);
                                                    edit.putBoolean(IS_LOGIN, true);
                                                    edit.putBoolean(IS_FACEBOOK_LOGIN, false);
                                                    edit.putBoolean(IS_GOOGLE_LOGIN, false);
                                                } catch (Exception e) {
                                                    Log.i(TAG, "onComplete: " + e);
                                                }
                                                edit.apply();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(getBaseContext(), "Something wrong happened!", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        user.sendEmailVerification();
                                        Toast.makeText(Login.this, "Check your email to verify you account!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(Login.this, "Wrong credentials!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgetPassword.class);
                startActivity(intent);
                finish();
            }
        });
        setFaceBookBtn();
    }

    static boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    private void setFaceBookBtn() {
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setPermissions(Arrays.asList("email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: " + loginResult);

                handleFaceBookAccessToken(loginResult.getAccessToken());
                saving = getSharedPreferences(LOGIN_DATA, 0);
                edit = saving.edit();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String name = object.getString("name");
                            String email = object.getString("email");
                            String picture = object.getString("picture");
                            socialMediaUser.setName(name);
                            socialMediaUser.setEmail(email);
                            socialMediaUser.setImageUrl(picture + "?type=large");
                            edit.putString(LOGIN_NAME, name);
                            edit.putString(LOGIN_EMAIL, email);
                            edit.putString(LOGIN_PICTURE, picture + "?type=large");
                            edit.putBoolean(IS_LOGIN, false);
                            edit.putBoolean(IS_FACEBOOK_LOGIN, true);
                            edit.putBoolean(IS_GOOGLE_LOGIN, false);
                            edit.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,email,picture");
                request.setParameters(parameters);
                request.executeAsync();

                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:OnCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: " + error.getMessage());
            }
        });

    }

    private void handleFaceBookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFaceBookAccessToken: " + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Success");
                } else {
                    Log.w(TAG, "onComplete: failure", task.getException());
                    Toast.makeText(Login.this, "Authentication Failed ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                            if (signInAccount != null) {
                                saving = getSharedPreferences(LOGIN_DATA, 0);
                                edit = saving.edit();
                                try {
                                    socialMediaUser.setName(signInAccount.getDisplayName());
                                    socialMediaUser.setEmail(signInAccount.getEmail());
                                    socialMediaUser.setImageUrl(signInAccount.getPhotoUrl().toString() + "?type=large");
                                    edit.putString(LOGIN_NAME, signInAccount.getDisplayName());
                                    edit.putString(LOGIN_EMAIL, signInAccount.getEmail());
                                    edit.putString(LOGIN_PICTURE, signInAccount.getPhotoUrl().toString() + "?type=large");
                                    edit.putBoolean(IS_LOGIN, false);
                                    edit.putBoolean(IS_FACEBOOK_LOGIN, false);
                                    edit.putBoolean(IS_GOOGLE_LOGIN, true);
                                } catch (Exception e) {
                                    Log.i(TAG, "onComplete: " + e);
                                }
                                edit.apply();
                            }
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(false);
    }

}