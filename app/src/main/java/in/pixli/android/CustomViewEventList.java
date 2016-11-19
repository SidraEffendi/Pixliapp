package in.pixli.android;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sidra on 10-11-2016.
 *
 * This file holds the json result of the list of hosted and guest events of a user.
 */

public class CustomViewEventList {

    @SerializedName("hosted_events")
    private List<String> hosted_events;

    @SerializedName("guest_code_id")
    private List<String> guest_code_id;

    public List<String> getHosted_events() {
        return hosted_events;
    }

    public void setHosted_events(List<String> hosted_events) {
        this.hosted_events = hosted_events;
    }

    public List<String> getGuest_code_id() {
        return guest_code_id;
    }

    public void setGuest_code_id(List<String> guest_code_id) {
        this.guest_code_id = guest_code_id;
    }
}
