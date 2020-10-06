package com.example.uberforhotels.Other;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.example.uberforhotels.HotelProfileActivity;
import com.example.uberforhotels.R;
import com.example.uberforhotels.UserProfileActivity;
import com.example.uberforhotels.models.Address;
import com.example.uberforhotels.models.Hotel;
import com.example.uberforhotels.models.Room;
import com.example.uberforhotels.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class DBHelper {
    static DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    static StorageReference sr = FirebaseStorage.getInstance().getReference("Uploads");
    static SharedPreferences prefs;

    public static boolean getCurrentUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    public static void addHotel(Hotel hotel){
        if (hotel.getAddress() != null) {
            db.child("Hotels").child(hotel.getId()).setValue(hotel);
        } else {
        DatabaseReference hotelReference = FirebaseDatabase.getInstance().getReference("Hotels").child(hotel.getId());
        hotelReference.child("id").setValue(hotel.getId());
        hotelReference.child("hotel_name").setValue(hotel.getHotel_name());
        hotelReference.child("email").setValue(hotel.getEmail());
        hotelReference.child("imageUrl").setValue(hotel.getImageUrl());
        hotelReference.child("hotelOpen").setValue(hotel.isHotelOpen());
        }
    }

    public static void addUser(User user){
        db.child("Users").child(user.getPhone()).setValue(user);
    }

    public static void addAddress(Address address, Context context){
        String uid = Helper.getHotelIdFromPreference(context);
        db.child("Hotels").child(uid).child("address").setValue(address).addOnSuccessListener(aVoid -> {
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefs.edit().putString("hotel_city", address.getCity()).apply();
        }).addOnFailureListener(e -> Helper.toast(context, e.getMessage()));

    }

    public static void addRoom(Room room, Context context){
        String uid = Helper.getHotelIdFromPreference(context);
        db.child("Rooms").child(uid).child(String.valueOf(room.getRoom_id())).setValue(room);
    }

    public static void deleteRoom(Room room, Context context){
        String uid = Helper.getHotelIdFromPreference(context);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Are you sure you want to delete this room");
        alertDialog.setIcon(R.drawable.ic_baseline_clear_12);

        alertDialog.setPositiveButton("YES", (dialogInterface, i) -> {
            if (room.getImageUrl() != null){
                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(room.getImageUrl());
                ref.delete().addOnSuccessListener(aVoid -> {
                    db.child("Rooms").child(uid).child(Integer.toString(room.getRoom_id())).removeValue();
                    Helper.toast(context, "Deleted");
                }).addOnFailureListener(e -> Helper.toast(context, e.getMessage()));
            }else {
                db.child("Rooms").child(uid).child(Integer.toString(room.getRoom_id())).removeValue()
                        .addOnSuccessListener(aVoid -> Helper.toast(context, "Deleted"))
                        .addOnFailureListener(e -> Helper.toast(context, e.getMessage()));
            }
        });
        alertDialog.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.show();
    }

    public static void InitializeHotelSettings(final SharedPreferences prefs, final Context context){
        String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        db.child("Hotels").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        String id = snapshot.child("id").getValue(String.class);
                        String name = snapshot.child("hotel_name").getValue(String.class);

                        prefs.edit().putString("hotel_id", id).apply();
                        prefs.edit().putString("hotel_name", name).apply();
                        Helper.setHotelMailInPrefs(snapshot.child("email").getValue(String.class), context);
                        //Helper.toast(context, id);

                       // getCoverImageUrlFromDB(context);
                        //getCurrentHotelAddress(context);

                    }
                }else{
                    Helper.toast(context, "something wrong");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Helper.toast(context, databaseError.getMessage());
            }
        });
    }

    public static void InitializeUserSettings(Context context){
        String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        db.child("Users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        UserPrefs.saveUserState(snapshot.getValue(User.class), context);
                        Helper.LounchActivity(context, UserProfileActivity.class);
                        ((Activity) context).finish();
                    }
                }else{
                    Helper.toast(context, "something wrong");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Helper.toast(context, databaseError.getMessage());
            }
        });
    }


    private static void getCurrentHotelAddress(final Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        db.child("Hotels").child(prefs.getString("hotel_id", "")).child("address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String city;
                if(dataSnapshot.exists())
                    city = dataSnapshot.child("city").getValue(String.class);
                else city = "not set";
                prefs.edit().putString("hotel_city", city).apply();
                Helper.LounchActivity(context, HotelProfileActivity.class);
                ((Activity) context).finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Helper.toast(context, databaseError.getMessage());
            }
        });
    }

    public static void getCoverImageUrlFromDB(final Context context){
        db.child("Hotels").child(Helper.getHotelIdFromPreference(context)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Hotel hotel = snapshot.getValue(Hotel.class);
                    Helper.setPrefsCoverImgUrl(hotel.getImageUrl(), context);
                    if(hotel.getAddress() != null){
                        prefs.edit().putString("hotel_city", hotel.getAddress().getCity()).apply();
                    }
                }
                Helper.LounchActivity(context, HotelProfileActivity.class);
                ((Activity) context).finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Helper.toast(context, error.getMessage());
            }
        });
    }

    public static ArrayList<Room> getAllRooms(final Context context){
        final String uid = Helper.getHotelIdFromPreference(context);
        final ArrayList<Room> rooms = new ArrayList<>();
        db.child("Rooms").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren())
                    rooms.add(snapshot.getValue(Room.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Helper.toast(context, databaseError.getMessage());
            }
        });
        return rooms;
    }

    public static void uploadCoverImage(final Uri imageUri, final Context context){
        if (imageUri != null){
            String fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(imageUri));
            final String hid = Helper.getHotelIdFromPreference(context);
            final StorageReference fileReference = sr.child(hid).child("cover" + "." + fileExtension);
            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> {
                db.child("Hotels").child(hid).child("imageUrl").setValue(uri.toString());
                Helper.setPrefsCoverImgUrl(uri.toString(), context);
                Helper.toast(context, "Image Uploaded");
                ((HotelProfileActivity) context).fillNavigationView();
            })).addOnFailureListener(e -> Helper.toast(context, e.getMessage()));
        }
    }

}
