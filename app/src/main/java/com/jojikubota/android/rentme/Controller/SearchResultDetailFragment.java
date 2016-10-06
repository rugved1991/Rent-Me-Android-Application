package com.jojikubota.android.rentme.Controller;

import com.jojikubota.android.rentme.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.jojikubota.android.rentme.AuthenticationActivity;
import com.jojikubota.android.rentme.Model.Favorite;
import com.jojikubota.android.rentme.Model.Posting;

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


/**
 * Created by joji on 5/1/16.
 */
public class SearchResultDetailFragment extends Fragment {

    // Variables
    private TextView mPostId;
    private TextView mViewedCount;
    private TextView mStreet;
    private TextView mCity;
    private TextView mState;
    private TextView mZip;
    private TextView mType;
    private TextView mRooms;
    private TextView mBaths;
    private TextView mSqft;
    private TextView mRent;
    private TextView mPhone;
    private TextView mEmail;
    private TextView mDescription;
    private ImageView mPhotoHouse;
    private ImageView mPhotoFav;
    private Posting mPosting;
    private Favorite mFavorite;
    private String mUserEmail;

    private Uri mUri;
    private static final int REQUEST_CHOOSER = 1000;
    private static final String STATE_URI = "state_uri";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;

    private String mGETResponse;

    private static final String ARG_POST_ID = "post_id";

    // Custom initializer to save fragment arguments
    public static SearchResultDetailFragment newInstance(String postId) {
        Bundle args = new Bundle();
        args.putCharSequence(ARG_POST_ID, postId);

        SearchResultDetailFragment fragment = new SearchResultDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Notify the option menu exits
        setHasOptionsMenu(true);

        // Retrieve fragment arguments
        String postId = (String) getArguments().getCharSequence(ARG_POST_ID);
//        mPosting = PostingList.get(getActivity()).getPosting(postId);

        mUserEmail = findUserEmail();

        // Search the posting via postId
        searchForPosting(postId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate main view.
        View view = inflater.inflate(R.layout.fragment_result_detail, container, false);

        // Setup navigation drawer
        setNavigationDrawer(view);

        // Create references and populate current values
        mPhotoHouse = (ImageView)view.findViewById(R.id.house_image);
        mPhotoHouse.setImageResource(R.drawable.hosue);
        mPhotoFav = (ImageView)view.findViewById(R.id.fav_image);
        mPhotoFav.setImageResource(R.drawable.unfavorite);
        mPostId = (TextView) view.findViewById(R.id.result_detail_id);
        mViewedCount = (TextView) view.findViewById(R.id.result_detail_viewed);
        mStreet = (TextView) view.findViewById(R.id.result_detail_street);
        mCity = (TextView) view.findViewById(R.id.result_detail_city);
        mState = (TextView) view.findViewById(R.id.result_detail_state);
        mZip = (TextView) view.findViewById(R.id.result_detail_zip);
        mType = (TextView) view.findViewById(R.id.result_detail_type);
        mRooms = (TextView) view.findViewById(R.id.result_detail_rooms);
        mBaths = (TextView) view.findViewById(R.id.result_detail_baths);
        mSqft = (TextView) view.findViewById(R.id.result_detail_sqft);
        mRent = (TextView) view.findViewById(R.id.result_detail_rent);
        mPhone = (TextView) view.findViewById(R.id.result_detail_phone);
        mEmail = (TextView) view.findViewById(R.id.result_detail_email);
        mDescription = (TextView) view.findViewById(R.id.result_detail_description);


        return view;
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

    // Helper to get user email from facebook profile
    private String findUserEmail() {

        SharedPreferences sharedPref = getContext().getSharedPreferences("FB_PROFILE", getContext().MODE_PRIVATE);
        Log.d("FB_NAME", sharedPref.getString("fbEmail", "NoEmail"));

        return sharedPref.getString("fbEmail", "NoEmail");
    }


    // Helper to search for posting via id
    private void searchForPosting(final String postId) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Set up http call
                String endPoint = "http://ec2-52-39-228-217.us-west-2.compute.amazonaws.com:3000/";
                // Remove space
                String cleanedPostId = postId.replaceAll(" ", "%20");
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

                        Log.d("SEARCH_RESULT", mGETResponse);

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

                // Search if the posting is a favoraite
                searchForFavoriteStatus(postId);

            }

        }.execute();
    }

    // Helper to set value to TextView
    private void setupViewItems() {

        if (mFavorite != null) {
            if (mFavorite.getFavoriteStatus().equals("true")) {
                mPhotoFav.setImageResource(R.drawable.favorite);
            } else {
                mPhotoFav.setImageResource(R.drawable.unfavorite);
            }
        }

        // Set a listener to the Favorite button
        mPhotoFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFavorite != null) {
                    if (mFavorite.getFavoriteStatus().equals("true")) {
                        mPhotoFav.setImageResource(R.drawable.unfavorite);
                        setFavoriteStatus(false);
                        mFavorite.setFavoriteStatus("false");
                    } else {
                        mPhotoFav.setImageResource(R.drawable.favorite);
                        setFavoriteStatus(true);
                        mFavorite.setFavoriteStatus("true");
                    }
                } else {
                    Toast.makeText(getActivity(), "Favorite Database Down", Toast.LENGTH_SHORT).show();;
                }
            }
        });

        mPostId.setText(mPosting.getPostId());
        mViewedCount.setText(Integer.toString(mPosting.getCounter()));
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
        mDescription.setText(mPosting.getDescription());
    }

    // Helper to set favorite status
    private void setFavoriteStatus (Boolean favStatus) {

        // Set up http call
        String endPoint = "http://ec2-52-39-228-217.us-west-2.compute.amazonaws.com:3000/favorites/";
        endPoint += mUserEmail;
        endPoint += "/";
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
                    if (mFavorite.getFavoriteStatus().equals("true")) {
                        HttpPut httpPut = new HttpPut(finalEndPoint);
                        httpResponse = httpClient.execute(httpPut);
                    } else {
                        HttpDelete httpDelete = new HttpDelete(finalEndPoint);
                        httpResponse = httpClient.execute(httpDelete);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("FAV_TOGGLE", "response code: " + (httpResponse.getStatusLine().getStatusCode()));

                return null;
            }


            @Override
            protected void onPostExecute(Void v) {
                Log.d("FAV_CALL", "http call completed");
            }

        }.execute();

    }

    // Helper to search for favorite status via postId
    private void searchForFavoriteStatus(final String postId) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Set up http call
                String endPoint = "http://ec2-52-39-228-217.us-west-2.compute.amazonaws.com:3000/isfavorite/";
                endPoint += mUserEmail;
                endPoint += "/";
                // Remove space
                String cleanedPostId = postId.replaceAll(" ", "%20");
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

                        Log.d("SEARCH_RESULT", mGETResponse);

                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                Log.d("SEARCH_DETAIL", "IN onPostExecute");

                if (!mGETResponse.equals("null\n") && !mGETResponse.equals("404 page not found\n")) {
                    // Setup mPostingList
                    Gson gson = new Gson();
                    mFavorite = gson.fromJson(mGETResponse, Favorite.class);

                    Log.d("POST_EDIT", mFavorite.toString());

                }

                // Set up View
                setupViewItems();

            }

        }.execute();
    }

    // Helper to set up the navi drawer
    private void setNavigationDrawer(View view) {
        // Initialize NavigationView
        mNavigationView = (NavigationView) view.findViewById(R.id.navigation_view_result_detail);
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
        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout_search_detail);
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
