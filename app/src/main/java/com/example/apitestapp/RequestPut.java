package com.example.apitestapp;

import java.util.HashMap;

public class RequestPut {

    public final int id;
    public final String name;
    public final double lat;
    public final double lng;

    public RequestPut(HashMap<String, Object> param){
        this.id = (int)param.get("id");
        this.name = (String)param.get("name");
        this.lat = (double)param.get("lat");
        this.lng = (double)param.get("lng");

    }
}
