package com.example.sidra.pixliapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sidra.pixliapp.retrofit.ApiClient;
import com.example.sidra.pixliapp.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sidra on 20-10-2016.
 *
 * This class sends the event details entered by the user into the postgreSQL database through (Flask) api.
 */

public class Events extends AppCompatActivity {

    EditText EventType, AlbumName, EventDate, EventLocation;
    Button Next,Ok;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);

        //
        EventType = (EditText) findViewById(R.id.EventType);
        AlbumName = (EditText) findViewById(R.id.AlbumName);
        EventDate = (EditText) findViewById(R.id.EventDate);
        EventLocation = (EditText) findViewById(R.id.EventLocation);

        ///////// When Next button is clicked a unique code is generated and all the data is saved into postgreSQL db //////

        Next = (Button) findViewById(R.id.Next);
        //Toast.makeText(getApplicationContext(), "Create clicked", Toast.LENGTH_SHORT).show();

        Next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // --- open the dialog box showing unique code --

                //generating a random unique code
                code = "ASP1";

                //creating a dialog box
                alertDialogBuilder = new AlertDialog.Builder(Events.this);

                // Inflate and set the layout for the dialog
                LayoutInflater inflater = Events.this.getLayoutInflater();

                // Pass null as the parent view because its going in the dialog layout
                View v_iew=inflater.inflate(R.layout.code_display, null);
                alertDialogBuilder.setView(v_iew);
                //alertDialogBuilder.setMessage(code);
                TextView CodeDisplay = (TextView) v_iew.findViewById(R.id.CodeDisplay);
                CodeDisplay.setText(code);

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();


                Ok = (Button) v_iew.findViewById(R.id.Ok);
                Ok.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                     //-----Post all data to the flask api for entry in postgreSQl db-----//

                        //get the entered details
                        //creating object of CustomViewHolder type
                        //CustomViewHolder cc = new CustomViewHolder("zz2C","Travel","Travelogue","2016-12-12","Gulmarg","http://zcjsckjdcjk233=&");
                        CustomViewHolder ab = new CustomViewHolder();
                        ab.setCode_id(code);
                        ab.setEvent_type(EventType.getText().toString());
                        ab.setAlbum_name(AlbumName.getText().toString());
                        ab.setEvent_date(EventDate.getText().toString());
                        ab.setEvent_loc(EventLocation.getText().toString());
                        ab.setBucket_link("img"+code);

                        //creating call to post data
                        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        Call<CustomViewResponse> call1 = apiService.createTask(ab);
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
                                }

                            }

                            @Override
                            public void onFailure(Call<CustomViewResponse> call1, Throwable t) {

                                Log.e("EVENT Posting", t.toString());
                            }
                        });

                        Intent myIntent = new Intent(Events.this, BucketDisplay.class);
                        Events.this.startActivity(myIntent);
                    }
                });
            }
        });
    }
}