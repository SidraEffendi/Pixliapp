package com.example.sidra.pixliapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by sidra on 22-10-2016.
 *
 * This class is responsible for enlarging the photo when clicked in the gridview of BucketDisplay class
 */

public class PhotoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        int position = getIntent().getIntExtra("position",-1);
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

}
