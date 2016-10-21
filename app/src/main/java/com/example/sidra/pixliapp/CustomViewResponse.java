package com.example.sidra.pixliapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sidra on 21-10-2016.
 */

public class CustomViewResponse {

    @SerializedName("Events")
    private List<CustomViewHolder> Events;

    public List<CustomViewHolder> getResults(){
        return Events;
    }
    /*public void setResults(List<CustomViewHolder> results){
        this.results = results;

    }*/
}
