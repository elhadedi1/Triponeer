package com.app.triponeer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AlarmService extends Service {
    public AlarmService() {
    }

    WindowManager mWindowManager;
    View view;
    WindowManager.LayoutParams params;
    MediaPlayer myPlayer;

    TextView tvAlarmName, tvAlarmDesc, tvAlarmDate,
            tvAlarmTime, tvAlarmSource, tvAlarmDestination,
            tvAlarmType, tvAlarmDistance;
    Button btnAlarmStart, btnAlarmCancel, btnAlarmSnooze;

    String name, description, date, time, source, destination, type, distance;
    double destLat, destLong;
    ArrayList<String> notes;


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Inflate the floating view layout we created
        view = LayoutInflater.from(this).inflate(R.layout.activity_alarm, null);
        myPlayer = MediaPlayer.create(this, R.raw.media);
        myPlayer.setLooping(true);
        myPlayer.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }


        //Specify the view position
        params.gravity = Gravity.CENTER + Gravity.BOTTOM;       //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 0;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(view, params);


        tvAlarmName = view.findViewById(R.id.tvAlarmName);
        tvAlarmDesc = view.findViewById(R.id.tvAlarmDesc);
        tvAlarmDate = view.findViewById(R.id.tvAlarmDate);
        tvAlarmTime = view.findViewById(R.id.tvAlarmTime);
        tvAlarmSource = view.findViewById(R.id.tvAlarmSource);
        tvAlarmDestination = view.findViewById(R.id.tvAlarmDestination);
        tvAlarmType = view.findViewById(R.id.tvAlarmType);
        tvAlarmDistance = view.findViewById(R.id.tvAlarmDistance);
        btnAlarmStart = view.findViewById(R.id.btnAlarmStart);
        btnAlarmCancel = view.findViewById(R.id.btnAlarmCancel);
        btnAlarmSnooze = view.findViewById(R.id.btnAlarmSnooze);

        name = intent.getStringExtra("name");
        description = intent.getStringExtra("description");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        source = intent.getStringExtra("source");
        destination = intent.getStringExtra("destination");
        type = intent.getStringExtra("type");
        distance = intent.getStringExtra("distance");
        notes = intent.getStringArrayListExtra("notes");

        if (time != null) {
            beatifyTime(Integer.parseInt(time.split(":")[0]), Integer.parseInt(time.split(":")[1]));
            tvAlarmName.setText(name);
            tvAlarmDesc.setText(description);
            tvAlarmDate.setText(date);
            tvAlarmTime.setText(time);
            tvAlarmSource.setText(source);
            tvAlarmDestination.setText(destination);
            tvAlarmType.setText(type);
            tvAlarmDistance.setText(distance);
        }


        btnAlarmStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri intentUri = Uri.parse("google.navigation:q=" +
                        destLat + ", " +
                        destLong);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                v.getContext().startActivity(mapIntent);

                if (!Settings.canDrawOverlays(v.getContext())) {
                    //If the draw over permission is not available open the settings screen
                    //to grant the permission.
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + v.getContext().getPackageName()));
                    v.getContext().startActivity(intent);
                } else {
                    v.getContext().startService(new Intent(v.getContext(), Bubble.class)
                            .putStringArrayListExtra("notes", notes));
                }
                myPlayer.stop();
                stopSelf();
            }
        });

        btnAlarmCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), AlarmBroadcast.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                manager.cancel(pendingIntent);
                myPlayer.stop();

                stopSelf();
            }
        });

        btnAlarmSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm(name, description, date, time, source, destination, type, distance, destLat, destLong, notes);
                myPlayer.pause();
                Toast.makeText(AlarmService.this, "Snoozed for 1 minute!", Toast.LENGTH_SHORT).show();

                Handler handler = new Handler(getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        String result = msg.obj.toString();
                        if (result.equals("resume")) {
                            myPlayer.start();
                            mWindowManager.addView(view, params);
                        }
                    }
                };
                mWindowManager.removeView(view);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(60000);
                        } catch (Exception e) {

                        }
                        Message msg = Message.obtain();
                        msg.obj = "resume";
                        handler.sendMessage(msg);

                    }
                }).start();

            }
        });

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (view != null) mWindowManager.removeView(view);
    }

    void beatifyTime(int hour, int minute) {
        if (hour == 0) {
            if (minute >= 10) {
                time = "12:" + minute + " AM";
            } else {
                time = "12:0" + minute + " AM";
            }
        } else if (hour < 12 && hour > 0) {
            if (minute >= 10) {
                time = hour + ":" + minute + " AM";
            } else {
                time = hour + ":0" + minute + " AM";
            }
        } else if (hour == 12) {
            if (minute >= 10) {
                time = "12:" + minute + " PM";
            } else {
                time = "12:0" + minute + " PM";
            }
        } else if (hour > 12) {
            if (minute >= 10) {
                time = hour - 12 + ":" + minute + " PM";
            } else {
                time = hour - 12 + ":0" + minute + " PM";
            }
        }
    }


    private void setAlarm(String name, String description,
                          String date, String time, String source,
                          String destination, String type, String distance,
                          double destLat, double destLong, ArrayList<String> notes) {
        AlarmManager am = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getBaseContext().getApplicationContext(), AlarmBroadcast.class);
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
                getBaseContext().getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String dateNTime = date + " " + time;
        DateFormat formatter = new SimpleDateFormat("d-M-yyyy H:mm");
        Calendar c = Calendar.getInstance();
        try {
            Date date1 = formatter.parse(dateNTime);
            if (c.getTimeInMillis() < date1.getTime()) {
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 5), pendingIntent);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}