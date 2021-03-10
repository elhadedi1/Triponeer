package com.app.triponeer;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class History extends Fragment implements OnHistoryEmptyList {
    RecyclerView rvHistory;
    HistoryAdapter historyAdapter;
    ArrayList<Trip> historyTrips;
    SwipeRefreshLayout swipeRefreshLayoutHistory;
    public static LinearLayout historyEmptyLayout;
    ArrayList<String> notes;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private DatabaseReference reference;
    Trip trip;

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        swipeRefreshLayoutHistory = view.findViewById(R.id.swipeRefreshLayoutHistory);
        historyEmptyLayout = view.findViewById(R.id.historyEmptyLayout);
        swipeRefreshLayoutHistory.setColorSchemeResources(R.color.colorPrimary);
        historyTrips = new ArrayList<>();
        notes = new ArrayList<>();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.keepSynced(true);
        trip = new Trip();

        historyAdapter = new HistoryAdapter(getContext(), History.this);
        rvHistory.setAdapter(historyAdapter);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        if(reference != null && user != null)
        {
            getData();
        }


        swipeRefreshLayoutHistory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayoutHistory.setRefreshing(false);
            }
        });

        return view;
    }

    private void setDataSource() {
        if (historyAdapter != null) {
            historyAdapter.setData(historyTrips);
        }
    }

    private void getData() {
        historyTrips.clear();
        reference.child(user.getUid()).child("trips").child("history").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    trip = dataSnapshot.getValue(Trip.class);
                    if (trip != null) {
                        historyTrips.add(trip);
                    }
                    historyAdapter = new HistoryAdapter(getContext(), History.this);
                    if (!historyTrips.isEmpty()) {
                        rvHistory.setAdapter(historyAdapter);
                        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                }
                if (!historyTrips.isEmpty()) {
                    historyEmptyLayout.setVisibility(View.INVISIBLE);
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
        historyEmptyLayout.setVisibility(View.VISIBLE);
    }
}