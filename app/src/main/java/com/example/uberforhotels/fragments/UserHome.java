package com.example.uberforhotels.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uberforhotels.Other.Helper;
import com.example.uberforhotels.R;
import com.example.uberforhotels.adapters.UserHomeAdapter;
import com.example.uberforhotels.models.Hotel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserHome extends Fragment {

    @BindView(R.id.featureRecyclerView)
    RecyclerView featureRecyclerView;
    @BindView(R.id.allHotelRecyclerView)
    RecyclerView allHotelRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);
        ButterKnife.bind(this, view);
        featureRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        allHotelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        ArrayList<Hotel> hotels = new ArrayList<>();
        db.child("Hotels").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hotels.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    hotels.add(dataSnapshot.getValue(Hotel.class));

                featureRecyclerView.setAdapter(new UserHomeAdapter(hotels, false));
                allHotelRecyclerView.setAdapter(new UserHomeAdapter(hotels, true));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Helper.toast(getContext(), error.getMessage());
            }
        });
    }
}