package com.example.sidra.pixliapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sidra.pixliapp.retrofit.ApiClient;
import com.example.sidra.pixliapp.retrofit.ApiInterface;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.RelativeLayout.TRUE;
import static com.example.sidra.pixliapp.MainActivity.CLICKED_CREVENT;
import static com.example.sidra.pixliapp.MainActivity.LOGGED_IN;
import static com.example.sidra.pixliapp.MainActivity.app_preferences;

/**
 * Created by sidra on 01-11-2016.
 */

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private static final String CLIENT_ID = "";
    private static final String WEB_CLIENT_ID="312796068697-2vgqnsd95ukghbhj7uvael4oq48gt515.apps.googleusercontent.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        ///////////scope comes here
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestServerAuthCode(WEB_CLIENT_ID)
                .requestIdToken(WEB_CLIENT_ID) //It gets name, email,etc and dont need to add anything else
                .build();

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

       /* mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();*/
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            //Log.d(TAG, "Got cached sign-in");
            System.out.println("inside to check if already logged in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            System.out.println(".........inside to check if already logged in");
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            System.out.println("new sign in");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            final String displayName = acct.getDisplayName();
            System.out.println(displayName);
            //String idToken = result.getSignInAccount().getIdToken();
            String authCode = acct.getServerAuthCode();
            //var id_token = acct.getAuthResponse().id_token;
            //System.out.println(idToken);
            System.out.println(authCode);
            final String email_Id = acct.getEmail();
            System.out.println(email_Id);

            ApiInterface apiService = ApiClient.createService(ApiInterface.class);
            Call<Void> call1 = apiService.createUsers(displayName,email_Id);
            call1.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call1, Response<Void> respo) {
                    int statuscode = respo.code();

                    Log.d("Message", "code..." + respo.code() + " message..." + respo.message());
                    System.out.println("Calling getToken");
                    getToken(email_Id,displayName);

                    Void respon = respo.body();

                    if (respon == null) {
                        Log.e("Error", "" + statuscode + "......" + respo.message() + "....null body");
                    }
                    else{
                        //System.out.println("Calling getToken");
                        //getToken();
                    }
                }
                @Override
                public void onFailure(Call<Void> call1, Throwable t) {

                    Log.e(TAG, t.toString());
                }
            });

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }

    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut(View v) {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess(View v) {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        /*if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();*/
    }

    private void hideProgressDialog() {
        /*if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }*/
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            findViewById(R.id.disconnect_button).setVisibility(View.VISIBLE);
        } else {
            //mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            findViewById(R.id.disconnect_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        signIn();
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut(v);
                break;
            case R.id.disconnect_button:
                revokeAccess(v);
                break;
        }
    }

    public void getToken(final String email_Id, final String displayName){
        ApiInterface apiService1 = ApiClient.createService(ApiInterface.class,email_Id,displayName);
        Call<ResponseBody> call1 = apiService1.getToken();
        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call1,Response<ResponseBody> response){
                int statuscode = response.code();

                Log.e("INSIDE GET TOKEN", "Response: " + statuscode);

                if (response.body() != null) {
                    // Get the image urls from the response body and store it in an array mThumbIds
                    //PHOTO_COUNT =1;
                    try {
                        System.out.println("CHECK HERE" + response.body().string());

                        // Mark the user the user has successfully logged in
                        SharedPreferences.Editor editor = app_preferences.edit();
                        editor.putInt("LOGGED_IN", ++LOGGED_IN);
                        editor.commit(); // Very important

                        if (CLICKED_CREVENT == 1) {
                            Intent myIntent = new Intent(LoginActivity.this, Events.class);
                            myIntent.putExtra("email_Id", email_Id);
                            LoginActivity.this.startActivity(myIntent);
                            finish();
                        }
                        else{
                            Intent myIntent = new Intent(LoginActivity.this, Event_List.class);
                            myIntent.putExtra("email_Id", email_Id);
                            LoginActivity.this.startActivity(myIntent);
                            finish();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Error", "" + statuscode + "......" + "....null body");
                    Toast.makeText(getApplicationContext(), "token null", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call1,Throwable t) {
                t.printStackTrace();
            }
        });

    }
}
