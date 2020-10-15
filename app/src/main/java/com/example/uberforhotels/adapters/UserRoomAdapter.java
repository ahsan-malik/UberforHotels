package com.example.uberforhotels.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uberforhotels.Other.Helper;
import com.example.uberforhotels.R;
import com.example.uberforhotels.models.Room;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserRoomAdapter extends RecyclerView.Adapter<UserRoomAdapter.ViewHolder> {

    ArrayList<Room> rooms;

    public UserRoomAdapter(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View roomView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_room, parent, false);
        return new ViewHolder(roomView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Room room = rooms.get(position);

        if (room.getImageUrl() != null)
            Picasso.get().load(room.getImageUrl()).into(holder.img);
        if (!room.isInternet())
            holder.internet.setVisibility(View.INVISIBLE);
        if (room.getBeds() == 1)
            holder.bed.setText("Single Bed");
        else holder.bed.setText("Double Bed");
        if (room.getStatus() != null && room.getStatus().equals("Reserved"))
            holder.status.setText("Reserved");
        else holder.status.setVisibility(View.INVISIBLE);
        holder.price.setText("Rs "+room.getRent());
        holder.roomId.setText("Room No: "+ room.getRoom_id());

        holder.bookBtn.setOnClickListener(view -> Helper.toast(holder.itemView.getContext(), "you reserved the room "));

    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.room_id)
        TextView roomId;
        @BindView(R.id.internet)
        LinearLayout internet;
        @BindView(R.id.bookBtn)
        LinearLayout bookBtn;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.bed)
        TextView bed;
        @BindView(R.id.status)
        TextView status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
