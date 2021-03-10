package com.app.triponeer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;

public class Profile extends Fragment {
    Button btnEditProfile, btnLogout;
    ImageView imgViewProfilePicture;
    TextView txtViewProfileName, txtViewProfileEmail;
    SocialMediaUser socialMediaUser;
    NormalUser normalUser;
    SharedPreferences saving;
    SharedPreferences.Editor edit;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profile, container, false);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnLogout);
        imgViewProfilePicture = view.findViewById(R.id.imgViewProfilePicture);
        txtViewProfileName = view.findViewById(R.id.txtViewProfileName);
        txtViewProfileEmail = view.findViewById(R.id.txtViewProfileEmail);
        socialMediaUser = SocialMediaUser.getInstance();
        normalUser = NormalUser.getInstance();
        saving = getContext().getSharedPreferences(Login.LOGIN_DATA, 0);
        edit = saving.edit();
        showUserData();
        btnEditProfile();
        btnLogout();

        return view;
    }

    void btnEditProfile() {
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference(".info/connected").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean connected = snapshot.getValue(Boolean.class);
                        if (connected) {
                            if (saving.getBoolean(Login.IS_LOGIN, true)) {
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.fragment_enter_right_to_left, R.anim.fragment_exit_to_left)
                                        .replace(R.id.fragment_container, new EditProfile()).commit();
                            } else {
                                Toast.makeText(getContext(), "Please Change your data from your social media account!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "You're offline,\nconnect to internet and try again", Toast.LENGTH_SHORT).show();
                            FirebaseDatabase.getInstance().purgeOutstandingWrites();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    void showUserData() {
        if (saving.getBoolean(Login.IS_LOGIN, false)) {
            txtViewProfileName.setText(normalUser.getName());
            txtViewProfileEmail.setText(normalUser.getEmail());
            if (!normalUser.getImageUrl().isEmpty()) {
                try {
                    FileInputStream is = getContext().openFileInput(normalUser.getEmail() + ".png");
                    Bitmap image = BitmapFactory.decodeStream(is);
                    imgViewProfilePicture.setImageBitmap(image);
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (saving.getBoolean(Login.IS_FACEBOOK_LOGIN, false)) {
            txtViewProfileName.setText(socialMediaUser.getName());
            txtViewProfileEmail.setText(socialMediaUser.getEmail());

        } else if (saving.getBoolean(Login.IS_GOOGLE_LOGIN, false)) {
            txtViewProfileName.setText(socialMediaUser.getName());
            txtViewProfileEmail.setText(socialMediaUser.getEmail());
            if (!socialMediaUser.getImageUrl().isEmpty()) {
                Picasso.get().load(socialMediaUser.getImageUrl()).into(imgViewProfilePicture);
            }
        }

    }

    void btnLogout() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(getContext(), Login.class);
                try {
                    edit.putString(Login.LOGIN_NAME, "");
                    edit.putString(Login.LOGIN_EMAIL, "");
                    edit.putString(Login.LOGIN_PICTURE, "");
                    edit.putBoolean(Login.IS_LOGIN, false);
                    edit.putBoolean(Login.IS_FACEBOOK_LOGIN, false);
                    edit.putBoolean(Login.IS_GOOGLE_LOGIN, false);
                    edit.putBoolean(Login.IS_NEW_PICTURE, true);
                } catch (Exception e) {
                    Log.i("Profile", "onComplete: " + e);
                }
                edit.commit();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}