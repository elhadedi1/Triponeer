package com.app.triponeer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class RoundTrip extends DialogFragment {
    String date, time;
    int selected_hour, selected_minute, selected_year, selected_month, selected_day;

    public interface OnRoundTrip {
        void roundData(String date, String time, int hour, int minute, int year, int month, int day);
    }

    public OnRoundTrip onRoundTrip;
    TextView roundDate, roundTime;
    ImageView imgDate, imgTime;
    Button btnSaveRound;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.round_trip_dialog, container, false);

        initComponent(view);

        btnSaveRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRoundTrip.roundData(date, time, selected_hour, selected_minute, selected_year, selected_month, selected_day);
                getDialog().dismiss();
            }
        });

        roundTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selected_hour = hourOfDay;
                                selected_minute = minute;
                                setTxtViewTime(hourOfDay, minute);
                            }
                        }, selected_hour, selected_minute, false);
                timePickerDialog.show();
            }
        });

        roundDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                selected_year = year;
                                selected_month = monthOfYear + 1;
                                selected_day = dayOfMonth;
                                date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                roundDate.setText(date);
                            }
                        }, selected_year, selected_month, selected_day);
                datePickerDialog.show();
            }
        });

        imgTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selected_hour = hourOfDay;
                                selected_minute = minute;
                                setTxtViewTime(hourOfDay, minute);
                            }
                        }, selected_hour, selected_minute, false);
                timePickerDialog.show();
            }
        });

        imgDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                selected_year = year;
                                selected_month = monthOfYear + 1;
                                selected_day = dayOfMonth;
                                date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                roundDate.setText(date);
                            }
                        }, selected_year, selected_month, selected_day);
                datePickerDialog.show();
            }
        });
        return view;
    }

    private void initComponent(View view) {
        roundDate = view.findViewById(R.id.roundDate);
        roundTime = view.findViewById(R.id.roundTime);
        imgDate = view.findViewById(R.id.imgDate);
        imgTime = view.findViewById(R.id.imgTime);
        btnSaveRound = view.findViewById(R.id.btnSaveRound);

        Calendar c = Calendar.getInstance();
        selected_year = c.get(Calendar.YEAR);
        selected_month = c.get(Calendar.MONTH);
        selected_day = c.get(Calendar.DAY_OF_MONTH);
        selected_hour = c.get(Calendar.HOUR_OF_DAY);
        selected_minute = c.get(Calendar.MINUTE);
        date = selected_day + "-" + (selected_month + 1) + "-" + selected_year;
        roundDate.setText(date);
        setTxtViewTime(selected_hour, selected_minute);
    }

    void setTxtViewTime(int hour, int minute) {
        if (hour == 0) {
            if (minute >= 10) {
                time = "12:" + minute + " AM";
                roundTime.setText(time);
            } else {
                time = "12:0" + minute + " AM";
                roundTime.setText(time);
            }
        } else if (hour < 12 && hour > 0) {
            if (minute >= 10) {
                time = hour + ":" + minute + " AM";
                roundTime.setText(time);
            } else {
                time = hour + ":0" + minute + " AM";
                roundTime.setText(time);
            }
        } else if (hour == 12) {
            if (minute >= 10) {
                time = "12:" + minute + " PM";
                roundTime.setText(time);
            } else {
                time = "12:0" + minute + " PM";
                roundTime.setText(time);
            }
        } else if (hour > 12) {
            if (minute >= 10) {
                time = hour - 12 + ":" + minute + " PM";
                roundTime.setText(time);
            } else {
                time = hour - 12 + ":0" + minute + " PM";
                roundTime.setText(time);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onRoundTrip = (OnRoundTrip) getTargetFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
