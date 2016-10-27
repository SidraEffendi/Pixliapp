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
import android.widget.Toast;

import com.example.sidra.pixliapp.retrofit.ApiClient;
import com.example.sidra.pixliapp.retrofit.ApiInterface;

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

public class Events extends AppCompatActivity {

    EditText EventType, AlbumName, EventDate, EventLocation;
    Button Next,Ok;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    String code;
    String temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);

        //
        EventType = (EditText) findViewById(R.id.EventType);
        AlbumName = (EditText) findViewById(R.id.AlbumName);
        EventDate = (EditText) findViewById(R.id.EventDate);
        EventLocation = (EditText) findViewById(R.id.EventLocation);

        //----- When Next button is clicked a unique code is generated, displayed to the user and all the data is saved into postgreSQL db if valid ------//

        Next = (Button) findViewById(R.id.Next);
        Next.setOnClickListener(new View.OnClickListener() {
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
                alertDialogBuilder = new AlertDialog.Builder(Events.this);               //creating a dialog box
                LayoutInflater inflater = Events.this.getLayoutInflater();               // Inflate and set the layout for the dialog
                View v_iew=inflater.inflate(R.layout.code_display, null);                // Pass null as the parent view because its going in the dialog layout
                alertDialogBuilder.setView(v_iew);
                TextView CodeDisplay = (TextView) v_iew.findViewById(R.id.CodeDisplay);  //Displaying the unique ID to user
                CodeDisplay.setText(code);

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();


                Ok = (Button) v_iew.findViewById(R.id.Ok);
                Ok.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                     //-----Post all data to the flask api for entry in postgreSQl db-----//

                        /////    get the entered event details from the xml
                        CustomViewHolder ab = new CustomViewHolder();            //creating object of CustomViewHolder type
                        ab.setCode_id(code);
                        ab.setEvent_type(EventType.getText().toString());
                        ab.setAlbum_name(AlbumName.getText().toString());
                        ab.setEvent_date(EventDate.getText().toString());
                        ab.setEvent_loc(EventLocation.getText().toString());
                        temp = "img"+code;
                        ab.setBucket_link(temp);

                        //creating call to post data to api
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
                                    EVENT_ID=code;
                                    FOLDER_NAME = temp;
                                }

                            }

                            @Override
                            public void onFailure(Call<CustomViewResponse> call1, Throwable t) {

                                Log.e("EVENT Posting Failed", t.toString());
                                Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                            }
                        });

                        Intent myIntent = new Intent(Events.this, BucketDisplay.class);
                        Events.this.finish();
                        startActivity(myIntent);

                    }
                });
            }
        });
    }
}