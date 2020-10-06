package com.example.uberforhotels.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.uberforhotels.Other.DBHelper;
import com.example.uberforhotels.Other.Helper;
import com.example.uberforhotels.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class HomeHotel extends Fragment {

    TextView hotel_name, hotel_city;
    View btn_address, btn_room, btn_setting, btn_img;
    ImageView header_img;

    Uri imageUri;

    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_hotel, container, false);

        hotel_city = view.findViewById(R.id.hotel_city);
        hotel_name = view.findViewById(R.id.hotelName);
        header_img = view.findViewById(R.id.headerImage);
        btn_address = view.findViewById(R.id.address);
        btn_room = view.findViewById(R.id.room);
        btn_img = view.findViewById(R.id.img_pen);
        btn_setting = view.findViewById(R.id.setting);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String coverImageUrl = Helper.getPrefsCoverImgUrl(getContext());
        if (!coverImageUrl.isEmpty()){
            //Drawable drawable = Helper.getDrawableFromUrl(coverImageUrl, getContext());
            //header_img.setBackground(drawable);
            Picasso.get().load(coverImageUrl).into(header_img);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        hotel_name.setText(prefs.getString("hotel_name", "Your Hotel Name"));
        hotel_city.setText(prefs.getString("hotel_city", "City"));
        btn_address.setOnClickListener(onClickListener);
        btn_room.setOnClickListener(onClickListener);
        btn_setting.setOnClickListener(onClickListener);
        btn_img.setOnClickListener(view -> openFileChooser());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(header_img);
            DBHelper.uploadCoverImage(imageUri, getContext());
        }else {
            Helper.toast(getContext(), "No cover selected");
        }
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private View.OnClickListener onClickListener = view -> {
        FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        if (view.getId() == R.id.address)
            transaction.replace(R.id.fragment_container, new Hotel_address());
        else if(view.getId() == R.id.room)
            transaction.replace(R.id.fragment_container, new HotelRooms());
        else if(view.getId() == R.id.setting)
            transaction.replace(R.id.fragment_container, new HotelSettings());
        transaction.addToBackStack(null);
        transaction.commit();
    };
}
