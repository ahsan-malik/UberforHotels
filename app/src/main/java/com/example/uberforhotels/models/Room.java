package com.example.uberforhotels.models;

public class Room {
    int room_id, beds;
    boolean internet;
    double rent;
    String imageUrl, status;

    public Room() {

    }

    public Room(int room_id, int beds, boolean internet, double rent, String status, String imageUrl) {
        this.room_id = room_id;
        this.beds = beds;
        this.internet = internet;
        this.rent = rent;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
