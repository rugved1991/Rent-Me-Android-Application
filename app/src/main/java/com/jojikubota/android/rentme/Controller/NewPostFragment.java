package com.jojikubota.android.rentme.Controller;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.jojikubota.android.rentme.AuthenticationActivity;
import com.jojikubota.android.rentme.Model.Posting;
import com.jojikubota.android.rentme.R;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by joji on 4/17/16.
 */

public class NewPostFragment extends Fragment {

    //Nikhil: from facebook
    private String mUserFirstName;
    private String mUserLastName;
    private String mUserEmail;
    private String mProfilePic;

    // Variables
    private EditText mPostId;
    private EditText mStreet;
    private EditText mCity;
    private EditText mState;
    private EditText mZip;
    private EditText mType;
    private EditText mRooms;
    private EditText mBaths;
    private EditText mSqft;
    private EditText mRent;
    private EditText mPhone;
    private EditText mEmail;
    private EditText mDescription;
    private ImageView mPhoto;
    private ImageButton mSelectButton;
    private ImageButton mResetButton;
    private ImageButton mSaveButton;

    private Uri mUri;
    private static final int REQUEST_CHOOSER = 1000;
    private static final String STATE_URI = "state_uri";

    // For navigation drawer
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Notify the option menu exits
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate main view.
        View view = inflater.inflate(R.layout.fragment_post_new, container, false);

        // Retrieve the user email from fb profile
        getFacebookProfile();

        // Set the Drawer with fb profile
        setFacebookProfile();

        // Setup navigation drawer
        setNavigationDrawer(view);

        // Create references to the input texts
        setReferences(view);

        // Populate the email box
        mEmail.setText(mUserEmail);
        mEmail.setFocusable(false); // not editable by user

        // Take a photo or pick an image from Gallery when SELECT is tapped.
        setSelectButtonListener();

        // Reset the photo to default
        setResetButtonListener();

        // Save the entry when SAVE is tapped
        setSaveButtonListern();

