package in.pixli.android;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sidra on 21-10-2016.
 *
 * This file holds the json format for the Event table of database.
 */

public class CustomViewHolder {

    @SerializedName("id")
    private Integer id;

    @SerializedName("code_id")
    private String code_id;

    @SerializedName("event_type")
    private String event_type;

    @SerializedName("album_name")
    private String album_name;

    @SerializedName("event_date")
    private String event_date;

    @SerializedName("event_loc")
    private String event_loc;

    @SerializedName("bucket_link")
    private String bucket_link;


    public CustomViewHolder(){}

    public CustomViewHolder(String code_id, String event_type, String album_name, String event_date, String event_loc, String bucket_link,Boolean authority) {
        this.code_id = code_id;
        this.event_type = event_type;
        this.album_name = album_name;
        this.event_date = event_date;
        this.event_loc = event_loc;
        this.bucket_link = bucket_link;
    }

    public String getCode_id(){
        return code_id;
    }
    public void setCode_id(String code_id){
        this.code_id = code_id;
    }

    public String getEvent_type()
    {
        return event_type;
    }
    public void setEvent_type(String event_type){
        this.event_type = event_type;
    }

    public String getAlbum_name(){
        return album_name;
    }
    public void setAlbum_name(String album_name){
        this.album_name = album_name;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getEvent_loc() {
        return event_loc;
    }

    public void setEvent_loc(String event_loc) {
        this.event_loc = event_loc;
    }

    public String getBucket_link() {
        return bucket_link;
    }

    public void setBucket_link(String bucket_link) {
        this.bucket_link = bucket_link;
    }

}
