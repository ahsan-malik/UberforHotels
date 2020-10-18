package com.example.uberforhotels.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.example.uberforhotels.HotelProfileActivity;
import com.example.uberforhotels.Other.Helper;
import com.example.uberforhotels.Other.HotelSinglton;
import com.example.uberforhotels.Other.UserPrefs;
import com.example.uberforhotels.R;
import com.example.uberforhotels.UserProfileActivity;
import com.example.uberforhotels.models.Hotel;
import com.example.uberforhotels.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    EditText email_et, password_et;
    RadioButton hotel_r, user_r;
    LazyLoader loader;

    SharedPreferences prefs;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_et = findViewById(R.id.email);
        password_et = findViewById(R.id.password);
        hotel_r = findViewById(R.id.radio_hotel);
        user_r = findViewById(R.id.radio_user);
        loader = findViewById(R.id.loader);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        db = FirebaseDatabase.getInstance().getReference();
    }

    public void btnClick(View view){
        if(view.getId() == R.id.login)
            if(fieldValidation()){
                if (hotel_r.isChecked())
                    logInHotel();
                else if (user_r.isChecked())
                    logInUser();
            }
    }

    public void logInUser(){
        String email = email_et.getText().toString().trim();
        String password = password_et.getText().toString();
        db.child("Users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                assert user != null;
                                UserPrefs.saveUserState(user, getApplicationContext());
                                prefs.edit().putBoolean("isHotel", false).apply();
                                Helper.LounchActivity(LoginActivity.this, UserProfileActivity.class);
                                finish();
                            }
                        }).addOnFailureListener(e -> {
                            Helper.toast(getApplicationContext(), e.getMessage());
                            loader.setVisibility(View.INVISIBLE);
                        });
                    }
                }else{
                    Helper.toast(getApplicationContext(), "User not found");
                    loader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Helper.toast(getApplicationContext(), databaseError.getMessage());
                loader.setVisibility(View.GONE);
            }
        });
    }

    private void logInHotel(){
        String mail = email_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();

        db.child("Hotels").orderByChild("email").equalTo(mail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Hotel hotel = dataSnapshot.getValue(Hotel.class);
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(mail, password).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                assert hotel != null;
                                prefs.edit().putBoolean("isHotel", true).apply();
                                prefs.edit().putString("hotel_id", hotel.getId()).apply();
                                prefs.edit().putString("hotel_name", hotel.getHotel_name()).apply();
                                if (hotel.getAddress() != null)
                                    prefs.edit().putString("hotel_city", hotel.getAddress().getCity()).apply();
                                else prefs.edit().putString("hotel_city", "not set").apply();
                                Helper.setHotelMailInPrefs(mail, getApplicationContext());
                                Helper.setPrefsCoverImgUrl(hotel.getImageUrl(), LoginActivity.this);

                                Helper.setAverageRatingInPrefs(hotel.getAverageRating(), getApplicationContext());
                                Helper.setNumberOfRatingInPrefs(hotel.getNumberOfUserRating(), getApplicationContext());

                                HotelSinglton.setHotel(hotel);

                                Helper.LounchActivity(LoginActivity.this, HotelProfileActivity.class);
                                finish();
                                //DBHelper.getCoverImageUrlFromDB(LoginActivity.this);
                            }
                        }).addOnFailureListener(e -> Helper.toast(getApplicationContext(), e.getMessage()));
                    }
                } else{
                    Helper.toast(getApplicationContext(), "not found");
                    loader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Helper.toast(getApplicationContext(), error.getMessage());
                loader.setVisibility(View.GONE);
            }
        });
    }

    private boolean fieldValidation() {
        if (TextUtils.isEmpty(email_et.getText().toString().trim())) {
            email_et.setError("UserName is required!");
            return false;
        }
        if (TextUtils.isEmpty(password_et.getText().toString().trim())) {
            password_et.setError("Password is required!");
            return false;
        }
        if(!hotel_r.isChecked() && !user_r.isChecked()){
            hotel_r.setError("Please Select one!");
            return false;
        }
        hotel_r.setError(null);
        loader.setVisibility(View.VISIBLE);
        return true;
    }
}
