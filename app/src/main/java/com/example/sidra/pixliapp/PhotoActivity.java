package com.example.sidra.pixliapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.android.gms.appindexing.Action;
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

    //static CognitoCachingCredentialsProvider credentialsProvider;
    //static AmazonS3 s3;
    TransferUtility transferUtility;

    private static int PHOTO_SELECTED = 0;
    private static int PICK_IMAGE_MULTIPLE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    File fileToDownload, f;

    private GoogleApiClient client;

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

         imageView = (ImageView) findViewById(R.id.imageView);

        //---- This section is responsible for enlarging the image which has been clicked
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


    }

    public void setFileToDownload(View view) {

        fileToDownload = new File(Environment.getExternalStorageDirectory().getPath() + "/Pixli/hello.jpg");
        //f = new File("/storage/emulated/0/11.jpg");

        transferUtility = new TransferUtility(s3, getApplicationContext());


        TransferObserver transferObserver = transferUtility.download("pixliapp01", "images/second.jpg", fileToDownload);
        transferObserverListener(transferObserver);
        System.out.println("DOWNLOAD trying");
    }


    public void transferObserverListener(TransferObserver transferObserver) {

        transferObserver.setTransferListener(new TransferListener() {
            public void onStateChanged(int id, TransferState state) {
                System.out.println("State changed");
            }

            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                System.out.println("Progress changed");
            }

            public void onError(int id, Exception ex) {
                System.out.println("DOWNLOAD ERROR" +ex);
            }
        });
    }

}
