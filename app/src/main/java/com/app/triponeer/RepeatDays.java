package com.app.triponeer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class RepeatDays extends DialogFragment {
    public interface OnApplyRepeat {
        void sendInput(ArrayList<String> input);
    }

    public OnApplyRepeat onApplyRepeat;
    Switch switchSaturday;
    Switch switchSunday;
    Switch switchMonday;
    Switch switchTuesday;
    Switch switchWednesday;
    Switch switchThursday;
    Switch switchFriday;
    Button btnApply;

    ArrayList<String> checkedDays;

    RepeatDays(ArrayList<String> repeatedDays) {
        checkedDays = repeatedDays;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.repeat_days, container, false);

        initComponent(view);

        showChecked();


        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedDays.clear();
                if (switchSaturday.isChecked()) {
                    checkedDays.add(switchSaturday.getText().toString());
                }
                if (switchSunday.isChecked()) {
                    checkedDays.add(switchSunday.getText().toString());
                }
                if (switchMonday.isChecked()) {
                    checkedDays.add(switchMonday.getText().toString());
                }
                if (switchTuesday.isChecked()) {
                    checkedDays.add(switchTuesday.getText().toString());
                }
                if (switchWednesday.isChecked()) {
                    checkedDays.add(switchWednesday.getText().toString());
                }
                if (switchThursday.isChecked()) {
                    checkedDays.add(switchThursday.getText().toString());
                }
                if (switchFriday.isChecked()) {
                    checkedDays.add(switchFriday.getText().toString());
                }
                onApplyRepeat.sendInput(checkedDays);
                getDialog().dismiss();
            }
        });
        return view;
    }

    private void showChecked() {
        if (!checkedDays.isEmpty()) {
            if (checkedDays.contains("Saturday")) {
                switchSaturday.setChecked(true);
            }
            if (checkedDays.contains("Sunday")) {
                switchSunday.setChecked(true);
            }
            if (checkedDays.contains("Monday")) {
                switchMonday.setChecked(true);
            }
            if (checkedDays.contains("Tuesday")) {
                switchTuesday.setChecked(true);
            }
            if (checkedDays.contains("Wednesday")) {
                switchWednesday.setChecked(true);
            }
            if (checkedDays.contains("Thursday")) {
                switchThursday.setChecked(true);
            }
            if (checkedDays.contains("Friday")) {
                switchFriday.setChecked(true);
            }
        }
    }

    private void initComponent(View view) {
        switchSaturday = view.findViewById(R.id.switchSaturday);
        switchSunday = view.findViewById(R.id.switchSunday);
        switchMonday = view.findViewById(R.id.switchMonday);
        switchTuesday = view.findViewById(R.id.switchTuesday);
        switchWednesday = view.findViewById(R.id.switchWednesday);
        switchThursday = view.findViewById(R.id.switchThursday);
        switchFriday = view.findViewById(R.id.switchFriday);
        btnApply = view.findViewById(R.id.btnApply);
        if (checkedDays == null) {
            checkedDays = new ArrayList<String>();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onApplyRepeat = (OnApplyRepeat) getTargetFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
