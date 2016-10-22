package com.example.sidra.pixliapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/*
 *The main activity reacts on clicking the buttons guest login and create event.
 */

public class MainActivity extends AppCompatActivity {

    Button CreateEvent,Guest;
    AlertDialog.Builder alertDialogBuilder1;
    AlertDialog alertDialog1;

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
                alertDialogBuilder1.setView(inflater.inflate(R.layout.dialog_guest, null));
                // Add action buttons
                alertDialogBuilder1.setPositiveButton("Enter",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                /*Intent myIntent = new Intent(MainActivity.this, Guest.class);
                                   MainActivity.this.startActivity(myIntent);*/
                                System.out.println("Pressed code enter");
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
