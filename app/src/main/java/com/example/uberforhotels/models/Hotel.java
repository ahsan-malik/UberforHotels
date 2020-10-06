package com.example.uberforhotels.models;

public class Hotel {
    String id, hotel_name, email, imageUrl;
    boolean hotelOpen;
    Address address;

    public Hotel(){}

    public Hotel(String id, String hotel_name, String email, String imageUrl, boolean hotelOpen, Address address) {
        this.id = id;
        this.hotel_name = hotel_name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.hotelOpen = hotelOpen;
        this.address = address;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHotel_name() {
        return hotel_name;
    }

    public void setHotel_name(String hotel_name) {
        this.hotel_name = hotel_name;
    }

    public boolean isHotelOpen() {
        return hotelOpen;
    }

    public void setHotelOpen(boolean hotelOpen) {
        this.hotelOpen = hotelOpen;
    }
}
