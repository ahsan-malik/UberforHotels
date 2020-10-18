package com.example.uberforhotels.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uberforhotels.Other.Helper;
import com.example.uberforhotels.R;
import com.example.uberforhotels.fragments.UserSelectRoom;
import com.example.uberforhotels.models.Hotel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserHomeAdapter extends RecyclerView.Adapter<UserHomeAdapter.ViewHolder> {

    ArrayList<Hotel> hotels;
    ArrayList<Float> distances;
    boolean isVertical;

    public UserHomeAdapter(ArrayList<Hotel> hotels, ArrayList<Float> distances, boolean isVertical) {
        this.hotels = hotels;
        this.distances = distances;
        this.isVertical = isVertical;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout;
        if (isVertical) {
            layout = R.layout.item_allhotel;
        } else {
            layout = R.layout.item_feature;
        }
        View hotelItem = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(hotelItem);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Hotel hotel = hotels.get(position);

        holder.ratingText.setText(String.format("%.1f", hotel.getAverageRating()/hotel.getNumberOfUserRating()));
        holder.ratingNum.setText("(" + hotel.getNumberOfUserRating() + ")");

        holder.name.setText(hotel.getHotel_name());
        if (hotel.getImageUrl() != null && !hotel.getImageUrl().isEmpty() && !hotel.getImageUrl().equals(""))
            Picasso.get().load(hotel.getImageUrl()).into(holder.img);
        holder.address.setText(hotel.getAddress().getAddressLine());
        holder.distance.setText(String.format("%.1f", distances.get(position)) + "km");

        holder.distance.setOnClickListener(view -> Helper.openGoogleMapForDirection(view.getContext(), hotel));

        holder.itemView.setOnClickListener(view -> ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new UserSelectRoom(hotel)).addToBackStack(null).commit());
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.distance)
        TextView distance;
        @BindView(R.id.ratingStar)
        RatingBar ratingStar;
        @BindView(R.id.ratingText)
        TextView ratingText;
        @BindView(R.id.ratingNum)
        TextView ratingNum;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.address)
        TextView address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
