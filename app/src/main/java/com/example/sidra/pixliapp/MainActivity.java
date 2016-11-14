package com.example.sidra.pixliapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sidra.pixliapp.retrofit.ApiClient;
import com.example.sidra.pixliapp.retrofit.ApiInterface;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/*
 *The main activity reacts on clicking the buttons guest login and create event. On Clicking create event  button
 * the Event_List.java activity will open up if the user is logged in else LoginActivity.java will open.
 * On clicking the guest login button, user will be asked for unique event code and code entered will be
 * stored in the static variable EVENT_ID and BucketDisplay.java activity will be started.
 */

public class MainActivity extends AppCompatActivity {

    static  int PHOTO_COUNT =0;             // No.of Photos for an event
    static String FOLDER_NAME = "";         // Folder name for the current event in app
    static String EVENT_ID = "";            // Event unique code for the current event in app

    Button CreateEvent,Guest;
    AlertDialog.Builder alertDialogBuilder1;
    AlertDialog alertDialog1;

    EditText Event_idd;

    public static SharedPreferences app_preferences;
    public static int LOGGED_IN;
    public static int CLICKED_CREVENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.sidra.fb_login", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                //System.out.println("YourKeyHash: "+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/

        // Get the app's shared preferences to keep track of user's activity in the app
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        LOGGED_IN = app_preferences.getInt("LOGGED_IN",0);                /*To check is user has already logged in (from MainActivity).*/
        CLICKED_CREVENT = app_preferences.getInt("CLICKED_CREVENT",0);    /*To track if user clicked Create event button or 'already member'
                                                                             link and take action accordingly (from MainActivity).*/
        System.out.println("logged in :" + LOGGED_IN);

        //----- When create event button is clicked user is directed to EventsCreate java class -----//
        CreateEvent = (Button) findViewById(R.id.Create);
        //Toast.makeText(getApplicationContext(), "Create clicked", Toast.LENGTH_SHORT).show();
        CreateEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // --- It is checked if the user is logged in --

            if(TRUE) {     //This value is supposed to be 0. Keep = 1 during debugging login section. LOGGED_IN == 0

                /* Mark that create event button has been clicked to later direct the user to filling event details */
                CLICKED_CREVENT = app_preferences.getInt("CLICKED_CREVENT",1); //set this value again to 0 when already member is clicked
                System.out.println("logged in :" + CLICKED_CREVENT);

                /* User is directed to login page since value is set to 0. */
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
                else{
                //User is shown a list of his/her events
                System.out.println("You are already logged in");
                /*Intent myIntent = new Intent(MainActivity.this, EventList.class);
                MainActivity.this.startActivity(myIntent);*/
            }

            }
        });

        ///--- When guest login button is clicked user is prompted to enter the event code ---//
        Guest = (Button) findViewById(R.id.Guest);
        //Toast.makeText(getApplicationContext(), "Create clicked", Toast.LENGTH_SHORT).show();
        Guest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // --- open the dialog box --

                alertDialogBuilder1 = new AlertDialog.Builder(MainActivity.this);

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View v_iew=inflater.inflate(R.layout.dialog_guest, null);       // Pass null as the parent view because its
                                                                                      // going in the dialog layout
                alertDialogBuilder1.setView(v_iew);
                // Add action buttons
                alertDialogBuilder1.setPositiveButton("Enter",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                /* Get the event id entered in the dialog box. */
                                Event_idd = (EditText) v_iew.findViewById(R.id.Event_idd);  /*Find ViewById is called on v_iew because
                                                                                            it is inside the dialog box */
                                System.out.println("Pressed code enter");
                                EVENT_ID= Event_idd.getText().toString();

                                /*api call is made to check if the entered event code exists. */
                                ApiInterface apiService1 = ApiClient.createService(ApiInterface.class);
                                Call<ResponseBody> call1 = apiService1.getEventsExits(EVENT_ID);
                                call1.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call1, Response<ResponseBody> response) {
                                        int statuscode = response.code();

                                        Log.e("Check Event existence", "Response: "+statuscode);
                                        if (response.body() != null){

                                            /* the BucketDisplay.java file is opened to display photos of event. */
                                            FOLDER_NAME = "img"+EVENT_ID;
                                            Intent myIntent = new Intent(MainActivity.this, BucketDisplay.class);
                                            MainActivity.this.startActivity(myIntent);
                                            try {
                                                System.out.println(response.body().string());
                                            }catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        else{
                                            Log.e("Error",""+statuscode+ "......"+ "....null body");
                                            Toast.makeText(getApplicationContext(), "Event id does not exist", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<ResponseBody> call1,Throwable t) {
                                        t.printStackTrace();
                                    }
                                });

                            }
                        });
                alertDialogBuilder1.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //box2();
                            }
                        });

                alertDialog1 = alertDialogBuilder1.create();
                alertDialog1.show();

            }
        });
    }
}
