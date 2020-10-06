package com.example.uberforhotels.models;

public class Room {
    int room_id, beds;
    boolean internet, available;
    double rent;
    String imageUrl;

    public Room() {

    }

    public Room(int room_id, int beds, boolean internet, boolean available, double rent, String imageUrl) {
        this.room_id = room_id;
        this.beds = beds;
        this.internet = internet;
        this.available = available;
        this.rent = rent;
        this.imageUrl = imageUrl;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public boolean isInternet() {
        return internet;
    }

    public void setInternet(boolean internet) {
        this.internet = internet;
    }

    public double getRent() {
        return rent;
    }

    public void setRent(double rent) {
        this.rent = rent;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
