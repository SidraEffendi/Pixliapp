package com.example.sidra.pixliapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import java.io.File;

import static com.example.sidra.pixliapp.BucketDisplay.s3;

/**
 * Created by sidra on 22-10-2016.
 *
 * This class is responsible for enlarging the photo when clicked in the gridview of BucketDisplay class
 */

public class PhotoActivity extends Activity {

    Button Download;
    ImageView imageView;

    TransferUtility transferUtility;
    File fileToDownload, f;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

         imageView = (ImageView) findViewById(R.id.imageView);

        int position = getIntent().getIntExtra("position",-1);
        System.out.println("THE RESULT: "+BucketDisplay.result[position].toString());
        if(position != -1){
            Picasso.with(PhotoActivity.this)
                    .load(BucketDisplay.result[position])
                    .into(imageView);
        }
        else{
            Picasso.with(PhotoActivity.this)
                    .load(R.drawable.s_9)
                    .into(imageView);
        }

        Download = (Button) findViewById(R.id.Download);
        Download.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // --- open the events screen --

                /*Intent myIntent = new Intent(MainActivity.this, Events.class);
                MainActivity.this.startActivity(myIntent);*/
                fileToDownload = new File(Environment.getExternalStorageDirectory().getPath() + "/second.jpg");

                transferUtility = new TransferUtility(s3, getApplicationContext());
                // ATTENTION: This was auto-generated to implement the App Indexing API.
                // See https://g.co/AppIndexing/AndroidStudio for more information.
                //client = new GoogleApiClient.Builder(PhotoActivity.this).addApi(AppIndex.API).build();

                //setFileToDownload();

            }
        });
    }

    public void setFileToDownload() {
        TransferObserver transferObserver = transferUtility.download("pixliapp01", "images/second.jpg", fileToDownload);
        transferObserverListener(transferObserver);
    }


    public void transferObserverListener(TransferObserver transferObserver) {

        transferObserver.setTransferListener(new TransferListener() {
            public void onStateChanged(int id, TransferState state) {
            }

            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            public void onError(int id, Exception ex) {
            }
        });
    }

}
