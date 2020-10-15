package com.example.uberforhotels.fragments;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uberforhotels.Other.Helper;
import com.example.uberforhotels.Other.UserPrefs;
import com.example.uberforhotels.R;
import com.example.uberforhotels.adapters.UserRoomAdapter;
import com.example.uberforhotels.models.Hotel;
import com.example.uberforhotels.models.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class UserSelectRoom extends Fragment {

    Hotel hotel;
    public UserSelectRoom(Hotel hotel){this.hotel = hotel;}


    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.hotelName)
    TextView hotelName;
    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.address)
    TextView address;

    Unbinder unbinder;
    @BindView(R.id.roomRecyclerView)
    RecyclerView roomRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_select_room, container, false);
        unbinder = ButterKnife.bind(this, view);

        roomRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fillUI();
    }

    @OnClick(R.id.gerDirection)
    public void onViewClicked() {
        Helper.openGoogleMapForDirection(getContext(), hotel);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    void fillUI(){

        float[] straightDistance = new float[1];
        Location.distanceBetween(UserPrefs.getLat(getContext()), UserPrefs.getLng(getContext()), hotel.getAddress().getLat(),
                hotel.getAddress().getLng(), straightDistance);

        Picasso.get().load(hotel.getImageUrl()).into(backImg);
        hotelName.setText(hotel.getHotel_name());
        address.setText(hotel.getAddress().getAddressLine());
        distance.setText("Distance " + String.format("%.1f", straightDistance[0]/1000) + " km");

        ArrayList<Room> rooms = new ArrayList<>();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Rooms").child(hotel.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rooms.clear();
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Room room = dataSnapshot.getValue(Room.class);
                        if (room.getStatus() != null && room.getStatus().equals("Checked"))
                            continue;
                        else rooms.add(dataSnapshot.getValue(Room.class));
                    }
                    roomRecyclerView.setAdapter(new UserRoomAdapter(rooms));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
