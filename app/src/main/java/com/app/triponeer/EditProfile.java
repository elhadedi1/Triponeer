package com.app.triponeer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.InputStream;


public class EditProfile extends Fragment {

    ImageView imgViewEditProfileImage;
    EditText edtTextEditProfileName;
    EditText edtTextEditProfileEmail;
    EditText edtTextEditProfilePassword;
    EditText edtTextEditProfileConfirmPassword;
    Button btnSaveEditProfile;
    EditText edtTextEditProfileCurrentPassword;
    Bitmap profileImage;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    public static Uri imgpath;
    ProgressBar progressBar;
    DatabaseReference reference;
    NormalUser normalUser = NormalUser.getInstance();
    SharedPreferences saving;
    SharedPreferences.Editor edit;
    Uri pictureUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.edit_profile, container, false);

        initComponent(view);

        saving = getContext().getSharedPreferences(Login.LOGIN_DATA, 0);
        edit = saving.edit();

        edtTextEditProfileName.setText(normalUser.getName());
        edtTextEditProfileEmail.setText(normalUser.getEmail());
        if (!normalUser.getImageUrl().isEmpty()) {
            try {
                FileInputStream is = getContext().openFileInput(normalUser.getEmail() + ".png");
                Bitmap image = BitmapFactory.decodeStream(is);
                imgViewEditProfileImage.setImageBitmap(image);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        btnSaveEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTextEditProfileName.getText().toString().isEmpty() ||
                        edtTextEditProfileEmail.getText().toString().isEmpty() ||
                        edtTextEditProfilePassword.getText().toString().isEmpty() ||
                        edtTextEditProfileConfirmPassword.getText().toString().isEmpty() ||
                        edtTextEditProfileCurrentPassword.getText().toString().isEmpty()
                ) {
                    if (edtTextEditProfileName.getText().toString().isEmpty()) {
                        edtTextEditProfileName.setError("Full Name");
                        edtTextEditProfileName.requestFocus();
                    }
                    if (edtTextEditProfileEmail.getText().toString().isEmpty()) {
                        edtTextEditProfileEmail.setError("Valid Email address");
                        edtTextEditProfileEmail.requestFocus();
                    }
                    if (edtTextEditProfilePassword.getText().toString().isEmpty()) {
                        edtTextEditProfilePassword.setError("Password should be 6 characters at least");
                        edtTextEditProfilePassword.requestFocus();
                    }
                    if (edtTextEditProfileConfirmPassword.getText().toString().isEmpty()) {
                        edtTextEditProfileConfirmPassword.setError("Confirm Password");
                        edtTextEditProfileConfirmPassword.requestFocus();
                    }


                } else if (!Patterns.EMAIL_ADDRESS.matcher(edtTextEditProfileEmail.getText().toString()).matches()) {
                    edtTextEditProfileEmail.setError("Invalid Email!");
                    edtTextEditProfileEmail.requestFocus();
                } else if (edtTextEditProfilePassword.getText().toString().length() < 6) {
                    edtTextEditProfilePassword.setError("Password should be 6 characters at least");
                    edtTextEditProfilePassword.requestFocus();
                } else if (!(edtTextEditProfilePassword.getText().toString().equals(edtTextEditProfileConfirmPassword.getText().toString()))) {
                    edtTextEditProfileConfirmPassword.setError("Password doesn't match");
                    edtTextEditProfileConfirmPassword.requestFocus();
                } else {

                    String currentPassword = edtTextEditProfileCurrentPassword.getText().toString();

                    progressBar.setVisibility(View.VISIBLE);
                    reference = FirebaseDatabase.getInstance().getReference("Users");
                    changeData();

                    AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

                    user.reauthenticate(authCredential)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    user.updatePassword(edtTextEditProfilePassword.getText().toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                                    user.updateEmail(edtTextEditProfileEmail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                                    Toast.makeText(getContext(), "Profile has been updated", Toast.LENGTH_SHORT).show();
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .setCustomAnimations(R.anim.fragment_enter_left_to_right, R.anim.fragment_exit_to_right)
                                            .replace(R.id.fragment_container, new Profile()).commit();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Wrong password!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

        imgViewEditProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fragment_enter_left_to_right, R.anim.fragment_exit_to_right)
                        .replace(R.id.fragment_container, new Profile()).commit();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        return view;
    }

    private void initComponent(View view) {
        imgViewEditProfileImage = view.findViewById(R.id.imgViewEditProfileImage);
        edtTextEditProfileName = view.findViewById(R.id.edtTextEditProfileName);
        edtTextEditProfileEmail = view.findViewById(R.id.edtTextEditProfileEmail);
        edtTextEditProfileCurrentPassword = view.findViewById(R.id.edtTextEditProfileCurrentPassword);
        edtTextEditProfilePassword = view.findViewById(R.id.edtTextEditProfilePassword);
        edtTextEditProfileConfirmPassword = view.findViewById(R.id.edtTextEditProfileConfirmPassword);
        btnSaveEditProfile = view.findViewById(R.id.btnSaveEditProfile);
        progressBar = view.findViewById(R.id.progressBar2);
        profileImage = null;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromStream(Context context, Uri uri,
                                                       int reqWidth, int reqHeight) {
        InputStream inputStream = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            ContentResolver contentResolver = context.getContentResolver();
            inputStream = contentResolver.openInputStream(uri);
            // First decode with inJustDecodeBounds=true to check dimensions
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            inputStream = contentResolver.openInputStream(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_OK && requestCode == 1) {
            try {
                final Uri imageUri = data.getData();
                imgpath = imageUri;
                pictureUri = imageUri;
                final Bitmap selectedImage = decodeSampledBitmapFromStream(getContext(), imageUri, 200, 200);
                profileImage = selectedImage;
                imgViewEditProfileImage.setImageBitmap(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void changeData() {
        if (!normalUser.getName().equals(edtTextEditProfileName.getText().toString())) {
            reference.child(user.getUid()).child("name").setValue(edtTextEditProfileName.getText().toString());
            normalUser.setName(edtTextEditProfileName.getText().toString());
            edit.putString(Login.LOGIN_NAME, edtTextEditProfileName.getText().toString());
            edit.apply();
        }
        if (!normalUser.getEmail().equals(edtTextEditProfileEmail.getText().toString())) {
            reference.child(user.getUid()).child("email").setValue(edtTextEditProfileName.getText().toString());
            normalUser.setEmail(edtTextEditProfileEmail.getText().toString());
            edit.putString(Login.LOGIN_EMAIL, edtTextEditProfileEmail.getText().toString());
            edit.apply();
        }
        if (pictureUri != null) {
            uploadToFirebase(pictureUri);
        }

    }

    private void uploadToFirebase(Uri uri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        reference.child(user.getUid()).child("imageUrl").setValue(uri.toString());
                        normalUser.setImageUrl(uri.toString());
                        edit.putString(Login.LOGIN_PICTURE, uri.toString());
                        edit.putBoolean(Login.IS_NEW_PICTURE, true);
                        edit.apply();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Uploading Failed !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri mUri) {

        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

}