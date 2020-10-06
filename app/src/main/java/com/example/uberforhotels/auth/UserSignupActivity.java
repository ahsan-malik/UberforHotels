package com.example.uberforhotels.auth;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.example.uberforhotels.Other.DBHelper;
import com.example.uberforhotels.Other.Helper;
import com.example.uberforhotels.Other.UserPrefs;
import com.example.uberforhotels.R;
import com.example.uberforhotels.UserProfileActivity;
import com.example.uberforhotels.models.User;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserSignupActivity extends AppCompatActivity {

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.loader)
    LazyLoader loader;

    FirebaseAuth auth;
    SharedPreferences prefs;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
        ButterKnife.bind(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
    }

    @OnClick({R.id.sup, R.id.lin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sup:
                if(fieldValidation()){
                    loader.setVisibility(View.VISIBLE);
                    UserSignUp();
                }
                break;
            case R.id.lin:
                Helper.LounchActivity(this, LoginActivity.class);
                break;
        }
    }

    private void UserSignUp(){
        final String phoneNo = phone.getText().toString().trim();
        final String userName = name.getText().toString().trim();
        final String mail = email.getText().toString().trim();
        String pswrd = password.getText().toString().trim();

        db.child("Users").child(phoneNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Helper.toast(UserSignupActivity.this, phoneNo + " This Phone Number already used");
                    loader.setVisibility(View.INVISIBLE);
                }else {
                    auth.createUserWithEmailAndPassword(mail, pswrd).addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            User user = new User(userName, mail, phoneNo, pswrd, null);
                            DBHelper.addUser(user);
                            loader.setVisibility(View.INVISIBLE);
                            prefs.edit().putBoolean("isHotel", false).apply();
                            UserPrefs.saveUserState(user, UserSignupActivity.this);
                            Helper.LounchActivity(UserSignupActivity.this, UserProfileActivity.class);
                            finish();
                        }else {
                            if(task.getException() instanceof FirebaseNetworkException)
                                Helper.toast(getApplicationContext(), "Network Error, Check your internet connection");
                            else if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                Helper.toast(getApplicationContext(), "user exist already");
                            loader.setVisibility(View.INVISIBLE);
                        }
                    }).addOnFailureListener(e -> Helper.toast(getApplicationContext(), e.getMessage()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Helper.toast(UserSignupActivity.this, error.getMessage());
                loader.setVisibility(View.INVISIBLE);
            }
        });
    }

    private boolean fieldValidation() {
        if (TextUtils.isEmpty(name.getText().toString().trim())) {
            name.setError("Name not set!");
            return false;
        }
        if (TextUtils.isEmpty(email.getText().toString().trim())) {
            email.setError("Email is required!");
            return false;
        }
        if(TextUtils.isEmpty(password.getText().toString().trim())){
            password.setError("password is required");
            return false;
        }
        if (password.length() < 6) {
            password.setError("Password should have minimum of 6 characters!");
            return false;
        }
        if(TextUtils.isEmpty(phone.getText().toString().trim())){
            phone.setError("phone is required");
            return false;
        }
        if (phone.length() != 11) {
            phone.setError("Incorrect phone number");
            return false;
        }

        return true;
    }
}
