package com.example.sidra.pixliapp;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sidra on 21-10-2016.
 */

public class CustomViewHolder {

    @SerializedName("id")
    private Integer id;


    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    public CustomViewHolder(String name, String description){
        this.name = name;
        this.description = description;

        //this.id = id;
    }

    public Integer getid(){
        return id;
    }
    /*public void setid(Integer id){
        this.id = id;
    }*/

    public String gettt()
    {
        return name;
    }
    public void settt(String name){
        this.name = name;
    }

    public String getedi(){
        return description;
    }
    public void setedi(String description){
        this.description = description;
    }
}
