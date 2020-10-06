package com.example.uberforhotels.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uberforhotels.Other.DBHelper;
import com.example.uberforhotels.Other.Helper;
import com.example.uberforhotels.R;
import com.example.uberforhotels.adapters.RoomAdapter;
import com.example.uberforhotels.models.Room;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class HotelRooms extends Fragment {

    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    View room_form;
    ImageView formImg;

    Uri formImgUri = null;
    String roomImgUrl = null;

    AlertDialog alertDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        floatingActionButton = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        showList();

        floatingActionButton.setOnClickListener(view -> openRoomForm(null));
    }

    private void showList(){
        String uid = Helper.getHotelIdFromPreference(getContext());
        final ArrayList<Room> rooms = new ArrayList<>();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Rooms").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rooms.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren())
                    rooms.add(snapshot.getValue(Room.class));
                recyclerView.setAdapter(new RoomAdapter(rooms, HotelRooms.this));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Helper.toast(getContext(), databaseError.getMessage());
            }
        });
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    public void openRoomForm(Room room){

        formImgUri = null;
        roomImgUrl = null;

        AlertDialog.Builder dailogBuilder = new AlertDialog.Builder(getContext());
        room_form = getLayoutInflater().inflate(R.layout.form_room, null);
        dailogBuilder.setView(room_form);
        alertDialog = dailogBuilder.create();
        alertDialog.show();

        formImg = room_form.findViewById(R.id.img);

        if (room != null){
            TextInputLayout id_layout = room_form.findViewById(R.id.room_id);
            TextInputLayout rent_layout = room_form.findViewById(R.id.rent);
            TextInputLayout bed_layout = room_form.findViewById(R.id.beds);
            CheckBox wifi_box = room_form.findViewById(R.id.wifi);
            CheckBox avail_box = room_form.findViewById(R.id.available);

            Objects.requireNonNull(id_layout.getEditText()).setText(Integer.toString(room.getRoom_id()));
            Objects.requireNonNull(rent_layout.getEditText()).setText(Double.toString(room.getRent()));
            Objects.requireNonNull(bed_layout.getEditText()).setText(Integer.toString(room.getBeds()));
            wifi_box.setChecked(room.isInternet());
            avail_box.setChecked(room.isAvailable());
            if(room.getImageUrl() != null){
                roomImgUrl = room.getImageUrl();
                Picasso.get().load(roomImgUrl).into(formImg);
            }
        }

        TextView add_button = room_form.findViewById(R.id.add_button);
        TextView cancel_button = room_form.findViewById(R.id.cancel_button);

        formImg.setOnClickListener(view -> openFileChooser());

        add_button.setOnClickListener(view -> addRoom());

        cancel_button.setOnClickListener(view -> alertDialog.dismiss());
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){

            formImgUri = data.getData();
            Picasso.get().load(formImgUri).into(formImg);

        }else {
            Helper.toast(getContext(), "Nothing selected");
        }
    }

    private void addRoom(){
        TextInputLayout id_layout = room_form.findViewById(R.id.room_id);
        TextInputLayout rent_layout = room_form.findViewById(R.id.rent);
        TextInputLayout bed_layout = room_form.findViewById(R.id.beds);
        CheckBox wifi_box = room_form.findViewById(R.id.wifi);
        CheckBox avail_box = room_form.findViewById(R.id.available);

        if(Helper.isEmpty(id_layout) | Helper.isEmpty(rent_layout) | Helper.isEmpty(bed_layout)) return;

        int id =  Integer.parseInt(Objects.requireNonNull(id_layout.getEditText()).getText().toString());
        int beds = Integer.parseInt(Objects.requireNonNull(bed_layout.getEditText()).getText().toString());
        double rent = Double.parseDouble(Objects.requireNonNull(rent_layout.getEditText()).getText().toString());
        boolean wifi = wifi_box.isChecked();
        boolean availability = avail_box.isChecked();

        if (formImgUri != null){
            String hid = Helper.getHotelIdFromPreference(getContext());
            String fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(Objects.requireNonNull(getActivity()).getContentResolver().getType(formImgUri));

            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Uploads");
            StorageReference fileReference = storageReference.child(hid).child(id + "." + fileExtension);

            fileReference.putFile(formImgUri).addOnSuccessListener(taskSnapshot ->
                    Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl()
                            .addOnSuccessListener(uri -> addRoomToDB(new Room(id, beds, wifi, availability, rent, uri.toString()))))
                    .addOnFailureListener(e -> Helper.toast(getContext(), e.getMessage()))
                    .addOnProgressListener(taskSnapshot -> Helper.toast(getContext(), "Uploading..."));
        }
        else if (roomImgUrl != null)
            addRoomToDB(new Room(id, beds, wifi, availability, rent, roomImgUrl));
        else
            addRoomToDB(new Room(id, beds, wifi, availability, rent, null));
    }

    private void addRoomToDB(Room room){
        DBHelper.addRoom(room, getContext());
        Helper.toast(getContext(), "Room Updated");
        alertDialog.dismiss();
    }

}