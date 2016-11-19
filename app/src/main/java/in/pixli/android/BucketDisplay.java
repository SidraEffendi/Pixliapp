package in.pixli.android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.pixli.sidra.android.R;

import in.pixli.android.retrofit.ApiClient;
import in.pixli.android.retrofit.ApiInterface;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sidra on 22-10-2016.
 *
 * This file brings the images of an event from the s3 bucket into gridview.
 */

public class BucketDisplay extends AppCompatActivity {

    //String[] mThumbIds1 = {"images/first.jpg","images/frustration.jpg","images/g.jpg"};
    //,"images/05es.jpg","images/15es","images/2 ee.jpg","images/25es.jpg","images/again??.jpg"
    //String[] mThumbIds1= new String[1000];
    List<String> mThumbIds1 = new ArrayList<String>();
    public static List<String> result = new ArrayList<String>();
    int photoSize =0;

    static CognitoCachingCredentialsProvider credentialsProvider;
    static AmazonS3 s3;
     TransferUtility transferUtility;

    //public static String[] result = new String[2];
    GridView gridview;
    ImageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bucket_display_fab);

        // Setting up credentials of s3 bucket
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                "ap-northeast-1:2c9313f6-ef22-44e7-bdb3-2a41f5b155a3",    /* Identity Pool ID */
                Regions.AP_NORTHEAST_1          /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
        transferUtility = new TransferUtility(s3, getApplicationContext());  //Required for upload and download from s3 bucket

        gridview = (GridView) findViewById(R.id.gridview);
        //adapter = new ImageAdapter(getApplicationContext());

        /*call to the database to check the no.of photos for the event*/
        ApiInterface apiService1 = ApiClient.createService(ApiInterface.class);
        Call<ResponseBody> call1 = apiService1.getCountOfPhotos(MainActivity.EVENT_ID);
        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call1,Response<ResponseBody> response) {
                int statuscode = response.code();

                Log.e("Getting no.of Photos", "Response: "+statuscode);

                if (response.body() != null){

                    MainActivity.PHOTO_COUNT =1;   /* This flag means the event photo folder is not empty */
                    photoEntry();     /* Function call to display the photos */

                    try {
                        System.out.println("CHECK HERE"+response.body().string());
                    }catch (IOException e) {
                        e.printStackTrace();
                    }

                    //gridview.invalidateViews(); //If this activity is removed then this line is not required


                }
                else{
                    Log.e("Error",""+statuscode+ "......"+ "....null body");
                    Toast.makeText(getApplicationContext(), "Add images to your event", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call1,Throwable t) {
                t.printStackTrace();
            }
        });

        /*if(PHOTO_COUNT == 1) {
            setImage(this, gridview, 1);
        }
        else{
            Toast.makeText(getApplicationContext(), "Add images to your event", Toast.LENGTH_LONG).show();
        }*/
        //gridview.setAdapter(new ImageAdapter(this));

        /*the gridview is made responsive to click on a photo*/
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //PHOTO_COUNT = 1;
                //click action
                Intent intent= new Intent(BucketDisplay.this,PhotoActivity.class);
                intent.putExtra("position",position);
                BucketDisplay.this.startActivity(intent);
            }

        });

        /*fab button for uploading photos from gallery (or camera - to be implemented)*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent myIntent = new Intent(BucketDisplay.this,UploadActivity.class);
                startActivityForResult(myIntent,123);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    // [START onActivityResult]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* Result returned from launching UploadActivity.java intent from Gridview.OnClickListener.*/
        if(requestCode == 123  && resultCode == RESULT_OK ) {

            /* If uploading photo to S3 bucket has been success full. */
            System.out.println("INSIDE BUCKET AGAIN: "+ MainActivity.PHOTO_COUNT);

            gridview.invalidateViews();   /* To invalidate view before photo upload. */
            photoEntry();                 /* Function call to display the photos. */
            //setImage(BucketDisplay.this, gridview, 1);

        }
        else{
            System.out.println("SOMETHING MISSING");
        }
    }
    // [END onActivityResult]

    // [Start photoEntry]
    public void photoEntry(){
        /* make an api call to get the image_url(names) of the photos having current Event_ID. */
        ApiInterface apiService = ApiClient.createService(ApiInterface.class);
        Call<CustomViewPhotosResponse> call = apiService.getPhotosOfEvent(MainActivity.EVENT_ID);
        call.enqueue(new Callback<CustomViewPhotosResponse>() {
            @Override
            public void onResponse(Call<CustomViewPhotosResponse> call, Response<CustomViewPhotosResponse> response) {
                int statuscode = response.code();
                List<CustomViewPhotosHolder> customViewPhotosHolders = response.body().getResults();
                Log.e("BucketDisplayGetCall", "Response: "+statuscode);

                if (customViewPhotosHolders != null){

                    /* Get the image urls (name) from the response body and store it in an array 'mThumbIds1'. */
                    MainActivity.PHOTO_COUNT =1;
                    System.out.println("photos exist");
                    List<String> images = new ArrayList<String>();
                    photoSize = customViewPhotosHolders.size();

                    for(int i = 0, count = photoSize; i< count; i++)
                    {
                        images.add(customViewPhotosHolders.get(i).getImage_url());
                        System.out.println("NAME photos " + images.get(i));
                        mThumbIds1.add(MainActivity.FOLDER_NAME+"/"+images.get(i));
                        System.out.println("NAME photos " + mThumbIds1.get(i));
                    }
                    Log.e("BucketDisplaySize:", ""+photoSize);
                    Log.e("BucketDisplaySize:", MainActivity.FOLDER_NAME);

                    /*Function call to display images in the gridview. */
                    setImage(BucketDisplay.this, gridview, 1);
                }
                else{
                    Log.e("Error",""+statuscode+ "......"+ "....null body");
                }
                // Update mThumbsID with the imageurl in the response.
            }

            @Override
            public void onFailure(Call<CustomViewPhotosResponse> call, Throwable t) {
                Log.e("FailBucketDisplGetCall", t.toString());
            }
        });
    }
    // [END photoEntry]

    // [START setImage]
    private void setImage(final Context context,final ViewGroup parent, final int id) {


        new AsyncTask<Void, Void, List<String>>() {

            @Override
            protected List<String> doInBackground(Void... params) {

                try {

                    /* URL is generated for the each image in the S3 bucket*/
                    //for(int i=0;i< imagesName.length;i++) {
                    Log.e("URL: ", "started");
                    for(int i=0;i< photoSize;i++) {

                        //Extending the expiry time for photo remission
                        java.util.Date expiration = new java.util.Date();
                        long msec = expiration.getTime();
                        msec += 1000 * 60 * 60; // 1 hour.
                        expiration.setTime(msec);

                        /* Generating uri for image in S3 bucket. */
                        Log.e("URL:", mThumbIds1.get(i));
                        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                                new GeneratePresignedUrlRequest("pixliapp01", mThumbIds1.get(i));
                        generatePresignedUrlRequest.setMethod(HttpMethod.GET); // Default.
                        generatePresignedUrlRequest.setExpiration(expiration);

                        /* Uri is converted to url. */
                        URL ss = s3.generatePresignedUrl(generatePresignedUrlRequest);
                        System.out.println("THE URL" + ss);
                        result.add(ss.toString());
                        //uri = Uri.parse(ss.toString());
                        System.out.println("jnkjnn" + result.get(i));
                        //result[i]= uri;
                    }
                }catch(Exception e){System.out.println(""+e);}
                return result;
            }

            @Override
            protected void onPostExecute(List<String> result) {
                super.onPostExecute(result);

                for(int j=0;j<result.size();j++) {
                    System.out.println("In Post Execute :" + result.get(j));
                }
                //adapter.notifyDataChanged();
                //gridview.invalidateViews();

                /* ImageAdapter is called to view the images. */
                gridview.setAdapter(new ImageAdapter(context));
            }
        }.execute();
    }
    // [END setImage]

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        // Constructor
        public ImageAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            return result.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

         /*create a new ImageView for each item referenced by the Adapter*/
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            //imageView = new ImageView(mContext);

            if (convertView == null) {
                System.out.println("yoyoyoyooooooo");
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(300, 300)); //size of photos
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(1, 1, 1, 1);
            }
            else
            {
                imageView = (ImageView) convertView;
            }
            //imageView.setImageResource(mThumbIds[position]);
            System.out.println("iiiiiiiiii");

            /* Loading the images to gridview using Picasso library. */
            Picasso.with(mContext)
                    .load(result.get(position))
                    .placeholder(R.drawable.s_9)
                    .error(R.drawable.s_9)
                    //.resize(60, 60)
                    //.centerInside()
                    .into(imageView);
            return imageView;
        }
    }
}
