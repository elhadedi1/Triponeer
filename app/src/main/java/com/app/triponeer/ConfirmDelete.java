package com.app.triponeer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmDelete extends DialogFragment {

    public interface OnConfirmDelete {
        void sendInput(String input);
    }

    public OnConfirmDelete onConfirmDelete;

    Button btnConfirmDeleteTrip, btnCancelDeleteTrip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_delete, container, false);

        btnConfirmDeleteTrip = view.findViewById(R.id.btnConfirmDeleteTrip);
        btnCancelDeleteTrip = view.findViewById(R.id.btnCancelDeleteTrip);

        btnConfirmDeleteTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmDelete.sendInput("delete");
                getDialog().dismiss();
            }
        });

        btnCancelDeleteTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmDelete.sendInput("cancel");
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onConfirmDelete = (OnConfirmDelete) getTargetFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
