package com.example.uberforhotels.Other;

import com.example.uberforhotels.models.Hotel;

public class HotelSinglton {
    static Hotel _hotel;

    public static Hotel getHotel(){
        if (_hotel == null)
            _hotel = new Hotel();
        return _hotel;
    }

    public static void setHotel(Hotel hotel){
        _hotel = hotel;
    }
}
