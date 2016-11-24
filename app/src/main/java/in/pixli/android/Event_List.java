package in.pixli.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.Toast;

import in.pixli.android.R;

import in.pixli.android.retrofit.ApiClient;
import in.pixli.android.retrofit.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.pixli.android.MainActivity.EVENT_ID;

/**
 * Created by sidra on 02-11-2016.
 *
 * This Activity is responsible for showing the list of events related to the user account (as admin and as guest).
 * When an event code is clicked, EVENT_ID is set to that code and photos for it are displayed in BucketDisplay.java
 */

public class Event_List extends Activity{


       public SimpleCursorAdapter listAdapter;

    List<CustomViewHolder> hostedEvent;
    List<CustomViewEventList> attendedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);

        //Make a get call to get the list of events from database

        Log.e("Inside event list","yoyo"+LoginActivity.EMAIL_ID);
        ApiInterface apiService1 = ApiClient.createService(ApiInterface.class);
        Call<CustomViewEventListResponse> call1 = apiService1.getEventList(LoginActivity.EMAIL_ID);
        //Call<CustomViewEventList> call1 = apiService1.getEventList(getIntent().getStringExtra("data"));
        //Call<CustomViewEventList> call1 = apiService1.getEventList("sidraeffendi@gmail.com");
        call1.enqueue(new Callback<CustomViewEventListResponse>() {
            @Override
            public void onResponse(Call<CustomViewEventListResponse> call1, Response<CustomViewEventListResponse> response) {
                int statuscode = response.code();

                Log.e("Getting event list", "Response: "+statuscode);

                if (response.body() != null){

                    hostedEvent =response.body().getEvents();
                    attendedEvent = response.body().getGuestList();


                    /* To get the no.of events hosted and attended as guest */
                    int sizeHevents = hostedEvent.size();
                    int sizeGevents = attendedEvent.size();

                    Log.e("Hosted_events size  :  ",""+ sizeHevents);
                    Log.e("Attended_events size:  ",""+ sizeGevents);

                    /* Printing the hosted event name */
                    for(int i =0;i<hostedEvent.size();i++){
                        System.out.println("Hosted event: "+hostedEvent.get(i).getCode_id());
                    }

                    /* Printing the attended event name */
                    if(sizeGevents != 0){
                        for(int i =0;i<attendedEvent.size();i++){
                            System.out.println("Guest event: "+attendedEvent.get(i).getAttended_event_id());
                        }
                    }


                    System.out.println(hostedEvent.get(0).getCode_id());
                    //Log.e("Guest_events  :  ", attendedEvent.get(0).getAttended_event_id());

                    MainActivity.PHOTO_COUNT =1;     /* static variable declared in MainActiviyt.java*/

                    /* Set the event id, the photos of whihc will be displayed in Bucket */
                    MainActivity.EVENT_ID= hostedEvent.get(0).getCode_id();
                    System.out.println(MainActivity.EVENT_ID);

                    //Display the list of events
                    displayEventList();
                }
                else{
                    Log.e("Error",""+statuscode+ "......"+ "....null body");
                    Toast.makeText(getApplicationContext(), "No events", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<CustomViewEventListResponse> call1,Throwable t) {
                t.printStackTrace();
            }
        });

        // Set the EVENT_ID= the one user clicked and start the BucketDispay file
        //MainActivity.EVENT_ID = "ASP1";

        MainActivity.FOLDER_NAME ="img"+MainActivity.EVENT_ID;
        Intent myIntent = new Intent(Event_List.this, BucketDisplay.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Event_List.this.startActivity(myIntent);
        //finish();
    }

    public void displayEventList(){

        /*Log.i("Event list", "updating");

        listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.event_item,
                cursor,
                new String[] { TableData.Columns.VENUE, TableData.Columns.CODE},
                new int[] { R.id.admin_code_id,R.id.guest_code_id},
                0
        );
        this.setListAdapter(listAdapter);*/
    }

}
