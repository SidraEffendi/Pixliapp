package in.pixli.android;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sidra on 25-10-2016.
 *
 * This file holds the json list for the Event table of database.
 */

public class CustomViewPhotosResponse {

    @SerializedName("Photos")
    private List<CustomViewPhotosHolder> Photos;

    public List<CustomViewPhotosHolder> getResults(){
        return Photos;
    }
}
