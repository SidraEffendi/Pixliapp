package com.example.sidra.pixliapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.example.sidra.pixliapp.retrofit.ApiClient;
import com.example.sidra.pixliapp.retrofit.ApiInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.sidra.pixliapp.BucketDisplay.s3;
import static com.example.sidra.pixliapp.MainActivity.EVENT_ID;
import static com.example.sidra.pixliapp.MainActivity.FOLDER_NAME;
import static com.example.sidra.pixliapp.MainActivity.PHOTO_COUNT;
import static com.example.sidra.pixliapp.R.id.AlbumName;
import static com.example.sidra.pixliapp.R.id.EventType;

/**
 * Created by sidra on 23-10-2016.
 *
 * This Activity starts when fab button is clicked to upload images, currently only from gallery. To select images from galllery the
 * GActivity is started which returns the selected images to this activity.
 * Then a random string is generated as the name of the photo and a call is made to the database to check that this name already exist.If so then another string is generated.
 * The images are then loaded to the S3 bucket with their details being stored in the database through a POST call.
 */

public class UploadActivity extends Activity{


    TransferUtility transferUtility;
    private static int PHOTO_SELECTED = 0;
    private static int PICK_IMAGE_MULTIPLE = 1;

    File fileToUpload;
    String Imgcode_temp;
    String UploadedPhotos = "";

    private ArrayList<String> imagesPathList;
    private LinearLayout lnrImages;
    private Bitmap yourbitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("my Main", "updating");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        transferUtility = new TransferUtility(s3, getApplicationContext());

        ////
        Log.i("Intent", "onClick1");
        Intent myIntent1 = new Intent(UploadActivity.this, GActivity.class);
        startActivityForResult(myIntent1, PICK_IMAGE_MULTIPLE);
        //setFileToUpload()
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {

            Log.i("my Main", "updating in gallery");

            imagesPathList = new ArrayList<String>();
            String[] imagesPath = data.getStringExtra("data").split("\\|");
            try {
                lnrImages.removeAllViews();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            for (int i = 0; i < imagesPath.length; i++) {
                imagesPathList.add(imagesPath[i]);
                yourbitmap = BitmapFactory.decodeFile(imagesPath[i]);
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(yourbitmap);
                imageView.setAdjustViewBounds(true);
                Log.i("Image", "" + yourbitmap);
                Log.i("Image", "" + imagesPath[i]);

                //generating a random Photo name to be stored in image_url column of Photos Database (i in billion chances of being same)
                UUID uniqueKey = UUID.randomUUID();
                Imgcode_temp = uniqueKey.toString();
                System.out.println(Imgcode_temp);

                FOLDER_NAME= "img"+EVENT_ID;
                String picturePath = imagesPath[i];
                fileToUpload = new File(picturePath);
                TransferObserver transferObserver = transferUtility.upload("pixliapp01", FOLDER_NAME +"/"+Imgcode_temp+"photo.jpg", fileToUpload);
                transferObserverListener(transferObserver);

                UploadedPhotos = Imgcode_temp + "photo.jpg";

                //------- In this section the details of the photos uploaded to S3 bucket, are entered into the database via POST call

                // entering the details of photos in the photos table
                CustomViewPhotosHolder entry = new CustomViewPhotosHolder();       //creating object of CustomViewPhotosHolder type
                entry.setPhoto_code_id(EVENT_ID);
                entry.setImage_url(UploadedPhotos);


                //creating call to post data to api
                ApiInterface apiService = ApiClient.createService(ApiInterface.class);
                Call<CustomViewPhotosResponse> call = apiService.createPhotos(EVENT_ID,entry);
                call.enqueue(new Callback<CustomViewPhotosResponse>() {
                    @Override
                    public void onResponse(Call<CustomViewPhotosResponse> call, Response<CustomViewPhotosResponse> respo) {
                        int statuscode = respo.code();

                        Log.d("Message", "code..."+respo.code() + " message..." + respo.message());

                        CustomViewPhotosResponse respon = respo.body();

                        if (respon == null){
                            Log.e("Error",""+statuscode+ "......"+ respo.message()+"....null body");
                        }
                        else{
                            Log.e("Success",""+statuscode+ "......"+ respo.message()+"vvvvv body exists");
                        }

                    }

                    @Override
                    public void onFailure(Call<CustomViewPhotosResponse> call, Throwable t) {

                        Log.e("Photo Posting Failed", t.toString());
                        Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                    }
                });
            }
            PHOTO_COUNT =1;
            System.out.println("GOING BACK");
            Intent ii = new Intent(UploadActivity.this,BucketDisplay.class);
            System.out.println("222 GOING BACK");
            //ii.putExtra("data", UploadedPhotos);
            setResult(Activity.RESULT_OK, ii);
            System.out.println("333 GOING BACK");
            finish();
        }
    }

    public void transferObserverListener(TransferObserver transferObserver) {

        transferObserver.setTransferListener(new TransferListener() {
            public void onStateChanged(int id, TransferState state) {
                System.out.println("INSIDE STATE CHANGED");

            }

            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                System.out.println("PROGRESS CHANGED");
            }

            public void onError(int id, Exception ex) {

                System.out.println("ON ERROR" + ex);
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();

    }
}
