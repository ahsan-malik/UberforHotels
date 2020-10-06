package com.example.uberforhotels.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uberforhotels.R;
import com.example.uberforhotels.models.Hotel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserHomeAdapter extends RecyclerView.Adapter<UserHomeAdapter.ViewHolder> {

    ArrayList<Hotel> hotels;
    boolean isVertical;

    public UserHomeAdapter(ArrayList<Hotel> hotels, boolean isVertical) {
        this.hotels = hotels;
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Hotel hotel = hotels.get(position);

        holder.name.setText(hotel.getHotel_name());
        if (hotel.getImageUrl() != null && !hotel.getImageUrl().isEmpty() && !hotel.getImageUrl().equals(""))
            Picasso.get().load(hotel.getImageUrl()).into(holder.img);
        if (hotel.getAddress() != null)
            holder.address.setText(hotel.getAddress().getAddressLine());
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, address;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);

        }
    }
}
