package com.jojikubota.android.rentme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.jojikubota.android.rentme.Controller.NewPostActivity;
import com.jojikubota.android.rentme.Controller.SearchActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class AuthenticationActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProgressDialog progressDialog;
    private ProfileTracker profileTracker;
    private LoginButton loginButton;
    private String email;



    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Profile profile = Profile.getCurrentProfile();
            nextActivity(profile);

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.content_authentication);
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };
        profileTracker = new

                ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                        nextActivity(currentProfile);
                    }
                };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_about_me", "user_friends", "user_birthday", "user_location"));
        loginButton.registerCallback(callbackManager, callback);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                System.out.println("onSuccess");
                progressDialog = new ProgressDialog(AuthenticationActivity.this);
                progressDialog.setMessage("Processing Data...");
                progressDialog.show();
                String accessToken = loginResult.getAccessToken().getToken();
                Log.i("accessToken", accessToken);

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("AuthenticationActivity", response.toString());
                        // Get facebook data from login
                        Bundle bFacebookData = getFacebookData(object);
                        Log.d("FB_SESSION", bFacebookData.getString("email"));
                        Log.d("FB_SESSION", bFacebookData.getString("profile_pic"));

                        // Save fb email for lookup
                        SharedPreferences sharedPref = getSharedPreferences("FB_PROFILE", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("fbEmail", bFacebookData.get("email").toString());
                        editor.putString("fbPic", bFacebookData.get("profile_pic").toString());
                        editor.putString("fbFirstName", bFacebookData.get("first_name").toString());
                        editor.putString("fbLastName", bFacebookData.get("last_name").toString());

                        editor.commit();

                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,gender,birthday,location");
                request.setParameters(parameters);
                request.executeAsync();



                Log.d("response",request.toString());
            }

            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                System.out.println("onError");
                Log.v("LoginActivity", exception.getCause().toString());
            }
        });
    }


    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }


            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;


        } catch (Exception e) {

        }
        return null;
    }
    @Override
    protected void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        nextActivity(profile);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        callbackManager.onActivityResult(requestCode, responseCode, intent);
    }

    private void nextActivity(Profile profile) {
        if (profile != null) {
            Intent main = new Intent(AuthenticationActivity.this, SearchActivity.class);
            main.putExtra("name", profile.getFirstName());
            main.putExtra("surname", profile.getLastName());
            main.putExtra("imageURL", profile.getProfilePictureUri(200, 200).toString());

            startActivity(main);
        }
    }
}
