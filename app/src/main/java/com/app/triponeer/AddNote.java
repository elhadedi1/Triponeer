package com.app.triponeer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddNote extends DialogFragment {

    public interface OnSaveNote {
        void sendInput(String input);
    }

    public OnSaveNote onSaveNote;
    TextView textView;
    EditText edtTextNote;
    Button btnSaveNote;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_note, container, false);

        initComponent(view);

        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = edtTextNote.getText().toString();
                if (!input.isEmpty()) {
                    onSaveNote.sendInput(input);
                }
                getDialog().dismiss();
            }
        });
        return view;
    }

    private void initComponent(View view) {
        textView = view.findViewById(R.id.textView);
        edtTextNote = view.findViewById(R.id.edtTextNote);
        btnSaveNote = view.findViewById(R.id.btnConfirmDeleteTrip);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onSaveNote = (OnSaveNote) getTargetFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
