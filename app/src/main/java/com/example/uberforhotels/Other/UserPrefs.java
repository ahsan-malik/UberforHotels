package com.example.uberforhotels.Other;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.uberforhotels.models.User;

public class UserPrefs {
    static SharedPreferences prefs;

    public static void setName(String name, Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("user_name", name).apply();
    }

    public static String getName(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("user_name", "DEFAULT");
    }

    public static void setMail(String mail, Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("user_mail", mail).apply();
    }

    public static String getMail(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("user_mail", "DEFAULT");
    }

    public static void setPhoneNumber(String phone, Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("user_phone", phone).apply();
    }

    public static String getPhoneNumber(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("user_phone", "DEFAULT");
    }

    public static void saveUserState(User user, Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("user_name", user.getName()).apply();
        prefs.edit().putString("user_mail", user.getEmail()).apply();
        prefs.edit().putString("user_phone", user.getPhone()).apply();
        prefs.edit().putString("user_password", user.getPassword()).apply();
    }
}
