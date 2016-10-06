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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.jojikubota.android.rentme.AuthenticationActivity;
import com.jojikubota.android.rentme.Model.Posting;
import com.jojikubota.android.rentme.Model.PostingList;
import com.jojikubota.android.rentme.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by joji on 5/1/16.
 */
public class UpdatePostDetailFragment extends Fragment {

    // Variables
    private EditText mPostId;
    private RadioGroup mStatus;
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
    private ImageButton mDeleteButton;
    private PostingList mPostingList;
    private Posting mPosting;
    private RadioButton mStatusButton;
    private String mPostIdStr;

    private Uri mUri;
    private static final int REQUEST_CHOOSER = 1000;
    private static final String STATE_URI = "state_uri";

    private ListView mDrawerList;
    private ArrayAdapter<String> mArrayAdapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;

    private String mGETResponse;

    private static final String ARG_POST_ID = "post_id";

    // Custom initializer to save fragment arguments
    public static UpdatePostDetailFragment newInstantce(String postId) {
        Bundle args = new Bundle();
        args.putCharSequence(ARG_POST_ID, postId);

        UpdatePostDetailFragment fragment = new UpdatePostDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Notify that the option menu exits
        setHasOptionsMenu(true);

        // Retrieve fragment arguments
        mPostIdStr = (String) getArguments().getCharSequence(ARG_POST_ID);
//        mPosting = PostingList.get(getActivity()).getPosting(postId);

        // Get posting for the user
        getPosting();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Thread.yield();

        // Inflate main view.
        final View view = inflater.inflate(R.layout.fragment_post_update, container, false);

        // Setup navigation drawer
        setNavigationDrawer(view);

        // Create references and populate current values
        mPostId = (EditText)view.findViewById(R.id.post_id);
        mStatus = (RadioGroup)view.findViewById(R.id.radio_status);
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
        mSelectButton = (ImageButton)view.findViewById(R.id.select_button);
        mResetButton = (ImageButton)view.findViewById(R.id.reset_button);
        mSaveButton = (ImageButton)view.findViewById(R.id.save_button);
        mDeleteButton = (ImageButton)view.findViewById(R.id.delete_button);
        mPhoto = (ImageView)view.findViewById(R.id.photo);

        int statusID = mStatus.getCheckedRadioButtonId();
        mStatusButton = (RadioButton) view.findViewById(statusID);



        return view;
    }


    // Helper to obtain posting info
    private void getPosting() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Set up http call
                String endPoint = "http://ec2-52-39-228-217.us-west-2.compute.amazonaws.com:3000/";
                // Remove space
                String cleanedPostId = mPostIdStr.replaceAll(" ", "%20");
                endPoint += cleanedPostId;

                // Setup a http client
                HttpClient httpClient = new DefaultHttpClient();
                // Prepare a request object
                HttpGet httpGet = new HttpGet(endPoint);

                // Execute request
                HttpResponse httpResponse;
                try {
                    httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();

                    if (httpEntity != null) {
                        InputStream inputStream = httpEntity.getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line = null;
                        try {
                            while ((line = reader.readLine()) != null) {
                                stringBuilder.append(line + "\n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        mGETResponse = stringBuilder.toString();

                        Log.d("UPDATE_POST", mGETResponse);

                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                Log.d("POST_EDIT", "IN onPostExecute");
                // Setup mPostingList
                Gson gson = new Gson();
                mPosting = gson.fromJson(mGETResponse, Posting.class);

                Log.d("POST_EDIT", mPosting.toString());

                setupViewItems();
            }

        }.execute();

    }


    private void setupViewItems() {

//        System.out.println("Status: " + mPosting.getStatus());
//        if (mPosting.getStatus() == null ||
//                mPosting.getStatus().equals("For Rent")) {
//            mStatus.check(R.id.radio_button_forrent);
//        } else if (mPosting.getStatus().equals("Rented")) {
//            mStatus.check(R.id.radio_button_rented);
//        } else {
//            mStatus.check(R.id.radio_button_cancelled);
//        }

        mPostId.setText(mPosting.getPostId());
        mPostId.setFocusable(false); // Do not allow edit
        mStreet.setText(mPosting.getStreet());
        mCity.setText(mPosting.getCity());
        mState.setText(mPosting.getState());
        mZip.setText(mPosting.getZip());
        mType.setText(mPosting.getType());
        mRooms.setText(mPosting.getRooms());
        mBaths.setText(mPosting.getBaths());
        mSqft.setText(mPosting.getSqft());
        mRent.setText(mPosting.getRent());
        mPhone.setText(mPosting.getPhone());
        mEmail.setText(mPosting.getEmail());
        mEmail.setFocusable(false); // Do not allow edit
        mDescription.setText(mPosting.getDescription());
//        mPhoto.setImageBitmap(mPosting.getPhoto());


        // Set up the singleton (data container)
//        mPostingList = PostingList.get(getContext());

        // Take a photo or pick an image from Gallery when SELECT is tapped.
        setSelectButtonListener() ;


        // Reset the photo to default
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set Image
                mPhoto.setImageResource(R.drawable.rent_me_img);
            }
        });

