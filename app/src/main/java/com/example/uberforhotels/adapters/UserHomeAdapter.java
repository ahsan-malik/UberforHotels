package com.example.uberforhotels.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uberforhotels.Other.UserPrefs;
import com.example.uberforhotels.R;
import com.example.uberforhotels.fragments.HomeHotel;
import com.example.uberforhotels.fragments.UserSelectRoom;
import com.example.uberforhotels.models.Hotel;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
        if (isVertical) { layout = R.layout.item_allhotel; }else { layout = R.layout.item_feature; }
        View hotelItem = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(hotelItem);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Hotel hotel = hotels.get(position);

        holder.name.setText(hotel.getHotel_name());
        if (hotel.getImageUrl() != null && !hotel.getImageUrl().isEmpty() && !hotel.getImageUrl().equals(""))
            Picasso.get().load(hotel.getImageUrl()).into(holder.img);
        holder.address.setText(hotel.getAddress().getAddressLine());
        holder.distance.setText(String.format("%.1f", distances.get(position)) + "km");

        holder.distance.setOnClickListener(view -> {

            //String uri = String.format(Locale.ENGLISH, "geo:%f,%f", hotel.getAddress().getLat(), hotel.getAddress().getLng());

            LatLng userLatLng = UserPrefs.getLatLng(view.getContext());
            String uri = "http://maps.google.com/maps?saddr=" + userLatLng.latitude + "," + userLatLng.longitude
                    + "&daddr=" + hotel.getAddress().getLat() + "," + hotel.getAddress().getLng();

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            view.getContext().startActivity(intent);
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new UserSelectRoom()).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, address, distance;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            distance = itemView.findViewById(R.id.distance);
        }
    }
}
