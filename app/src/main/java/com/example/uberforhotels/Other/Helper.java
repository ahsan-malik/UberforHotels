package com.example.uberforhotels.Other;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.uberforhotels.models.Hotel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class Helper {

    static SharedPreferences prefs;

    public static void LounchActivity(Context packageContext, Class<?> clas){
        packageContext.startActivity(new Intent(packageContext, clas));
    }

    public static void toast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static String getHotelIdFromPreference(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("hotel_id", "DEFAULT");
    }

    public static boolean isHotel(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("isHotel", false);
    }

    public static void setHotelNameInPrefs(String name, Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("hotel_name", name).apply();
    }

    public static String getHotelNameFromPrefs(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("hotel_name", "DEFAULT");
    }

    public static void setHotelOpenInPrefs(boolean tf, Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean("hotel_open", tf).apply();
    }

    public static boolean getIsHotelOpenFromPrefs(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("hotel_open", true);
    }

    public static void setHotelMailInPrefs(String mail, Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("hotelMail", mail).apply();
    }

    public static String getHotelMailFromPrefs(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("hotelMail", "DEFAULT");
    }

    public static boolean isEmpty(TextInputLayout textInputLayout){
        String text = Objects.requireNonNull(textInputLayout.getEditText()).getText().toString();
        if (text.equals("") || text.isEmpty() || text.contains(" ")){
            textInputLayout.setError("Field can't be empty");
            return true;
        }
        textInputLayout.setError(null);
        return false;
    }

    public static void setPrefsCoverImgUrl(String url, Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("coverImage", url).apply();
    }

    public static String getPrefsCoverImgUrl(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("coverImage", null);
    }

    public static void openGoogleMapForDirection(Context context, Hotel hotel){
        //String uri = String.format(Locale.ENGLISH, "geo:%f,%f", hotel.getAddress().getLat(), hotel.getAddress().getLng());

        LatLng userLatLng = UserPrefs.getLatLng(context);
        String uri = "http://maps.google.com/maps?saddr=" + userLatLng.latitude + "," + userLatLng.longitude
                + "&daddr=" + hotel.getAddress().getLat() + "," + hotel.getAddress().getLng();

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        context.startActivity(intent);
    }

}
