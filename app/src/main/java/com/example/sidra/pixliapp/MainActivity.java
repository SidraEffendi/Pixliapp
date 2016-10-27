package com.example.sidra.pixliapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 *The main activity reacts on clicking the buttons guest login and create event. On Clicking create event  button
 * the Events activity will open up and on clicking the guest login button, a dialog box and for unique event id will
 * open up. The id entered will be stored in the static variable EVENT_ID and BucketDisplay activity will be started.
 */

public class MainActivity extends AppCompatActivity {

    static  int PHOTO_COUNT =0;
    static String FOLDER_NAME = "";
    static String EVENT_ID = "";

    Button CreateEvent,Guest;
    AlertDialog.Builder alertDialogBuilder1;
    AlertDialog alertDialog1;

    EditText Event_idd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ///////// When create event button is clicked user is directed to Events java class //////

        CreateEvent = (Button) findViewById(R.id.Create);
        //Toast.makeText(getApplicationContext(), "Create clicked", Toast.LENGTH_SHORT).show();

        CreateEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // --- open the events screen --

                Intent myIntent = new Intent(MainActivity.this, Events.class);
                MainActivity.this.startActivity(myIntent);

            }
        });

        ///////// When guest login button is clicked user is prompted to enter the event code //////

        Guest = (Button) findViewById(R.id.Guest);
        //Toast.makeText(getApplicationContext(), "Create clicked", Toast.LENGTH_SHORT).show();

        Guest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // --- open the dialog box asking for code scan screen screen --


                alertDialogBuilder1 = new AlertDialog.Builder(MainActivity.this);
                //alertDialogBuilder1.setMessage(R.string.decision1);

                LayoutInflater inflater = MainActivity.this.getLayoutInflater();

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                final View v_iew=inflater.inflate(R.layout.dialog_guest, null);                // Pass null as the parent view because its going in the dialog layout
                alertDialogBuilder1.setView(v_iew);
                //alertDialogBuilder1.setView(inflater.inflate(R.layout.dialog_guest, null));
                // Add action buttons
                alertDialogBuilder1.setPositiveButton("Enter",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                // Get the event id entered in the dialog box. Find ViewById id to called on v_iew because
                                //it is inside the dialog box
                                Event_idd = (EditText) v_iew.findViewById(R.id.Event_idd);
                                System.out.println("Pressed code enter");
                                EVENT_ID= Event_idd.getText().toString();

                                ApiInterface apiService1 = ApiClient.getClient().create(ApiInterface.class);
                                Call<ResponseBody> call1 = apiService1.getEventsExits(EVENT_ID);
                                call1.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call1, Response<ResponseBody> response) {
                                        int statuscode = response.code();

                                        Log.e("Check Event existence", "Response: "+statuscode);

                                        if (response.body() != null){
                                            // Get the image urls from the response body and store it in an array mThumbIds
                                            //PHOTO_COUNT =1;
                                            try {
                                                System.out.println(response.body().string());
                                            }catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            Intent myIntent = new Intent(MainActivity.this, BucketDisplay.class);
                                            MainActivity.this.startActivity(myIntent);

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
