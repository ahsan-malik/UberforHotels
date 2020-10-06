package com.example.uberforhotels.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.example.uberforhotels.HotelProfileActivity;
import com.example.uberforhotels.Other.DBHelper;
import com.example.uberforhotels.Other.Helper;
import com.example.uberforhotels.R;
import com.example.uberforhotels.models.Hotel;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HotelSignupActivity extends AppCompatActivity {

    EditText id_text, name_text, email_text, password_text;
    LazyLoader loader;
    SharedPreferences prefs;

    private FirebaseAuth auth;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_signup);

        id_text = findViewById(R.id._id);
        name_text = findViewById(R.id.name);
        email_text = findViewById(R.id.email);
        password_text = findViewById(R.id.password);
        loader = findViewById(R.id.loader);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
    }

    public void btnClick(View view){
        if(view.getId() == R.id.sup){
            if(fieldValidation()){
                loader.setVisibility(View.VISIBLE);
                hotelSignUp();
            }
        }else{
            Helper.LounchActivity(HotelSignupActivity.this, LoginActivity.class);
        }
    }

    private void hotelSignUp(){
        final String id = id_text.getText().toString().trim();
        final String name = name_text.getText().toString().trim();
        final String email = email_text.getText().toString().trim();
        String password = password_text.getText().toString().trim();

        db.child("Hotels").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    loader.setVisibility(View.GONE);
                    Helper.toast(getApplicationContext(), "Id already taken! choose another one");
                }else {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            DBHelper.addHotel(new Hotel(id, name, email, "", true, null));
                            Helper.toast(getApplicationContext(), "User added");
                            loader.setVisibility(View.INVISIBLE);
                            prefs.edit().putBoolean("isHotel", true).apply();
                            prefs.edit().putString("hotel_id", id).apply();
                            prefs.edit().putString("hotel_name", name).apply();
                            prefs.edit().putString("hotel_city", "not_set").apply();
                            Helper.setHotelMailInPrefs(email, getApplicationContext());
                            Helper.setHotelOpenInPrefs(true, getApplicationContext());
                            Helper.setPrefsCoverImgUrl("", getApplicationContext());
                            Helper.LounchActivity(HotelSignupActivity.this, HotelProfileActivity.class);
                            finish();
                        }else {
                            if(task.getException() instanceof FirebaseNetworkException)
                                Helper.toast(getApplicationContext(), "Network Error, Check your internet connection");
                            else if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                Helper.toast(getApplicationContext(), "user exist already");
                            loader.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Helper.toast(HotelSignupActivity.this, error.getMessage());
                loader.setVisibility(View.INVISIBLE);
            }
        });
    }

    private boolean fieldValidation() {
        if (TextUtils.isEmpty(id_text.getText().toString().trim())) {
            id_text.setError("id is required!");
            return false;
        }
        if (TextUtils.isEmpty(name_text.getText().toString().trim())) {
            name_text.setError("Name not set!");
            return false;
        }
        if (TextUtils.isEmpty(email_text.getText().toString().trim())) {
            email_text.setError("Email is required!");
            return false;
        }
        if(TextUtils.isEmpty(password_text.getText().toString().trim())){
            password_text.setError("password is required");
            return false;
        }
        if (password_text.length() < 6) {
            password_text.setError("Password should have minimum of 6 characters!");
            return false;
        }

        return true;
    }
}
