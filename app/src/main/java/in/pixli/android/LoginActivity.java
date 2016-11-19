package in.pixli.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.pixli.sidra.android.R;

import in.pixli.android.retrofit.ApiClient;
import in.pixli.android.retrofit.ApiInterface;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

import static in.pixli.android.MainActivity.CLICKED_CREVENT;
import static in.pixli.android.MainActivity.LOGGED_IN;

/**
 * Created by sidra on 01-11-2016.
 */

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mGoogleApiClient;
    private static final String CLIENT_ID = "";
    private static final String WEB_CLIENT_ID="312796068697-2vgqnsd95ukghbhj7uvael4oq48gt515.apps.googleusercontent.com";

    CallbackManager callbackManager;
    LoginButton loginButton;

    String email_Id,displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Facebook sdk initialized before setting the layout view */
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(LoginActivity.this);
        setContentView(R.layout.login_activity);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
//        findViewById(R.id.sign_out_button).setOnClickListener(this);
//        findViewById(R.id.disconnect_button).setOnClickListener(this);

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
        // rendered based on the requested scopes. For pixli. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        ImageView signInButton = (ImageView) findViewById(R.id.sign_in_button);
        /*signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
*/
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        /* This the code for facebook login */

        // [START Facebook Login]
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setBackgroundResource(R.drawable.login_with_f);
        loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email"));
        //loginButton.setReadPermissions("email");

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        AccessToken access_token;
                        access_token = loginResult.getAccessToken();
                        System.out.println(access_token.getToken());
                        // App code
                        GraphRequest request = GraphRequest.newMeRequest(
                                access_token,
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());
                                        // Application code
                                        if (response != null) {
                                            try {
                                                String name = object.getString("name");
                                                String email = object.getString("email");

                                                System.out.println("Email = " + email);
                                                System.out.println("Name = " + name);

                                                email_Id = email;
                                                displayName = name;

                                                /* call to function */
                                                userDataEntry();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
            // [END Facebook Login]
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
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

        callbackManager.onActivityResult(requestCode, resultCode, data);

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
            displayName = acct.getDisplayName();
            System.out.println(displayName);
            //String idToken = result.getSignInAccount().getIdToken();
            String authCode = acct.getServerAuthCode();
            //var id_token = acct.getAuthResponse().id_token;
            System.out.println(authCode);
            email_Id = acct.getEmail();
            System.out.println(email_Id);

            /* call to function */
            userDataEntry();

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
//            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.disconnect_button).setVisibility(View.VISIBLE);
        } else {
            //mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
//            findViewById(R.id.disconnect_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        signIn();
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
//            case R.id.sign_out_button:
//                signOut(v);
//                break;
//            case R.id.disconnect_button:
//                revokeAccess(v);
//                break;
        }
    }

    // [START userDataEntry]
    public void userDataEntry(){

        /* Api call to send user data to the database (creates user id if not already existing). */
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
    }
    // [END userDataEntry]

    // [START getToken]
    public void getToken(final String email_Id, final String displayName){

        // Api call to exchange email_Id and displayName for a token
        ApiInterface apiService1 = ApiClient.createService(ApiInterface.class,email_Id,displayName);
        Call<ResponseBody> call1 = apiService1.getToken();
        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call1,Response<ResponseBody> response){
                int statuscode = response.code();

                Log.e("INSIDE GET TOKEN", "Response: " + statuscode);

                if (response.body() != null) {

                    try {
                        System.out.println("CHECK HERE" + response.body().string());

                        // Mark that the user has successfully logged in
                        SharedPreferences.Editor editor = MainActivity.app_preferences.edit();
                        editor.putInt("LOGGED_IN", ++MainActivity.LOGGED_IN);
                        editor.commit(); // Very important

                        if (CLICKED_CREVENT == 1) {     //CLICKED_CREVENT declared in MainActivity.java
                             /* Call the onActivityResult in CreateEventsActivity class */
                            Intent i = new Intent();
                            i.putExtra("data", email_Id);
                            setResult(Activity.RESULT_OK, i);
                            finish();
                        }
                        else{
                            /* Start Event list for the event_Id entered by guest (in MainActivty). */
                            Intent myIntent = new Intent(LoginActivity.this, Event_List.class);
                            myIntent.putExtra("data", email_Id);
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
    // [END getToken]

}
