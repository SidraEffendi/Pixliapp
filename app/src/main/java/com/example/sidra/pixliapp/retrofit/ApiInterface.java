package com.example.sidra.pixliapp.retrofit;

import com.example.sidra.pixliapp.CustomViewHolder;
import com.example.sidra.pixliapp.CustomViewPhotosHolder;
import com.example.sidra.pixliapp.CustomViewPhotosResponse;
import com.example.sidra.pixliapp.CustomViewResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by sidra on 21-10-2016.
 */

public interface ApiInterface {

    @GET("/pixget")
    Call<CustomViewResponse> getEvents();

    @GET("/pixget/{event_id}")
    Call<ResponseBody> getEventsExits(@Path("event_id") String eventId);

    @POST("/pixpost/events")
    Call<CustomViewResponse> createTask(@Body CustomViewHolder task);

    @GET("/pixget/photos/{event_id}")
    Call<ResponseBody> getCountOfPhotos(@Path("event_id") String eventId);

    @GET("/pixpost/photos/{event_id}")
    Call<CustomViewPhotosResponse> getPhotosOfEvent(@Path("event_id") String eventId);

    @POST("/pixpost/photos/{event_id}")
    Call<CustomViewPhotosResponse> createPhotos(@Path("event_id") String eventId,@Body CustomViewPhotosHolder task);

}
