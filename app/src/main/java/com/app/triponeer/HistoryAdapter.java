package com.app.triponeer;

import android.app.AlertDialog;
import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    ArrayList<Trip> historyTrips;
    public OnHistoryEmptyList onHistoryEmptyList;
    Fragment fragment;
    Context context;

    public HistoryAdapter(Context context, Fragment fragment) {
        this.fragment = fragment;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.history_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        onHistoryEmptyList = (OnHistoryEmptyList) fragment;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvHistoryName.setText(historyTrips.get(position).getName());
        holder.tvHistoryDesc.setText(historyTrips.get(position).getDescription());
        holder.tvHistoryDate.setText(historyTrips.get(position).getDate());
        holder.tvHistoryTime.setText(historyTrips.get(position).getTime());
        holder.tvHistorySource.setText(historyTrips.get(position).getSourceName());
        holder.tvHistoryDestination.setText(historyTrips.get(position).getDestinationName());
        holder.tvHistoryStatus.setText(historyTrips.get(position).getStatus());
        holder.tvHistoryType.setText(historyTrips.get(position).getType());
        holder.tvHistoryDistance.setText(String.format("%.1f", historyTrips.get(position).getDistance()) + " km");


        holder.btnHistoryNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder((v.getRootView().getContext()));
                View dialogView = LayoutInflater.from(v.getRootView().getContext())
                        .inflate(R.layout.view_notes, null);

                holder.recyclerViewNotes = dialogView.findViewById(R.id.recyclerViewNotes);
                holder.notesAdapter = new NotesAdapter(v.getRootView().getContext(), historyTrips.get(position).getNotes());
                holder.recyclerViewNotes.setAdapter(holder.notesAdapter);
                holder.recyclerViewNotes.setLayoutManager(new LinearLayoutManager(v.getRootView().getContext()));

                builder.setView(dialogView);
                builder.setCancelable(true);
                builder.show();
            }
        });

        holder.btnDeleteTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder((v.getRootView().getContext()));
                View dialogView = LayoutInflater.from(v.getRootView().getContext())
                        .inflate(R.layout.confirm_delete, null);

                holder.btnConfirmDeleteTrip = dialogView.findViewById(R.id.btnConfirmDeleteTrip);
                holder.btnCancelDeleteTrip = dialogView.findViewById(R.id.btnCancelDeleteTrip);

                holder.btnConfirmDeleteTrip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference(".info/connected").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean connected = snapshot.getValue(Boolean.class);
                                if (connected) {
                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                                            .getCurrentUser().getUid()).child("trips").child("history").child(historyTrips.get(position).getName()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            historyTrips.remove(position);
                                            notifyDataSetChanged();
                                            if (historyTrips.isEmpty()) {
                                                onHistoryEmptyList.emptyList();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(context, "You're offline,\nconnect to internet and try again", Toast.LENGTH_SHORT).show();
                                    FirebaseDatabase.getInstance().purgeOutstandingWrites();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                        if (holder.dialog != null) {
                            holder.dialog.dismiss();
                        }
                    }
                });
                holder.btnCancelDeleteTrip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.dialog != null) {
                            holder.dialog.dismiss();
                        }
                    }
                });
                builder.setView(dialogView);
                builder.setCancelable(true);
                holder.dialog = builder.show();
            }
        });

        if (historyTrips.get(position) != null) {
            if (historyTrips.get(position).getStatus().equals("Completed")) {
                holder.headerHistory.setBackgroundColor(holder.itemView.getResources().getColor(R.color.green_700));
            }
            if (historyTrips.get(position).getStatus().equals("Cancelled")) {
                holder.headerHistory.setBackgroundColor(holder.itemView.getResources().getColor(R.color.red_700));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (historyTrips != null) {
            return historyTrips.size();
        }
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHistoryName, tvHistoryDesc, tvHistoryDate, tvHistoryTime,
                tvHistorySource, tvHistoryDestination, tvHistoryStatus, tvHistoryType,
                tvHistoryDistance;
        Button btnDeleteTrip;
        Button btnHistoryNote;
        ConstraintLayout expandableHistory;
        ConstraintLayout headerHistory;
        CardView cvHistory;
        Button btnConfirmDeleteTrip;
        Button btnCancelDeleteTrip;
        RecyclerView recyclerViewNotes;
        AlertDialog dialog = null;
        NotesAdapter notesAdapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            initComponent(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandableHistory.getVisibility() == View.GONE) {
                        TransitionManager.beginDelayedTransition(cvHistory, new AutoTransition());
                        expandableHistory.setVisibility(View.VISIBLE);
                    } else {
                        TransitionManager.beginDelayedTransition(cvHistory, new AutoTransition());
                        expandableHistory.setVisibility(View.GONE);
                    }
                }
            });
        }

        private void initComponent(View itemView) {
            tvHistoryName = itemView.findViewById(R.id.tvHistoryName);
            tvHistoryDesc = itemView.findViewById(R.id.tvHistoryDesc);
            tvHistoryDate = itemView.findViewById(R.id.tvHistoryDate);
            tvHistoryTime = itemView.findViewById(R.id.tvHistoryTime);
            btnDeleteTrip = itemView.findViewById(R.id.btnDeleteTrip);
            btnHistoryNote = itemView.findViewById(R.id.btnHistoryNote);
            expandableHistory = itemView.findViewById(R.id.expandableHistory);
            headerHistory = itemView.findViewById(R.id.headerHistory);
            tvHistorySource = itemView.findViewById(R.id.tvHistorySource);
            tvHistoryDestination = itemView.findViewById(R.id.tvHistoryDestination);
            tvHistoryStatus = itemView.findViewById(R.id.tvHistoryStatus);
            tvHistoryType = itemView.findViewById(R.id.tvHistoryType);
            tvHistoryDistance = itemView.findViewById(R.id.tvHistoryDistance);
            cvHistory = itemView.findViewById(R.id.cvHistory);
        }
    }

    public void setData(ArrayList<Trip> historyTrips) {
        if (this.historyTrips != null) {
            this.historyTrips.clear();
        } else {
            this.historyTrips = historyTrips;
            notifyDataSetChanged();
        }
    }

}

interface OnHistoryEmptyList {
    void emptyList();
}