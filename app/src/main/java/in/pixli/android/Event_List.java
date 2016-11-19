package in.pixli.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.Toast;

import com.pixli.sidra.android.R;

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

    List<String> hosted_events;
    List<String> guest_code_ids;

    public SimpleCursorAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);

        //Make a get call to get the list of events from database

        ApiInterface apiService1 = ApiClient.createService(ApiInterface.class);
        Call<CustomViewEventList> call1 = apiService1.getEventList(getIntent().getStringExtra("data"));
        //Call<CustomViewEventList> call1 = apiService1.getEventList("sidraeffendi@gmail.com");
        call1.enqueue(new Callback<CustomViewEventList>() {
            @Override
            public void onResponse(Call<CustomViewEventList> call1, Response<CustomViewEventList> response) {
                int statuscode = response.code();

                Log.e("Getting no.of Photos", "Response: "+statuscode);

                if (response.body() != null){

                    hosted_events = response.body().getHosted_events();
                    guest_code_ids = response.body().getGuest_code_id();

                    Log.e("Hosted_events  :  ", hosted_events.get(1));
                    Log.e("Guest_events  :  ", guest_code_ids.get(1));

                    MainActivity.PHOTO_COUNT =1;     /* static variable declared in MainActiviyt.java*/

                    //Display the list of events
                    displayEventList();
                }
                else{
                    Log.e("Error",""+statuscode+ "......"+ "....null body");
                    Toast.makeText(getApplicationContext(), "No events", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<CustomViewEventList> call1,Throwable t) {
                t.printStackTrace();
            }
        });

        // Set the EVENT_ID= the one user clicked and start the BucketDispay file
        //EVENT_ID = "ASP1";
        EVENT_ID= hosted_events.get(1);
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
