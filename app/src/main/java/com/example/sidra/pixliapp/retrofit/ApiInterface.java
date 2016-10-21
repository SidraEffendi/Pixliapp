package com.example.sidra.pixliapp.retrofit;

import com.example.sidra.pixliapp.CustomViewHolder;
import com.example.sidra.pixliapp.CustomViewResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by sidra on 21-10-2016.
 */

public interface ApiInterface {

    @POST("/pixpost/new")
    Call<CustomViewResponse> createTask(@Body CustomViewHolder task);
}
