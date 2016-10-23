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

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.util.ArrayList;

import static com.example.sidra.pixliapp.BucketDisplay.s3;

/**
 * Created by sidra on 23-10-2016.
 */

public class UploadActivity extends Activity{


    TransferUtility transferUtility;
    private static int PHOTO_SELECTED = 0;
    private static int PICK_IMAGE_MULTIPLE = 1;

    File fileToUpload;
    String picturePath = "";

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
        Log.i("Intent", "onClick2");

        startActivityForResult(myIntent1, PICK_IMAGE_MULTIPLE);
        Log.i("Intent", "onClick3");
        //setFileToUpload()
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHOTO_SELECTED && resultCode == RESULT_OK && null != data) {

            Log.i("my Main", "updating in gallery");
            Uri selectedImage = data.getData();


            try {

                picturePath = getPath(getApplicationContext(), selectedImage);
                Log.i("my Main", "updating.......");
                fileToUpload = new File(picturePath);
                Log.i("my Main", "updating....... 01");
                TransferObserver transferObserver = transferUtility.upload("pixliapp01", "images/pix1.jpg", fileToUpload);
                Log.i("my Main", "updating....... 02");
                transferObserverListener(transferObserver);
            } catch (Exception e) {
                Log.i("my Main", "updating......." + e);
            }
        }

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
                // lnrImages.addView(imageView);


                String picturePath = imagesPath[i];
                Log.i("my Main", "updating.......");
                fileToUpload = new File(picturePath);
                Log.i("my Main", "updating....... 01");
                TransferObserver transferObserver = transferUtility.upload("pixliapp01", "images/" + i + 8 + "es.jpg", fileToUpload);
                Log.i("my Main", "updating....... 02");
                transferObserverListener(transferObserver);
            }
        }
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


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
