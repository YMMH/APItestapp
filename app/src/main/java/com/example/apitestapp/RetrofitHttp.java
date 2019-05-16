package com.example.apitestapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitHttp {
    @GET("/api/{v1}/locations")///?format=json")
    Call<List<UserLocation>> user_locations(
            @Path("v1") String name);//

}
