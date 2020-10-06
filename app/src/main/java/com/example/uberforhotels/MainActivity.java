package com.example.uberforhotels;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.example.uberforhotels.Other.DBHelper;
import com.example.uberforhotels.Other.Helper;
import com.example.uberforhotels.auth.HotelSignupActivity;
import com.example.uberforhotels.auth.LoginActivity;
import com.example.uberforhotels.auth.UserSignupActivity;

public class MainActivity extends AppCompatActivity {

    //boolean isHotel;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if(DBHelper.getCurrentUser()){
            if(prefs.getBoolean("isHotel", true))
                Helper.LounchActivity(this, HotelProfileActivity.class);
            else Helper.LounchActivity(this, UserProfileActivity.class);
            finish();
        }
    }

    public void btnClick(View view){
        switch (view.getId()){
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
