package com.example.uberforhotels.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uberforhotels.Other.DBHelper;
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
import com.hsalf.smileyrating.SmileyRating;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class UserSelectRoom extends Fragment {

    Hotel hotel;
    @BindView(R.id.ratingStar)
    RatingBar ratingStar;
    @BindView(R.id.ratingText)
    TextView ratingText;
    @BindView(R.id.ratingNum)
    TextView ratingNum;

    public UserSelectRoom(Hotel hotel) {
        this.hotel = hotel;
    }


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
    void fillUI() {

        ratingText.setText(String.format("%.1f", hotel.getAverageRating()));
        ratingNum.setText("(" + hotel.getNumberOfUserRating() + ")");

        float[] straightDistance = new float[1];
        Location.distanceBetween(UserPrefs.getLat(getContext()), UserPrefs.getLng(getContext()), hotel.getAddress().getLat(),
                hotel.getAddress().getLng(), straightDistance);

        if (!hotel.getImageUrl().equals("")) Picasso.get().load(hotel.getImageUrl()).into(backImg);
        hotelName.setText(hotel.getHotel_name());
        address.setText(hotel.getAddress().getAddressLine());
        distance.setText("Distance " + String.format("%.1f", straightDistance[0] / 1000) + " km");

        ArrayList<Room> rooms = new ArrayList<>();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Rooms").child(hotel.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rooms.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Room room = dataSnapshot.getValue(Room.class);
                        if (room.getStatus() != null && room.getStatus().equals("Checked"))
                            continue;
                        else rooms.add(dataSnapshot.getValue(Room.class));
                    }
                } else Helper.toast(getContext(), "no room available");
                roomRecyclerView.setAdapter(new UserRoomAdapter(rooms, hotel.getId()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Helper.toast(getContext(), error.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick({R.id.ratingStar, R.id.ratingNum})
    public void onViewClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View rating_form = getLayoutInflater().inflate(R.layout.form_rating, null);
        builder.setView(rating_form);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        TextView titleText = rating_form.findViewById(R.id.text);
        titleText.setText("How was your experiece with " + hotel.getHotel_name());


        SmileyRating smileyRating = rating_form.findViewById(R.id.smile_rating);
        smileyRating.setRating(3);

        TextView submit_button = rating_form.findViewById(R.id.submit);
        TextView cancel_button = rating_form.findViewById(R.id.cancel_button);

        submit_button.setOnClickListener(view1 -> {

            hotel.setNumberOfUserRating(hotel.getNumberOfUserRating() + 1);
            hotel.setAverageRating((hotel.getAverageRating() + smileyRating.getSelectedSmiley().getRating()));

            ratingText.setText(String.format("%.1f", hotel.getAverageRating()));
            ratingNum.setText("(" + hotel.getNumberOfUserRating() + ")");


            DBHelper.addHotel(hotel); //updating hotel rating

            alertDialog.dismiss();
        });

        cancel_button.setOnClickListener(view12 -> alertDialog.dismiss());
    }
}
