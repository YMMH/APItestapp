package com.example.apitestapp;
//variable names have to same with json data tag
public class UserLocation{
    //get info
    public final String name;
    public final double lat;
    public final double lng;

    public UserLocation(String name, double lat, double lng)
    {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }
}