        return view;
    }

    // Handle results from the camera or image gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println("in Fragment.onActivityResult");

        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK) {
            // canceled
            return ;
        }

        if(requestCode == REQUEST_CHOOSER) {
            Uri resultUri = (data != null ? data.getData() : mUri);
            System.out.println("resultUri: " + resultUri);
            System.out.println("data: " + data);

            if(resultUri == null) {
                // failed to obtain uri
                return;
            }

            // Scan gallery
            MediaScannerConnection.scanFile(
                    getActivity(),
                    new String[]{resultUri.getPath()},
                    new String[]{"image/jpeg"},
                    null
            );

            // Set Image
            mPhoto.setImageURI(resultUri);
        }
    }

    // Save & Restore mUri (gets lost when another activity is called)
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_URI, mUri);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_URI)) {
                mUri = savedInstanceState.getParcelable(STATE_URI);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }


        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Helper to set SAVE button listener
    private void setSaveButtonListern() {
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("SAVE BUTTON TAPPED");

                // Field validation
                if (mPostId.getText().toString().equals("") ||
                        mStreet.getText().toString().equals("") ||
                        mCity.getText().toString().equals("") ||
                        mState.getText().toString().equals("") ||
                        mZip.getText().toString().equals("") ||
                        mType.getText().toString().equals("") |
                        mRooms.getText().toString().equals("") ||
                        mBaths.getText().toString().equals("") ||
                        mSqft.getText().toString().equals("") ||
                        mRent.getText().toString().equals("") ||
                        mEmail.getText().toString().equals("")
                        ) {
                    Toast.makeText(getActivity(), "Please fill the required fields", Toast.LENGTH_LONG).show();

                    // Dismiss the keyboard
                    InputMethodManager imm = (InputMethodManager) (getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                    return;
                }

                // New posting to save data
                final Posting posting = new Posting();

                // Save data
                posting.setPostId(mPostId.getText().toString());
                posting.setStreet(mStreet.getText().toString());
                posting.setCity(mCity.getText().toString());
                posting.setState(mState.getText().toString());
                posting.setZip(mZip.getText().toString());
                posting.setType(mType.getText().toString());
                posting.setRooms(mRooms.getText().toString());
                posting.setBaths(mBaths.getText().toString());
                posting.setSqft(mSqft.getText().toString());
                posting.setRent(mRent.getText().toString());
                posting.setPhone(mPhone.getText().toString());
                posting.setEmail(mEmail.getText().toString());
                posting.setDescription(mDescription.getText().toString());

                // A little work to save the image
                BitmapDrawable drawable = (BitmapDrawable) mPhoto.getDrawable();
                if (drawable != null) {
                    Bitmap bitmap = drawable.getBitmap();
//                    posting.setPhoto(bitmap);
                }


                // Save in db
                // Create json string
                Gson gsonStr = new Gson();
                final String jsonStr = gsonStr.toJson(posting);
                Log.d("JSON", jsonStr);

                // Set up http call
                String endPoint = "http://ec2-52-39-228-217.us-west-2.compute.amazonaws.com:3000/rent";

                URL url = null;
                try {
                    url = new URL(endPoint);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                conn.setRequestProperty("Content-Type", "application/json");
                try {
                    conn.setRequestMethod("POST");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                conn.setDoOutput(true);

                // Send message content.
                final HttpURLConnection finalConn = conn;
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        OutputStream outputStream = null;
                        try {
                            finalConn.connect();
                            outputStream = finalConn.getOutputStream();
                            outputStream.write(jsonStr.getBytes());
                            outputStream.close();

                            if (finalConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                System.out.println("HTTP_OK");
                            } else {
                                System.out.println("HTTP_NOT_OK");
                            }
                        } catch (IOException e) {
                            System.out.println("Unable to send message." + e);
                        }

                        return null;
                    }

                    protected void onPostExecute() {
                        System.out.println("http call completed");
                    }

                }.execute();


                // Dismiss the keyboard
                InputMethodManager imm = (InputMethodManager) (getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                // Send a message to the user
                Toast.makeText(getActivity(), "DATA SAVED", Toast.LENGTH_SHORT).show();
                System.out.println("DATA SAVED");

                // Clear the fields
                mPostId.setText("");
                mStreet.setText("");
                mCity.setText("");
                mState.setText("");
                mZip.setText("");
                mType.setText("");
                mRooms.setText("");
                mBaths.setText("");
                mSqft.setText("");
                mRent.setText("");
                mPhone.setText("");
                mEmail.setText("");
                mDescription.setText("");
//                mPhoto.setImageResource(0);
                mPhoto.setImageResource(R.drawable.rent_me_img);
            }
        });

    }

    // Helper to set RESET button listener
    private void setResetButtonListener() {
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set Image
                mPhoto.setImageResource(R.drawable.rent_me_img);
            }
        });
    }

    // Helper to set SELECT button listener
    private void setSelectButtonListener() {
        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Intent to open a camera app
                String imgName = System.currentTimeMillis() + ".jpg";
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.TITLE, imgName);
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                mUri = getActivity().getContentResolver()
                        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);

                // Intent to open image app
                Intent galleryIntent;
                if (Build.VERSION.SDK_INT < 19) {
                    galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                } else {
                    galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    galleryIntent.setType("image/jpeg");
                }
                Intent intent = Intent.createChooser(cameraIntent, "Select Image");
                intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {galleryIntent});
                startActivityForResult(intent, REQUEST_CHOOSER);
            }
        });
    }

    // Helper to set facebook profile on navi drawer
    private void setFacebookProfile() {

        // Obtain the root view first to find the header.xml
        LayoutInflater factory = getActivity().getLayoutInflater();
        View headerView = factory.inflate(R.layout.header, null);

        // Name
        String userFullName = mUserFirstName + " " + mUserLastName;
        TextView userProfileName = (TextView) headerView.findViewById(R.id.name);
        userProfileName.setText(userFullName);

        // Email
        TextView userProfileEmail = (TextView) headerView.findViewById(R.id.email);
        userProfileEmail.setText(mUserEmail);

        // Image

        CircleImageView userImage = (CircleImageView) headerView.findViewById(R.id.fb_pic);
    }

    // Helper to get facebook profile
    private void getFacebookProfile() {

        SharedPreferences sharedPref = getContext().getSharedPreferences("FB_PROFILE", getContext().MODE_PRIVATE);
        mUserFirstName = sharedPref.getString("fbFirstName", "NoFirstName");
        mUserLastName = sharedPref.getString("fbLastName", "NoLastName");
        mUserEmail = sharedPref.getString("fbEmail", "NoEmail");
        mProfilePic = sharedPref.getString("fbPic", "NoPic");
        Log.d("FB_RETRIEVE", mUserFirstName);
        Log.d("FB_RETRIEVE", mUserLastName);
        Log.d("FB_RETRIEVE", mUserEmail);
        Log.d("FB_RETRIEVE", mProfilePic);


    }

    // Helper to set up references
    private void setReferences(View view) {

        mPostId = (EditText)view.findViewById(R.id.post_id);
        mStreet = (EditText)view.findViewById(R.id.street);
        mCity = (EditText)view.findViewById(R.id.city);
        mState = (EditText)view.findViewById(R.id.state);
        mZip = (EditText)view.findViewById(R.id.zip);
        mType = (EditText)view.findViewById(R.id.type);
        mRooms = (EditText)view.findViewById(R.id.rooms);
        mBaths = (EditText)view.findViewById(R.id.baths);
        mSqft = (EditText)view.findViewById(R.id.sqft);
        mRent = (EditText)view.findViewById(R.id.rent);
        mPhone = (EditText)view.findViewById(R.id.phone);
        mEmail = (EditText)view.findViewById(R.id.email);
        mDescription = (EditText)view.findViewById(R.id.description);
        mPhoto = (ImageView)view.findViewById(R.id.photo);
        mPhoto.setImageResource(R.drawable.rent_me_img);
        mSelectButton = (ImageButton)view.findViewById(R.id.select_button);
        mResetButton = (ImageButton)view.findViewById(R.id.reset_button);
        mSaveButton = (ImageButton)view.findViewById(R.id.save_button);
    }

    // Helper to set up the navi drawer
    private void setNavigationDrawer(View view) {

        // Initialize fb profiles


        // Initialize NavigationView
        mNavigationView = (NavigationView) view.findViewById(R.id.navigation_view_new_post);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // Trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                // Checking the item state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                // Close drawer on item click
                mDrawerLayout.closeDrawers();

                // Check which item was clicked
                Intent intent = new Intent(getActivity(), NewPostActivity.class);
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        intent = new Intent(getActivity(),SearchActivity.class);
                        break;
                    case R.id.favorite:
                        intent = new Intent(getActivity(),FavoritesActivity.class);
                        break;
                    case R.id.saved_searches:
                        intent = new Intent(getActivity(),SavedSearchesActivity.class);
                        break;
                    case R.id.post:
                        intent = new Intent(getActivity(), NewPostActivity.class);
                        break;
                    case R.id.manage_post:
                        intent = new Intent(getActivity(), EditPostActivity.class);
                        break;
                    case R.id.logout:
                        LoginManager.getInstance().logOut();
                        intent = new Intent(getActivity(), AuthenticationActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        break;
                }

                startActivity(intent);

                return true;
            }
        });

        // Setup navigation action drawer
        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout_post);
        mDrawerToggle  =
                new ActionBarDrawerToggle(getActivity(), mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                        super.onDrawerOpened(drawerView);
                    }
                };

        // Set actionbarToggle to drawer layout
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // Add Toggle switch for the navigation drawer
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_navi_drawer);
    }

}