package in.pixli.android;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sidra on 25-10-2016.
 *
 * This file holds the json format for the Photos table of database.
 */

public class CustomViewPhotosHolder {
    @SerializedName("id")
    private Integer id;

    @SerializedName("image_url")
    private String image_url;

    @SerializedName("like_count")
    private Integer like_count;

    @SerializedName("photo_code_id")
    private String photo_code_id;

    @SerializedName("share_count")
    private Integer share_count;

    public CustomViewPhotosHolder(){}

    public CustomViewPhotosHolder(String image_url, Integer like_count, String photo_code_id, Integer share_count){
        this.image_url = image_url;
        this.like_count = like_count;
        this.photo_code_id = photo_code_id;
        this.share_count = share_count;
    }

    public String getImage_url(){
        return image_url;
    }
    public void setImage_url(String image_url){
        this.image_url = image_url;
    }

    public Integer getLike_count()
    {
        return like_count;
    }
    public void setLike_count(Integer like_count){
        this.like_count = like_count;
    }

    public String getPhoto_code_id(){
        return photo_code_id;
    }
    public void setPhoto_code_id(String photo_code_id){
        this.photo_code_id = photo_code_id;
    }

    public Integer getShare_count() {
        return share_count;
    }

    public void setShare_count(Integer share_count) {
        this.share_count = share_count;
    }

}
