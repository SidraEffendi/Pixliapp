package in.pixli.android;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sidra on 10-11-2016.
 *
 * This file holds the json result of the list of hosted and guest events of a user.
 */

public class CustomViewEventList {

    /*@SerializedName("hosted_events")
    private String hosted_events;

    @SerializedName("guest_code_id")
    private List<String> guest_code_id;

    public String getHosted_events() {
        return hosted_events;
    }

    public void setHosted_events(String hosted_events) {
        this.hosted_events = hosted_events;
    }

    public List<String> getGuest_code_id() {
        return guest_code_id;
    }

    public void setGuest_code_id(List<String> guest_code_id) {
        this.guest_code_id = guest_code_id;
    }*/

    @SerializedName("id")
    private Integer id;

    @SerializedName("attended_event_id")
    private String attended_event_id;

    @SerializedName("guest_email_id")
    private String guest_email_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAttended_event_id() {
        return attended_event_id;
    }

    public void setAttended_event_id(String attended_event_id) {
        this.attended_event_id = attended_event_id;
    }

    public String getGuest_email_id() {
        return guest_email_id;
    }

    public void setGuest_email_id(String guest_email_id) {
        this.guest_email_id = guest_email_id;
    }
}
