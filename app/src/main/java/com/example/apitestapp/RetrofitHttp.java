package com.example.apitestapp;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitHttp {
    @GET("/api/{v1}/locations")
    Call<List<UserLocation>> user_locations(
            @Path("v1") String name);//

    @PUT("/api/v1/locations/{name}/")
    Call<UserLocation>  put_location(
            @Body RequestPut param);

    @FormUrlEncoded
    @POST("/api/v1/locations/")
    Call<UserLocation> create_user(@Field("name") String name,
                                   @Field("lat") double lat,
                                   @Field("lng") double lng);

    //@DELETE("/api/vi/locations/1")
    //돌려받는 것이 없을 때 ResponseBody 사용
    //@HTTP(method = "DELETE", path = "/api/v1/locations/2", hasBody = true)
    //Call<Void> delete_user();

    @DELETE("/api/v1/locations/{id}/")//last '/' was essential
    Call<Void> delete_user(@Path("id") int id);

}
