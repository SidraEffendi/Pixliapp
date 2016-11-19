package in.pixli.android;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sidra on 21-10-2016.
 *
 * This file holds the json list for the Photos table of database.
 */

public class CustomViewResponse {

    @SerializedName("CreateEventsActivity")
    private List<CustomViewHolder> Events;

    public List<CustomViewHolder> getResults(){
        return Events;
    }
    /*public void setResults(List<CustomViewHolder> results){
        this.results = results;

    }*/
}
