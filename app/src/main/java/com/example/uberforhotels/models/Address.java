package com.example.uberforhotels.models;

public class Address {
    private double lat;
    private double lng;
    private String addressLine, city;

    public Address() {
        lat = 0;
        lng = 0;
        addressLine = null;
        city = null;
    }

    public Address(double lat, double lng, String addressLine, String city) {
        this.lat = lat;
        this.lng = lng;
        this.addressLine = addressLine;
        this.city = city;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
