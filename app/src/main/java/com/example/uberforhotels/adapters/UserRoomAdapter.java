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

import com.example.uberforhotels.Other.DBHelper;
import com.example.uberforhotels.Other.Helper;
import com.example.uberforhotels.R;
import com.example.uberforhotels.models.Room;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserRoomAdapter extends RecyclerView.Adapter<UserRoomAdapter.ViewHolder> {

    ArrayList<Room> rooms;
    String hotelID;

    public UserRoomAdapter(ArrayList<Room> rooms, String hotelID) {
        this.rooms = rooms;
        this.hotelID = hotelID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View roomView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_room, parent, false);
        return new ViewHolder(roomView);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Room room = rooms.get(position);

        if (room.getImageUrl() != null){ Picasso.get().load(room.getImageUrl()).into(holder.img);}
        if (!room.isInternet()){ holder.internet.setVisibility(View.INVISIBLE);}else {holder.internet.setVisibility(View.VISIBLE);}
        if (room.getBeds() == 1) {holder.bed.setText("Single Bed");} else {holder.bed.setText("Double Bed");}
        if (room.getStatus() == null || !room.getStatus().equals("Reserved")) { holder.status.setVisibility(View.INVISIBLE); }
        else { holder.status.setVisibility(View.VISIBLE);holder.status.setText("Reserved"); }
        holder.price.setText("Rs "+room.getRent());
        holder.roomId.setText("Room No: "+ room.getRoom_id());

        holder.bookBtn.setOnClickListener(view -> {
            if (room.getStatus() == null || !room.getStatus().equals("Reserved")){
                Visible(holder.status);
                room.setStatus("Reserved");
                holder.status.setText(room.getStatus());
                DBHelper.updateRoom(hotelID, room);
                //Helper.toast(holder.itemView.getContext(), "you reserved the room ");
            }else
                Helper.toast(holder.itemView.getContext(), "Room is reserved already");
        });

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

    private void Visible(View view){
        view.setVisibility(View.VISIBLE);
    }
    private void Invisible(View view){
        view.setVisibility(View.INVISIBLE);
    }
}
