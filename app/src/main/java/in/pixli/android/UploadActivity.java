package in.pixli.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.pixli.sidra.android.R;

import in.pixli.android.retrofit.ApiClient;
import in.pixli.android.retrofit.ApiInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.pixli.android.BucketDisplay.s3;

/**
 * Created by sidra on 23-10-2016.
 *
 * This Activity starts when fab button is clicked to upload images, currently only from gallery. To select images from galllery the
 * GActivity is started which returns the selected images to this activity (Multiple photos can be selected).
 * Then a random string is generated as the name of the photo and a call is made to the database to check that this name already exist.If so then another string is generated.
 * The images are then loaded to the S3 bucket with their details being stored in the database through a POST call.
 */

public class UploadActivity extends Activity{


    TransferUtility transferUtility;
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

        /* Used for transfer of images from S3 bucket to app. */
        transferUtility = new TransferUtility(s3, getApplicationContext());

        /* GActivity opens the custom gallery to pick photos from */
        Log.i("Intent", "onClick1");
        Intent myIntent1 = new Intent(UploadActivity.this, GActivity.class);
        startActivityForResult(myIntent1, PICK_IMAGE_MULTIPLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        /* Result returned from GActivity.java */
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {

            Log.i("my Main", "updating in gallery");

            /* GActivity returns an array of images to upload into the S3 bucket */
            imagesPathList = new ArrayList<String>();
            String[] imagesPath = data.getStringExtra("data").split("\\|");
            try {
                lnrImages.removeAllViews();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            for (int i = 0; i < imagesPath.length; i++) {
                imagesPathList.add(imagesPath[i]);
                yourbitmap = BitmapFactory.decodeFile(imagesPath[i]);   /* The address of images in phone is decoded. */
                Log.i("Image", "" + imagesPath[i]);

                /////////// CHECK IF THIS IMAGE VIEW IS NEEDED
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(yourbitmap);
                imageView.setAdjustViewBounds(true);
                Log.i("Image", "" + yourbitmap);


                //generating a random Photo name to be stored in image_url column of Photos Database (1 in billion chances of being same)
                UUID uniqueKey = UUID.randomUUID();
                Imgcode_temp = uniqueKey.toString();
                System.out.println(Imgcode_temp);

                /* The S3 bucket name of the image with the folder name is set */
                MainActivity.FOLDER_NAME= "img"+ MainActivity.EVENT_ID;
                String picturePath = imagesPath[i];
                fileToUpload = new File(picturePath);

                /* Uploading the image to bucket. */
                TransferObserver transferObserver = transferUtility.upload("pixliapp01", MainActivity.FOLDER_NAME +"/"+Imgcode_temp+"photo.jpg", fileToUpload);
                transferObserverListener(transferObserver);

                //------- In this section the details of the photos uploaded to S3 bucket, are entered into the database via POST call

                // entering the details of photos in the photos table
                CustomViewPhotosHolder entry = new CustomViewPhotosHolder();       //creating object of CustomViewPhotosHolder type
                entry.setPhoto_code_id(MainActivity.EVENT_ID);
                UploadedPhotos = Imgcode_temp + "photo.jpg";
                entry.setImage_url(UploadedPhotos);


                /* Creating call to post data of Photos to databse through api. */
                ApiInterface apiService = ApiClient.createService(ApiInterface.class);
                Call<CustomViewPhotosResponse> call = apiService.createPhotos(MainActivity.EVENT_ID,entry);
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
            MainActivity.PHOTO_COUNT =1; /* to mark there are photos inside the bucket  (declared in MainActivity). */
            System.out.println("GOING BACK");

            /* returning to BucketDisplay.java after photo upload completion. */
            Intent ii = new Intent(UploadActivity.this,BucketDisplay.class);
            System.out.println("222 GOING BACK");
            //ii.putExtra("data", UploadedPhotos);
            setResult(Activity.RESULT_OK, ii);
            System.out.println("333 GOING BACK");
            finish();             /*  This UploadActivity is removed from the stack as its work has completed. */
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
        finish();                             /*  This UploadActivity is removed from the stack as its work has completed. */
        super.onBackPressed();

    }
}
