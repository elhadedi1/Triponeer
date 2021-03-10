package com.app.triponeer;

import android.annotation.SuppressLint;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;


public class Upcoming extends Fragment implements OnUpcomingEmptyList {
    RecyclerView rvUpcoming;
    UpcomingAdapter upcomingAdapter;
    public static LinearLayout upcomingEmptyLayout;
    ArrayList<Trip> upcomingTrips;
    Trip trip;
    Button btnAdd;
    SwipeRefreshLayout swipeRefreshLayoutUpcoming;
    SharedPreferences saving;
    SharedPreferences.Editor edit;
    NormalUser normalUser;
    SocialMediaUser socialMediaUser;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private DatabaseReference reference;

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.upcoming, container, false);
        btnAdd = view.findViewById(R.id.btnAddTrip);
        swipeRefreshLayoutUpcoming = view.findViewById(R.id.swipeRefreshLayoutUpcoming);
        upcomingEmptyLayout = view.findViewById(R.id.upcomingEmptyLayout);
        swipeRefreshLayoutUpcoming.setColorSchemeResources(R.color.colorPrimary);
        normalUser = NormalUser.getInstance();
        socialMediaUser = SocialMediaUser.getInstance();
        user = mAuth.getCurrentUser();
        trip = new Trip();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.keepSynced(true);
        saveDataToNormalUserClass();
        setBtnAddAction();

        rvUpcoming = view.findViewById(R.id.rvUpcoming);
        upcomingAdapter = new UpcomingAdapter(getContext(), Upcoming.this);
        rvUpcoming.setAdapter(upcomingAdapter);
        rvUpcoming.setLayoutManager(new LinearLayoutManager(getContext()));
        upcomingTrips = new ArrayList<>();


        if (reference != null && user != null) {
            getData();
        }


        swipeRefreshLayoutUpcoming.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayoutUpcoming.setRefreshing(false);
            }
        });

        return view;
    }

    private void setDataSource() {
        if (upcomingAdapter != null) {
            upcomingAdapter.setData(upcomingTrips);
        }
    }

    void setBtnAddAction() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference(".info/connected").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean connected = snapshot.getValue(Boolean.class);
                        if (connected) {
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fragment_enter_right_to_left, R.anim.fragment_exit_to_left)
                                    .replace(R.id.fragment_container, new AddTrip("new")).addToBackStack(null).commit();
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

    public void saveDataToNormalUserClass() {
        saving = getActivity().getSharedPreferences(Login.LOGIN_DATA, 0);
        if (saving.getBoolean(Login.IS_LOGIN, false)) {
            if (!saving.getString(Login.LOGIN_NAME, "").isEmpty() &&
                    !saving.getString(Login.LOGIN_EMAIL, "").isEmpty() &&
                    !saving.getString(Login.LOGIN_PICTURE, "").isEmpty()) {
                normalUser.setName(saving.getString(Login.LOGIN_NAME, ""));
                normalUser.setEmail(saving.getString(Login.LOGIN_EMAIL, ""));
                normalUser.setImageUrl(saving.getString(Login.LOGIN_PICTURE, ""));
                if (saving.getBoolean(Login.IS_NEW_PICTURE, false)) {
                    edit = saving.edit();
                    edit.putBoolean(Login.IS_NEW_PICTURE, false);
                    edit.apply();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = normalUser.getImageUrl();
                            Bitmap image = downloadImage(url);
                            try {
                                FileOutputStream stream = getContext().openFileOutput(normalUser.getEmail() + ".png", getContext().MODE_PRIVATE);
                                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                stream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        } else if (saving.getBoolean(Login.IS_FACEBOOK_LOGIN, false)) {
            if (!saving.getString(Login.LOGIN_NAME, "").isEmpty() &&
                    !saving.getString(Login.LOGIN_EMAIL, "").isEmpty() &&
                    !saving.getString(Login.LOGIN_PICTURE, "").isEmpty()) {
                Log.i("MainActivity", "SharePref: " + saving.getString(Login.LOGIN_NAME, ""));
                socialMediaUser.setName(saving.getString(Login.LOGIN_NAME, ""));
                socialMediaUser.setEmail(saving.getString(Login.LOGIN_EMAIL, ""));
                socialMediaUser.setImageUrl(saving.getString(Login.LOGIN_PICTURE, ""));
            }
        } else if (saving.getBoolean(Login.IS_GOOGLE_LOGIN, false)) {
            if (!saving.getString(Login.LOGIN_NAME, "").isEmpty() &&
                    !saving.getString(Login.LOGIN_EMAIL, "").isEmpty() &&
                    !saving.getString(Login.LOGIN_PICTURE, "").isEmpty()) {
                Log.i("MainActivity", "SharePref: " + saving.getString(Login.LOGIN_NAME, ""));
                socialMediaUser.setName(saving.getString(Login.LOGIN_NAME, ""));
                socialMediaUser.setEmail(saving.getString(Login.LOGIN_EMAIL, ""));
                socialMediaUser.setImageUrl(saving.getString(Login.LOGIN_PICTURE, ""));
            }
        }
    }

    public Bitmap downloadImage(String url) {
        Bitmap image = null;
        URL urlObj = null;
        HttpsURLConnection connection;
        InputStream inputStream;
        try {
            urlObj = new URL(url);
            connection = (HttpsURLConnection) urlObj.openConnection();
            connection.connect();
            inputStream = connection.getInputStream();
            image = BitmapFactory.decodeStream(inputStream);
            connection.disconnect();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    private void getData() {
        upcomingTrips.clear();
        reference.child(user.getUid()).child("trips").child("upcoming").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    trip = dataSnapshot.getValue(Trip.class);
                    if (trip != null) {
                        upcomingTrips.add(trip);
                        setAlarm(trip.getName(), trip.getDescription(),
                                trip.getDate(), trip.getHour() + ":" + trip.getMinute(),
                                trip.getSourceName(), trip.getDestinationName(), trip.getType(),
                                String.format("%.1f", trip.getDistance()),
                                trip.getDestLat(), trip.getDestLong(),
                                trip.getNotes(), trip.getRepeatPattern(), trip.getRepeatDays(), trip.getDay() + trip.getHour() + trip.getMinute());
                    }
                }
                if (!upcomingTrips.isEmpty()) {
                    upcomingEmptyLayout.setVisibility(View.INVISIBLE);
                }
                setDataSource();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void emptyList() {
        upcomingEmptyLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void update() {
        getData();
    }

    private void setAlarm(String name, String description,
                          String date, String time, String source,
                          String destination, String type, String distance,
                          double destLat, double destLong, ArrayList<String> notes,
                          String repeatPattern, ArrayList<String> repeatDays, int id) {
        AlarmManager am = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getContext().getApplicationContext(), AlarmBroadcast.class);
        intent.putExtra("name", name);
        intent.putExtra("description", description);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("source", source);
        intent.putExtra("destination", destination);
        intent.putExtra("type", type);
        intent.putExtra("distance", distance);
        intent.putExtra("destLat", destLat);
        intent.putExtra("destLong", destLong);
        intent.putStringArrayListExtra("notes", notes);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext().getApplicationContext(), id, intent, PendingIntent.FLAG_ONE_SHOT);
        String dateNTime = date + " " + time;
        DateFormat formatter = new SimpleDateFormat("d-M-yyyy H:mm");
        Calendar c = Calendar.getInstance();
        try {
            Date date1 = formatter.parse(dateNTime);
            if (c.getTimeInMillis() < date1.getTime()) {
                am.set(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}