        // Save the entry when SAVE is tapped
        setSaveButtonListener();

        // Delete the entry when DELETE is tapped
        setDeleteButtonListener();

    }

    class UpdateDb extends AsyncTask<Posting, Void, Void> {
        @Override
        protected Void doInBackground(Posting... postings) {

            Posting posting = postings[0];
            // Create json string
            Gson gsonStr = new Gson();
            final String jsonStr = gsonStr.toJson(posting);
            Log.d("JSON", jsonStr);

            // Set up http call
            String endPoint = "http://ec2-52-39-228-217.us-west-2.compute.amazonaws.com:3000/rent";
            endPoint += "/";
            // Remove space
            String cleanedPostId = mPostIdStr.replaceAll(" ", "%20");
            endPoint += cleanedPostId;

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
                conn.setRequestMethod("PUT");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            conn.setDoOutput(true);

            OutputStream outputStream = null;
            try {
                conn.connect();
                outputStream = conn.getOutputStream();
                outputStream.write(jsonStr.getBytes());
                outputStream.close();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    System.out.println("HTTP_OK");
                } else {
                    System.out.println("HTTP_NOT_OK");
                }
            } catch (IOException e) {
                System.out.println("Unable to send message." + e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

        }
    }


    // Helper to listen to the delete button
    private void setDeleteButtonListener() {

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Set up http call
                String endPoint = "http://ec2-52-39-228-217.us-west-2.compute.amazonaws.com:3000/rent/";
                // Remove space
                String cleanedPostId = mPosting.getPostId().replaceAll(" ", "%20");
                endPoint += cleanedPostId;


                final String finalEndPoint = endPoint;
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        // Set up new http client
                        HttpClient httpClient = new DefaultHttpClient();

                        // Set up new http response
                        HttpResponse httpResponse = null;

                        try {
                            HttpDelete httpDelete = new HttpDelete(finalEndPoint);
                            httpResponse = httpClient.execute(httpDelete);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Log.d("FAV_TOGGLE", "response code: " + (httpResponse.getStatusLine().getStatusCode()));

                        return null;
                    }


                    @Override
                    protected void onPostExecute(Void v) {
                        Log.d("DEL_CALL", "http call completed");

                        // Go back to the post list.
                        Intent intent = new Intent(getActivity(), EditPostActivity.class);
                        startActivity(intent);
                    }

                }.execute();


            }
        });

    }



    // Helper to listen to the saved button
    private void setSaveButtonListener() {

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
                        mType.getText().toString().equals("") ||
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

                // Remove current posting from the list
//                mPostingList.deletePosting(mPostId.getText().toString());

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

                // A little work to save the rental status
                posting.setStatus(mStatusButton.getText().toString());
                System.out.println(mStatusButton.getText().toString());

                // Add to list
//                mPostingList.addPostings(posting);
                UpdateDb updateDb = new UpdateDb();
                updateDb.execute(posting);


                // Dismiss the keyboard
                InputMethodManager imm = (InputMethodManager) (getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                // Send a message to the user
                Toast.makeText(getActivity(), "DATA UPDATED", Toast.LENGTH_SHORT).show();

                // Go back to the post list.
                Intent intent = new Intent(getActivity(), EditPostActivity.class);
                startActivity(intent);
            }
        });

    }

    // Helper to listen to the select button
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

            // Scan gallary
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

    // Helper to set up the navi drawer
    private void setNavigationDrawer(View view) {
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
        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout_post_detail);
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
