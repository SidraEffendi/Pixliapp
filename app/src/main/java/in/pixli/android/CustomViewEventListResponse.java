package in.pixli.android;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sidra on 23-11-2016.
 */

public class CustomViewEventListResponse {

    @SerializedName("Events")
    private List<CustomViewHolder> Events;

    @SerializedName("GuestList")
    private List<CustomViewEventList> GuestList;

    public List<CustomViewEventList> getGuestList() {
        return GuestList;
    }

    public List<CustomViewHolder> getEvents() {
        return Events;
    }

    public void setEvents(List<CustomViewHolder> events) {
        Events = events;
    }

    public void setGuestList(List<CustomViewEventList> guestList) {
        GuestList = guestList;
    }
}
