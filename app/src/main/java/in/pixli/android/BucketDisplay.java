package in.pixli.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import in.pixli.android.R;

import in.pixli.android.retrofit.ApiClient;
import in.pixli.android.retrofit.ApiInterface;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.renderscript.RenderScript.ContextType.PROFILE;

/**
 * Created by sidra on 22-10-2016.
 *
 * This file brings the images of an event from the s3 bucket into gridview.
 */

public class BucketDisplay extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {


    private RecyclerView mRecyclerView;
    private ViewHolderRecycler mAdapter;


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
   // GridView gridview;
    //ImageAdapter adapter;


  //  ActionBarDrawerToggle mDrawerToggle;

//    Button guestLogin;

    private static int BUCKET_TRIG_CREVENT = 1;
    private static int PHOTOS_SIZE =0;
    AlertDialog.Builder alertDialogBuilder1;
    AlertDialog alertDialog1;
    EditText event_idd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bucket_main_nav);

        //Recycler view code starts here
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_image_grid);


        //Recycler view code ends here
        //navbar code starts here
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//navbar code ends here



//     Navigation Drawer code starts here

        // Setting up credentials of s3 bucket
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                "ap-northeast-1:2c9313f6-ef22-44e7-bdb3-2a41f5b155a3",    /* Identity Pool ID */
                Regions.AP_NORTHEAST_1          /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
        transferUtility = new TransferUtility(s3, getApplicationContext());  //Required for upload and download from s3 bucket

       // gridview = (GridView) findViewById(R.id.gridview);
        //adapter = new ImageAdapter(getApplicationContext());

        WhatToDisplay();     /* Function call to display the photos */



        /*the gridview is made responsive to click on a photo*/
      /*  gridview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //PHOTO_COUNT = 1;
                //click action
                Intent intent= new Intent(BucketDisplay.this,PhotoActivity.class);
                intent.putExtra("position",position);
                BucketDisplay.this.startActivity(intent);
            }

        });*/

        /*fab button for uploading photos from gallery (or camera - to be implemented)*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                /* Allow photo upload only if user is logged in */
                if(MainActivity.LOGGED_IN == 0){
                    Toast.makeText(getApplicationContext(), "Login to Upload photos", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent myIntent = new Intent(BucketDisplay.this, UploadActivity.class);
                    startActivityForResult(myIntent, 123);
                }
            }
        });




        //navigation drawer code here
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

            //gridview.invalidateViews();   /* To invalidate view before photo upload. */
            FragmentAndRecycleShow();     /* Function call to display the photos and event lists. */
            //setImage(BucketDisplay.this, gridview, 1);

        }
        else if(requestCode == BUCKET_TRIG_CREVENT  && resultCode == RESULT_OK){
             /* Refresh the bucket view */
            /* Display the fragment and empty event on grid view */
            //Toast.makeText(getApplicationContext(), "Add images to your event", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext()," "+ MainActivity.EVENT_ID, Toast.LENGTH_LONG).show();
            MainActivity.PHOTO_COUNT=0;
            WhatToDisplay();
        }
        else{
            System.out.println("SOMETHING MISSING");
        }
    }
    // [END onActivityResult]

    public  void WhatToDisplay(){

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

                    if(MainActivity.LOGGED_IN == 0){
                        /* Do not show Fragment call photoentry directly */
                        //photoEntry();
                    }
                    else{
                        /* Show Fragment Activity and through it call photoEntry */
                        FragmentAndRecycleShow();
                    }
                    try {
                        System.out.println("CHECK HERE"+response.body().string());
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
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


    }
     public  void FragmentAndRecycleShow(){
         /* . The code to View fragment goes here and then call photoEntry to initiate gridView */
         Toast.makeText(getApplicationContext(), "Will show fragment", Toast.LENGTH_LONG).show();
         //photoEntry();
     }

    // [Start photoEntry]
    public void photoEntry(){
        Toast.makeText(getApplicationContext()," "+ MainActivity.EVENT_ID, Toast.LENGTH_LONG).show();

        /* make an api call to get the image_url(names) of the photos having current Event_ID. */
        ApiInterface apiService = ApiClient.createService(ApiInterface.class);
        Call<CustomViewPhotosResponse> call = apiService.getPhotosOfEvent(MainActivity.EVENT_ID);
        call.enqueue(new Callback<CustomViewPhotosResponse>() {
            @Override
            public void onResponse(Call<CustomViewPhotosResponse> call, Response<CustomViewPhotosResponse> response) {
                int statuscode = response.code();
                //List<CustomViewPhotosHolder> customViewPhotosHolders = response.body().getResults();
                Log.e("BucketDisplayGetCall", "Response: "+statuscode);

                //if (customViewPhotosHolders != null){
                if (response.body() != null){
                    List<CustomViewPhotosHolder> customViewPhotosHolders = response.body().getResults();
                    /* Get the image urls (name) from the response body and store it in an array 'mThumbIds1'. */
                    MainActivity.PHOTO_COUNT =1;
                    System.out.println("photos exist");
                    //List<String> images = new ArrayList<String>();
                    List<Integer> idListOfImages = new ArrayList<Integer>();

                    photoSize = customViewPhotosHolders.size();

                    for(int i = 0, count = photoSize; i< count; i++)
                    {
                        idListOfImages.add(customViewPhotosHolders.get(i).getId());
                        /*images.add(customViewPhotosHolders.get(i).getImage_url());
                        System.out.println("NAME photos " + images.get(i));
                        mThumbIds1.add(MainActivity.FOLDER_NAME+"/"+images.get(i));
                        System.out.println("NAME photos " + mThumbIds1.get(i));*/
                    }
                    Log.e("BucketDisplaySize:", ""+photoSize);
                    Log.e("BucketDisplaySize:", MainActivity.FOLDER_NAME);

                    int currentImageId = Collections.max(idListOfImages);
                    //int curentImageId = customViewPhotosHolders.get(photoSize).getId();

                    /*Function call to display images in the gridview. */
                    //setImage(BucketDisplay.this, mRecyclerView, 1);
                    Log.e("SomeError:", ""+PHOTOS_SIZE);

                    if(PHOTOS_SIZE == 0){
                        Log.e("Early PHOTO_ID1:", ""+PHOTOS_SIZE);

                        /* Then bring the name of images from database */
                        RefreshPhotos(0,customViewPhotosHolders);
                        /*PHOTOS_SIZE = photoSize;
                        Log.e("Update PHOTO_ID:", ""+PHOTOS_SIZE);*/
                    }
                    else if(PHOTOS_SIZE == photoSize){
                        Log.e("Early PHOTO_ID2:", ""+PHOTOS_SIZE);
                        /* Call the gridview directly, no need to refresh the bucket*/
                        /* ImageAdapter is called to view the images. */
                   //     gridview.setAdapter(new ImageAdapter(getApplicationContext()));

                        mAdapter = new ViewHolderRecycler(result);
                        mRecyclerView.setAdapter(mAdapter);


                    }
                    else if(PHOTOS_SIZE < photoSize){
                        Log.e("Early PHOTO_ID3:", ""+PHOTOS_SIZE);
                        /* bring the names of the latest images from the database  */
                        RefreshPhotos(PHOTOS_SIZE,customViewPhotosHolders);
                        PHOTOS_SIZE = photoSize;
                        Log.e("Update PHOTO_ID:", ""+PHOTOS_SIZE);
                    }
                    else{
                        Log.e("SomeError:", ""+PHOTOS_SIZE);
                    }
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

    // [START RefreshPhotos]
    public void RefreshPhotos(int startIdValue,List<CustomViewPhotosHolder> photoHolderArray){

        List<String> images = new ArrayList<String>();
        int startingPoint=0;
        Log.e("StartIdValue", ""+startIdValue);
        Log.e("mTHMbs", ""+mThumbIds1.size());
        for(int i = startIdValue, count = photoSize; i < count; i++)
        {
            images.add(photoHolderArray.get(i).getImage_url());
            System.out.println("NAME photos " + images.get(i-startIdValue));
            mThumbIds1.add(MainActivity.FOLDER_NAME+"/"+images.get(i-startIdValue));
            System.out.println("NAME photos " + mThumbIds1.get(i-startIdValue));

            ++startingPoint;
        }
        Log.e("BucketDisplaySize2:", ""+photoSize);
        Log.e("BucketDisplaySize2:", MainActivity.FOLDER_NAME);

        PHOTOS_SIZE = photoSize;
        Log.e("Update PHOTO_ID:", ""+PHOTOS_SIZE);

        /*Function call to display images in the gridview. */
        setImage(BucketDisplay.this, mRecyclerView, 1,startingPoint);
    }
    // [END RefreshPhotos]

    // [START setImage]
    private void setImage(final Context context, final ViewGroup parent, final int id,final int startpoint) {


        new AsyncTask<Void, Void, List<String>>() {

            @Override
            protected List<String> doInBackground(Void... params) {

                try {
                    int firstIndex=0;
                    if(startpoint == mThumbIds1.size()){
                        firstIndex =0;
                    }
                    else if(startpoint != 0){
                        firstIndex =mThumbIds1.size()-startpoint-1;
                    }

                    /* URL is generated for the each image in the S3 bucket*/
                    Log.e("URL: ", "started "+ mThumbIds1.size());
                    Log.e("URL: ", "index "+ firstIndex);
                    for(int i=firstIndex;i< mThumbIds1.size();i++) {

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
             //   gridview.setAdapter(new ImageAdapter(context));

                int mNoOfColumns = Utility.calculateNoOfColumns(getApplicationContext());
                mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), mNoOfColumns));

//       mAdapter = new ViewHolderRecycler(getActivity(), mActionListener);
                mAdapter = new ViewHolderRecycler(result);
                mRecyclerView.setAdapter(mAdapter);
            }
        }.execute();
    }

    // [END setImage]

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.CrEventBucket) {
            // Handle the create event action
            Toast.makeText(getApplicationContext(),"I will create Album", Toast.LENGTH_LONG).show();

            // Mark that the user has asked for event creation from Bucket
            SharedPreferences.Editor editor = MainActivity.app_preferences.edit();
            editor.putInt("FROM_BUCKET", ++MainActivity.FROM_BUCKET);
            editor.commit(); // Very important

            /*  Starting the CreatEventsActivity which retums its result to BucketDisplay. */
            Intent myIntent1 = new Intent(BucketDisplay.this, CreateEventsActivity.class);
            startActivityForResult(myIntent1, BUCKET_TRIG_CREVENT);


        }  else if (id == R.id.GuestBucket) {

            // --- open the dialog box --

            alertDialogBuilder1 = new AlertDialog.Builder(BucketDisplay.this);

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            LayoutInflater inflater = BucketDisplay.this.getLayoutInflater();
            final View v_iew=inflater.inflate(R.layout.dialog_guest, null);       // Pass null as the parent view because its
            // going in the dialog layout
            alertDialogBuilder1.setView(v_iew);
            // Add action buttons
            alertDialogBuilder1.setPositiveButton("Enter",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                                /* Get the event id entered in the dialog box. */
                            event_idd = (EditText) v_iew.findViewById(R.id.Event_idd);  /*Find ViewById is called on v_iew because
                                                                                            it is inside the dialog box */
                            System.out.println("Pressed code enter");
                            MainActivity.EVENT_ID= event_idd.getText().toString();

                                /*api call is made to check if the entered event code exists. */
                            ApiInterface apiService1 = ApiClient.createService(ApiInterface.class);
                            Call<ResponseBody> call1 = apiService1.getEventsExits(MainActivity.EVENT_ID);
                            call1.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call1, Response<ResponseBody> response) {
                                    int statuscode = response.code();

                                    Log.e("Check Event existence", "Response: "+statuscode);
                                    if (response.body() != null){

                                            /* the BucketDisplay.java file is opened to display photos of event. */
                                        MainActivity.FOLDER_NAME = "img"+MainActivity.EVENT_ID;
                                        try {
                                            System.out.println(response.body().string());

                                            /* the user is logged in so save this event id in the guest code column of database*/
                                            ApiInterface apiService = ApiClient.createService(ApiInterface.class);
                                            Call<CustomViewResponse> call2 = apiService.guestCodeEntry(LoginActivity.EMAIL_ID,MainActivity.EVENT_ID);
                                            call2.enqueue(new Callback<CustomViewResponse>() {
                                                @Override
                                                public void onResponse(Call<CustomViewResponse> call3, Response<CustomViewResponse> respons) {
                                                    int statuscode = respons.code();

                                                    Log.d("Message", "code..." + respons.code() + " message..." + respons.message());

                                                    CustomViewResponse respon = respons.body();
                                                    if (respon == null) {
                                                        Log.e("Error", "" + statuscode + "......" + respons.message() + "....null body");
                                                    } else {
                                                        Log.e("Success", "" + statuscode + "."+MainActivity.EVENT_ID + respons.message() + "guest code added to user");
                                                        WhatToDisplay();
                                                    }
                                                }
                                                @Override
                                                public void onFailure(Call<CustomViewResponse> call2, Throwable t) {

                                                    Log.e("EVENT Posting Failed", t.toString());
                                                    Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }catch (IOException e) {
                                            e.printStackTrace();
                                        }
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
        else if (id == R.id.nav_terms) {
            Intent myIntent = new Intent(BucketDisplay.this, TermsAndConditions.class);
            BucketDisplay.this.startActivity(myIntent);

        }else if (id == R.id.nav_privacy) {
            Intent myIntent = new Intent(BucketDisplay.this, PrivacyPolicy.class);
            BucketDisplay.this.startActivity(myIntent);

        } else if (id == R.id.nav_community) {
            Intent myIntent = new Intent(BucketDisplay.this, CommunityGuidelines.class);
            BucketDisplay.this.startActivity(myIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //close navigation menu on back press
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



   /* public class ImageAdapter extends BaseAdapter {
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

         *//*create a new ImageView for each item referenced by the Adapter*//*
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

            *//* Loading the images to gridview using Picasso library. *//*
            Picasso.with(mContext)
                    .load(result.get(position))
                    .placeholder(R.drawable.s_9)
                    .error(R.drawable.s_9)
                    //.resize(60, 60)
                    //.centerInside()
                    .into(imageView);
            return imageView;
        }
    }*/
    public class ViewHolderRecycler extends RecyclerView.Adapter<MyViewHolder>{
        private Context mContext;
        List<String> photoListItem;

        public ViewHolderRecycler(List<String> photoListItem){
            this.photoListItem=photoListItem;


        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_image_recycle, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder,final int position) {

            Picasso.with(getApplicationContext())
                    .load(result.get(position))
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_error)
                    //.resize(90, 90)
                    //.centerInside()
                    .into(holder.imageThumb);

            holder.imageThumb.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Intent intent= new Intent(BucketDisplay.this,PhotoActivity.class);
                    intent.putExtra("position",position);
                    BucketDisplay.this.startActivity(intent);


                }
            });

        }

        @Override
        public int getItemCount() {
            return photoListItem.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        final public ImageView imageThumb;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageThumb = (ImageView) itemView.findViewById(R.id.imageGridItem);
        }
    }


}