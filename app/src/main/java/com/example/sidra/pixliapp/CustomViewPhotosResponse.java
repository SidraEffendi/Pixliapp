package com.example.sidra.pixliapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sidra on 25-10-2016.
 */

public class CustomViewPhotosResponse {

    @SerializedName("Photos")
    private List<CustomViewPhotosHolder> Photos;

    public List<CustomViewPhotosHolder> getResults(){
        return Photos;
    }
}
