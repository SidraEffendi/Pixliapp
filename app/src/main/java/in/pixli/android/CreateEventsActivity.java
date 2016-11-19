package in.pixli.android;

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

import com.pixli.sidra.android.R;

import in.pixli.android.retrofit.ApiClient;
import in.pixli.android.retrofit.ApiInterface;

import java.util.Calendar;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.pixli.android.MainActivity.LOGGED_IN;

/**
 * Created by sidra on 20-10-2016.
 *
 * This class sends the event details entered by the user into the postgreSQL database through (Flask) api.
 * Upon entering valid details in the event details section, a random code will be generated (length=5).
 * It will be checked via post call if this id already exists in database, in that case, a new id will be
 * generated. Then all details are posted to the database and if successfully posted then BucketDisplay
 * activity is started and static variable EVENT_ID (declared in MainActivity) is set equal to the generated
 * code and static variable FOLDER_NAME (declared in MainActivity) is set to the Bucket_link value of our
 * event. FOLDER_NAME will serve as the folder is S3 Bucket to will all the photos of this event will be added.
 *
 */

public class CreateEventsActivity extends AppCompatActivity {

    EditText  albumName, eventLocation;
    private Spinner eventType;
    Button ok;
    ImageView next;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    String code;
    String temp;

    private String email_Id;

// variables for calendar and event dropdown
    private DatePicker datePicker;
    private Calendar calendar;
    private TextView  eventDate;
    private int year, month, day;

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
                CreateEventsActivity.this, R.layout.spinner_item, resources.getStringArray(R.array.album_type)
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


        eventType = (Spinner) findViewById(R.id.EventType); // Evet type with drop-down box
        albumName = (EditText) findViewById(R.id.AlbumName);
        eventLocation = (EditText) findViewById(R.id.EventLocation);

        //----- When Next button is clicked event entry is checked for validity and if user is not logged in
        //      then direct to login activity otherwise post event details to database. ------//
        next = (ImageView) findViewById(R.id.Next);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(albumName.getText().toString().equals("") && eventDate.getText().toString().equals("")){
                    //Write a toast message to show error to user
                    System.out.println("Empty fields");
                }
                else {

                    /* Implementing new flow. */
                    /* User is directed to login page since value is set to 0. */
                    System.out.println("No Empty fields");
                     if(LOGGED_IN == 0){

                         /* LoginActivity started and its result is called back in this activity. */
                         Intent myIntent = new Intent(CreateEventsActivity.this, LoginActivity.class);
                         startActivityForResult(myIntent,123);
                     }
                    else{
                         /* function call to post event details to the database */
                         postEventDatabase();
                     }
                }
            }
        });
    }

    // [START onActivityResult]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* Result returned from launching LoginActivity.java intent from Gridview.OnClickListener.*/
        if(requestCode == 123  && resultCode == RESULT_OK && null != data) {

            /* If user login has been success full, create event under their email id. */
             email_Id = getIntent().getStringExtra("data");
            //System.out.println("INSIDE BUCKET AGAIN: "+ MainActivity.PHOTO_COUNT);

            /* function call to post event details to the database */
            postEventDatabase();
        }
        else{
            System.out.println("SOMETHING MISSING");
        }
    }
    // [END onActivityResult]

    // [START postEventDatabase]

    /*unique code is generated, displayed to the user and all the data is saved into postgreSQL db*/
    public void postEventDatabase(){

        //generating a random unique code
        UUID uniqueKey = UUID.randomUUID();
        String code_temp = uniqueKey.toString();
        System.out.println(code_temp);
        String[] a = code_temp.split("-");
                /*for(int i = 0; i < a.length; i++){
                    System.out.println(""+a[i]);
                }*/
        code = a[0];
        System.out.println(code);
        //code = "ASP333";

        // A Dialog box is created which shows the generated unique event id
        alertDialogBuilder = new AlertDialog.Builder(CreateEventsActivity.this);               //creating a dialog box
        LayoutInflater inflater = CreateEventsActivity.this.getLayoutInflater();               // Inflate and set the layout for the dialog
        View v_iew = inflater.inflate(R.layout.code_display, null);                // Pass null as the parent view because its going in the dialog layout
        alertDialogBuilder.setView(v_iew);
        TextView CodeDisplay = (TextView) v_iew.findViewById(R.id.CodeDisplay);  //Displaying the unique ID to user
        CodeDisplay.setText(code);

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();


        ok = (Button) v_iew.findViewById(R.id.Ok);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //-----Post all data to the flask api for entry in postgreSQl db-----//

                /////    get the entered event details from the xml
                CustomViewHolder ab = new CustomViewHolder();            //creating object of CustomViewHolder type
                ab.setCode_id(code);
                ab.setEvent_type(eventType.getSelectedItem().toString());
                ab.setAlbum_name(albumName.getText().toString());
                ab.setEvent_date(eventDate.getText().toString());
                ab.setEvent_loc(eventLocation.getText().toString());
                temp = "img" + code;
                ab.setBucket_link(temp);

                //creating call to post data to api
                ApiInterface apiService = ApiClient.createService(ApiInterface.class);
                Call<CustomViewResponse> call1 = apiService.createEvent(ab, email_Id);
                call1.enqueue(new Callback<CustomViewResponse>() {
                    @Override
                    public void onResponse(Call<CustomViewResponse> call1, Response<CustomViewResponse> respo) {
                        int statuscode = respo.code();

                        Log.d("Message", "code..." + respo.code() + " message..." + respo.message());

                        CustomViewResponse respon = respo.body();

                        if (respon == null) {
                            Log.e("Error", "" + statuscode + "......" + respo.message() + "....null body");
                        } else {
                            Log.e("Success", "" + statuscode + "......" + respo.message() + "vvvvv body exists");
                            MainActivity.EVENT_ID = code;
                            MainActivity.FOLDER_NAME = temp;

                            /* Start the BucketDisplay.java activity and remove this activity from cycle */
                            Intent myIntent = new Intent(CreateEventsActivity.this, Event_List.class);
                            myIntent.putExtra("data", email_Id);
                            CreateEventsActivity.this.finish();    //removes this activity from the stack
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
    // [END postEventDatabase]

    // [START Implementing calendar view]
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

// [END Implementing calendar view]


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