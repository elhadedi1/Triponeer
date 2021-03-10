package com.app.triponeer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.ViewHolder> {
    ArrayList<Trip> upcomingTrips;
    RecyclerView recyclerViewNotes;
    NotesAdapter notesAdapter;
    Context context;
    public OnUpcomingEmptyList onUpcomingEmptyList;
    Fragment fragment;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private DatabaseReference reference;
    Trip trip;

    public UpcomingAdapter(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.upcoming_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        onUpcomingEmptyList = (OnUpcomingEmptyList) fragment;
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.keepSynced(true);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvUpcomingName.setText(upcomingTrips.get(position).getName());
        holder.tvUpcomingDesc.setText(upcomingTrips.get(position).getDescription());
        holder.tvUpcomingDate.setText(upcomingTrips.get(position).getDate());
        holder.tvUpcomingTime.setText(upcomingTrips.get(position).getTime());
        holder.tvUpcomingSource.setText(upcomingTrips.get(position).getSourceName());
        holder.tvUpcomingDestination.setText(upcomingTrips.get(position).getDestinationName());
        holder.tvUpcomingType.setText(upcomingTrips.get(position).getType());
        holder.tvUpcomingDistance.setText(String.format("%.1f", upcomingTrips.get(position).getDistance()) + " km");


        holder.btnUpcomingMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.upcoming_menu);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.btnMenuEdit:
                                FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        boolean connected = snapshot.getValue(Boolean.class);
                                        if (connected) {
                                            ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                                                    .setCustomAnimations(R.anim.fragment_enter_right_to_left, R.anim.fragment_exit_to_left)
                                                    .replace(R.id.fragment_container, new AddTrip(upcomingTrips.get(position), "edit")).addToBackStack(null).commit();
                                        } else {
                                            Toast.makeText(context, "You're offline,\nconnect to internet and try again", Toast.LENGTH_SHORT).show();
                                            FirebaseDatabase.getInstance().purgeOutstandingWrites();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                                return true;
                            case R.id.btnMenuCancel:
                                FirebaseDatabase.getInstance().getReference(".info/connected").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        boolean connected = snapshot.getValue(Boolean.class);
                                        if (connected) {
                                            upcomingTrips.get(position).setStatus("Cancelled");
                                            reference.child(FirebaseAuth.getInstance()
                                                    .getCurrentUser().getUid()).child("trips").child("upcoming").child(upcomingTrips.get(position).getName()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    reference.child(FirebaseAuth.getInstance()
                                                            .getCurrentUser().getUid()).child("trips").child("history").child(upcomingTrips.get(position).getName()).setValue(upcomingTrips.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (upcomingTrips.size() > 0) {
                                                                upcomingTrips.remove(position);
                                                            }
                                                            notifyDataSetChanged();
                                                            if (upcomingTrips.isEmpty()) {
                                                                onUpcomingEmptyList.emptyList();
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        } else {
                                            Toast.makeText(context, "You're offline,\nconnect to internet and try again", Toast.LENGTH_SHORT).show();
                                            FirebaseDatabase.getInstance().purgeOutstandingWrites();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        System.out.println(error.toString());
                                    }
                                });
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }
        });

        holder.btnUpcomingNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder((v.getRootView().getContext()));
                View dialogView = LayoutInflater.from(v.getRootView().getContext())
                        .inflate(R.layout.view_notes, null);

                recyclerViewNotes = dialogView.findViewById(R.id.recyclerViewNotes);
                notesAdapter = new NotesAdapter(v.getRootView().getContext(), upcomingTrips.get(position).getNotes());
                recyclerViewNotes.setAdapter(notesAdapter);
                recyclerViewNotes.setLayoutManager(new LinearLayoutManager(v.getRootView().getContext()));

                builder.setView(dialogView);
                builder.setCancelable(true);
                builder.show();
            }
        });

        holder.btnStartNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri intentUri = Uri.parse("google.navigation:q=" +
                        upcomingTrips.get(position).getDestLat() + ", " +
                        upcomingTrips.get(position).getDestLong());
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
                            .putStringArrayListExtra("notes", upcomingTrips.get(position).getNotes()));
                }
                holder.btnUpcomingDone.performClick();
            }
        });
        holder.btnUpcomingDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (upcomingTrips.get(position).getType().equals("Round") && !upcomingTrips.get(position).isRoundDone()) {
                    FirebaseDatabase.getInstance().getReference(".info/connected").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean connected = snapshot.getValue(Boolean.class);
                            if (connected) {
                                upcomingTrips.get(position).setStatus("Completed");
                                reference.child(user.getUid()).child("trips").child("history")
                                        .child(upcomingTrips.get(position).getName()).
                                        setValue(upcomingTrips.get(position))
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                            }
                                        });

                                trip = new Trip();
                                trip = upcomingTrips.get(position);
                                trip.swapRound();
                                trip.setStatus("Upcoming");
                                Log.i("UpcomingAdapter", trip.getDestinationName());
                                reference.child(user.getUid()).child("trips").child("upcoming").child(trip.getName()).setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                        onUpcomingEmptyList.update();
                                        if (upcomingTrips.isEmpty()) {
                                            onUpcomingEmptyList.emptyList();
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
                } else {
                    FirebaseDatabase.getInstance().getReference(".info/connected").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean connected = snapshot.getValue(Boolean.class);
                            if (connected) {
                                upcomingTrips.get(position).setStatus("Completed");
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                                        .getCurrentUser().getUid()).child("trips").child("upcoming").child(upcomingTrips.get(position).getName()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                                        .getCurrentUser().getUid()).child("trips").child("history").child(upcomingTrips.get(position).getName()).setValue(upcomingTrips.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        upcomingTrips.remove(position);
                                        notifyDataSetChanged();
                                        if (upcomingTrips.isEmpty()) {
                                            onUpcomingEmptyList.emptyList();
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
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (upcomingTrips != null) {
            return upcomingTrips.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUpcomingName, tvUpcomingDesc, tvUpcomingDate, tvUpcomingTime,
                tvUpcomingSource, tvUpcomingDestination, tvUpcomingType, tvUpcomingDistance;
        ConstraintLayout clExpandable;
        CardView cvUpcoming;
        ImageButton btnUpcomingMenu;
        Button btnStartNavigation, btnUpcomingNote, btnUpcomingDone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUpcomingName = itemView.findViewById(R.id.tvUpcomingName);
            tvUpcomingDesc = itemView.findViewById(R.id.tvUpcomingDesc);
            tvUpcomingDate = itemView.findViewById(R.id.tvUpcomingDate);
            tvUpcomingTime = itemView.findViewById(R.id.tvUpcomingTime);
            tvUpcomingSource = itemView.findViewById(R.id.tvUpcomingSource);
            tvUpcomingDestination = itemView.findViewById(R.id.tvUpcomingDestination);
            tvUpcomingType = itemView.findViewById(R.id.tvUpcomingType);
            tvUpcomingDistance = itemView.findViewById(R.id.tvUpcomingDistance);
            clExpandable = itemView.findViewById(R.id.expandableHistory);
            cvUpcoming = itemView.findViewById(R.id.cvUpcoming);
            btnUpcomingMenu = itemView.findViewById(R.id.btnUpcomingMenu);
            btnStartNavigation = itemView.findViewById(R.id.btnAlarmStart);
            btnUpcomingNote = itemView.findViewById(R.id.btnUpcomingNote);
            btnUpcomingDone = itemView.findViewById(R.id.btnUpcomingDone);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clExpandable.getVisibility() == View.GONE) {
                        TransitionManager.beginDelayedTransition(cvUpcoming, new AutoTransition());
                        clExpandable.setVisibility(View.VISIBLE);
                    } else {
                        TransitionManager.beginDelayedTransition(cvUpcoming, new AutoTransition());
                        clExpandable.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    public void setData(ArrayList<Trip> upcomingTrips) {
        if (this.upcomingTrips != null) {
            this.upcomingTrips.clear();
        } else {
            this.upcomingTrips = upcomingTrips;
            notifyDataSetChanged();
        }
    }
}

interface OnUpcomingEmptyList {
    void emptyList();

    void update();
}