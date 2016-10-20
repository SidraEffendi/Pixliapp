package com.example.sidra.pixliapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by sidra on 20-10-2016.
 */

public class Events extends AppCompatActivity {

    EditText EventName, AlbumName, EventDate, EventLocation;
    Button Next,Ok;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);

        //
        EventName = (EditText) findViewById(R.id.EventName);
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
                String code = "1A24";

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


                Ok = (Button) findViewById(R.id.Ok);
                Ok.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                     //-----Post all data to the flask api for entry in postgreSQl db-----//

                        //get the entered details

                    }
                });
            }
        });
    }
}