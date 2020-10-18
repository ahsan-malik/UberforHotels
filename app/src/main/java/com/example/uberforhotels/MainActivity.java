package com.example.uberforhotels;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uberforhotels.Other.DBHelper;
import com.example.uberforhotels.Other.Helper;
import com.example.uberforhotels.Other.HotelSinglton;
import com.example.uberforhotels.auth.HotelSignupActivity;
import com.example.uberforhotels.auth.LoginActivity;
import com.example.uberforhotels.auth.UserSignupActivity;
import com.example.uberforhotels.models.Hotel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    //boolean isHotel;
    SharedPreferences prefs;
    @BindView(R.id.loader)
    LinearLayout loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (DBHelper.getCurrentUser()) {
            if (prefs.getBoolean("isHotel", true)) {
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                db.child("Hotels").child(Helper.getHotelIdFromPreference(this)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            HotelSinglton.setHotel(snapshot.getValue(Hotel.class));
                            Helper.LounchActivity(getApplicationContext(), HotelProfileActivity.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Helper.toast(getApplicationContext(), error.getMessage());
                    }
                });
            } else Helper.LounchActivity(this, UserProfileActivity.class);
            finish();
        }
        loader.setVisibility(View.GONE);
    }

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.reg_hotel:
                //startActivity(new Intent(MainActivity.this, HotelSignupActivity.class));
                Helper.LounchActivity(MainActivity.this, HotelSignupActivity.class);
                break;
            case R.id.reg_user:
                startActivity(new Intent(MainActivity.this, UserSignupActivity.class));
                break;
            case R.id.login:
                Helper.LounchActivity(MainActivity.this, LoginActivity.class);

        }
    }
}
