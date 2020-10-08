package com.example.uberforhotels.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uberforhotels.Other.DBHelper;
import com.example.uberforhotels.R;
import com.example.uberforhotels.fragments.HotelRooms;
import com.example.uberforhotels.models.Room;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    HotelRooms hotelRoomsFragment;
    ArrayList<Room> rooms;

    public RoomAdapter(ArrayList<Room> rooms, HotelRooms hotelRoomsFragment) {
        this.rooms = rooms;
        this.hotelRoomsFragment = hotelRoomsFragment;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View roomItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(roomItem);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        final Room room = rooms.get(position);
        holder.id.setText("Room-" + room.getRoom_id());
        holder.rent.setText("Rs " + room.getRent());
        holder.bed.setText(": " + room.getBeds());
        if (room.getStatus() != null)
            holder.status.setText(room.getStatus());
        if (room.getImageUrl() != null)
            Picasso.get().load(room.getImageUrl()).into(holder.img);
        else
            Picasso.get().load(R.drawable.door).into(holder.img);
        if(room.isInternet())
            holder.wifi.setImageResource(R.drawable.ic_baseline_wifi_12);
        else
            holder.wifi.setImageResource(R.drawable.ic_baseline_wifi_off_12);

        holder.itemView.setOnClickListener(view -> hotelRoomsFragment.openRoomForm(room));

        holder.itemView.setOnLongClickListener(view -> {
            DBHelper.deleteRoom(room, view.getContext());
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {

       TextView id, bed, rent, status;
       ImageView wifi, img;

       public RoomViewHolder(@NonNull View itemView) {
           super(itemView);
           id = itemView.findViewById(R.id.text_id);
           rent = itemView.findViewById(R.id.rent);
           bed = itemView.findViewById(R.id.bedText);
           wifi = itemView.findViewById(R.id.wifiImg);
           img = itemView.findViewById(R.id.img);
           status= itemView.findViewById(R.id.statusText);
       }
    }
}

