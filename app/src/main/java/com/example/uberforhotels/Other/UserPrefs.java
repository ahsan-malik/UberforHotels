package com.example.uberforhotels.Other;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.uberforhotels.models.Address;
import com.example.uberforhotels.models.User;
import com.google.android.gms.maps.model.LatLng;

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

    public static void setLat(Double lat, Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putFloat("latitude", lat.floatValue()).apply();
    }

    public static double getLat(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return (double) prefs.getFloat("latitude", 0);
    }
    public static double getLng(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return (double) prefs.getFloat("longitude", 0);
    }

    public static LatLng getLatLng(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return new LatLng(
                getLat(context),
                (double) prefs.getFloat("longitude", 0)
        );
    }

    public static void setLng(Double lng, Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putFloat("longitude", lng.floatValue()).apply();
    }

    public static boolean isAddressSaved(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains("longitude"))
            return true;
        return false;
    }

    public static void saveLatLng(Address address, Context context){
        setLat(address.getLat(), context);
        setLng(address.getLng(), context);
    }

    public static void saveUserState(User user, Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("user_name", user.getName()).apply();
        prefs.edit().putString("user_mail", user.getEmail()).apply();
        prefs.edit().putString("user_phone", user.getPhone()).apply();
        prefs.edit().putString("user_password", user.getPassword()).apply();
        if (user.getAddress() == null){
            prefs.edit().remove("longitude");
            prefs.edit().remove("latitude");
        }else{
            prefs.edit().putFloat("longitude", (float) user.getAddress().getLng()).apply();
            prefs.edit().putFloat("latitude", (float) user.getAddress().getLat()).apply();
        }
    }
}
