package com.example.apitestapp;
//variable names have to same with json data tag
public class UserLocation{
    //get info
    public final String name;
    public final String lat;
    public final String lng;

    public UserLocation(String name, String lat, String lng)
    {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }
}