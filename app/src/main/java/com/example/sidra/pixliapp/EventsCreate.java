package com.example.sidra.pixliapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import com.example.sidra.pixliapp.retrofit.ApiClient;
import com.example.sidra.pixliapp.retrofit.ApiInterface;

import java.util.Calendar;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.sidra.pixliapp.MainActivity.EVENT_ID;
import static com.example.sidra.pixliapp.MainActivity.FOLDER_NAME;

/**
 * Created by sidra on 20-10-2016.
 *
 * This class sends the event details entered by the user into the postgreSQL database through (Flask) api.
 * Upon entering
 * valid details in the event details section, a random code will be generated (length=5). It will be checked via post call
 * if this id already exists in database, in that case, a new id will be generated. Then all details are posted to the database
 * and if successfully posted then BucketDisplay activity is started and static variable EVENT_ID (declared in MainActivity)
 * is set equal to the generated code and static variable FOLDER_NAME (declared in MainActivity) is set to the Bucket_link
 * value of our event. FOLDER_NAME will serve as the folder is S3 Bucket to will all the photos of this event will be added.
 *
 */

public class EventsCreate extends AppCompatActivity {

    EditText  albumName, eventLocation;
    Button ook;
    ImageView next;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    String code;
    String temp;


// variables for calendar and event dropdown
    private DatePicker datePicker;
    private Calendar calendar;
    private TextView  eventDate;
    private int year, month, day;
    private Spinner eventType;

    Calendar myCalendar = Calendar.getInstance();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);
        addListenerOnSpinnerItemSelection();


        Resources resources = getResources();

        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                EventsCreate.this, R.layout.spinner_item, resources.getStringArray(R.array.album_type)
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        eventType.setAdapter(spinnerArrayAdapter);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();



        eventDate = (TextView) findViewById(R.id.EventDate);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);




        //code to hide auto input box popup

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        //
        eventType = (Spinner) findViewById(R.id.EventType);
        albumName = (EditText) findViewById(R.id.AlbumName);
       // EventDate = (EditText) findViewById(R.id.EventDate);
        eventLocation = (EditText) findViewById(R.id.EventLocation);

        //----- When Next button is clicked a unique code is generated, displayed to the user and all the data is saved into postgreSQL db if valid ------//

        next = (ImageView) findViewById(R.id.Next);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //generating a random unique code
                UUID uniqueKey = UUID.randomUUID();
                String code_temp = uniqueKey.toString();
                System.out.println(code_temp);
                String[] a =code_temp.split("-");
                /*for(int i = 0; i < a.length; i++){
                    System.out.println(""+a[i]);
                }*/
                code = a[0];
                System.out.println(code);
                //code = "ASP333";

                // A Dialog box is created which shows the generated unique event id
                alertDialogBuilder = new AlertDialog.Builder(EventsCreate.this);               //creating a dialog box
                LayoutInflater inflater = EventsCreate.this.getLayoutInflater();               // Inflate and set the layout for the dialog
                View v_iew=inflater.inflate(R.layout.code_display, null);                // Pass null as the parent view because its going in the dialog layout
                alertDialogBuilder.setView(v_iew);
                TextView CodeDisplay = (TextView) v_iew.findViewById(R.id.CodeDisplay);  //Displaying the unique ID to user
                CodeDisplay.setText(code);

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();


                ook = (Button) v_iew.findViewById(R.id.Ok);
                ook.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                     //-----Post all data to the flask api for entry in postgreSQl db-----//

                        /////    get the entered event details from the xml
                        CustomViewHolder ab = new CustomViewHolder();            //creating object of CustomViewHolder type
                        ab.setCode_id(code);
                        ab.setEvent_type(eventType.getSelectedItem().toString());
                        ab.setAlbum_name(albumName.getText().toString());
                        ab.setEvent_date(eventDate.getText().toString());
                        ab.setEvent_loc(eventLocation.getText().toString());
                        temp = "img"+code;
                        ab.setBucket_link(temp);
                        String email_Id = getIntent().getStringExtra("email_Id");

                        //creating call to post data to api
                        ApiInterface apiService = ApiClient.createService(ApiInterface.class);
                        Call<CustomViewResponse> call1 = apiService.createEvent(ab,email_Id);
                        call1.enqueue(new Callback<CustomViewResponse>() {
                            @Override
                            public void onResponse(Call<CustomViewResponse> call1, Response<CustomViewResponse> respo) {
                                int statuscode = respo.code();

                                Log.d("Message", "code..."+respo.code() + " message..." + respo.message());

                                CustomViewResponse respon = respo.body();

                                if (respon == null){
                                    Log.e("Error",""+statuscode+ "......"+ respo.message()+"....null body");
                                }
                                else{
                                    Log.e("Success",""+statuscode+ "......"+ respo.message()+"vvvvv body exists");
                                    EVENT_ID=code;
                                    FOLDER_NAME = temp;
                                    Intent myIntent = new Intent(EventsCreate.this, Event_List.class);
                                    EventsCreate.this.finish();
                                    startActivity(myIntent);
                                }

                            }

                            @Override
                            public void onFailure(Call<CustomViewResponse> call1, Throwable t) {

                                Log.e("EVENT Posting Failed", t.toString());
                                Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
            }
        });
    }


    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        eventDate.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
    }




    public void addListenerOnSpinnerItemSelection() {
       eventType  = (Spinner) findViewById(R.id.EventType);
        eventType.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("CreateEvent Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}