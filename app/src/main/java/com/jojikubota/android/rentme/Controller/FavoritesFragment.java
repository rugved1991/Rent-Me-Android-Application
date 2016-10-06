package com.jojikubota.android.rentme.Controller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.jojikubota.android.rentme.Model.Posting;
import com.jojikubota.android.rentme.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by joji on 4/24/16.
 */
public class FavoritesFragment extends Fragment {


    private RecyclerView mPostingRecyclerView;
    private PostingAdapter mAdapter;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private String mGETResponse;
    //    private PostingList mPostingList;
    private List<Posting> mPostingList;

    // From facebook
    private String mUserFirstName;
    private String mUserLastName;
    private String mUserEmail;
    private String mProfilePic;



    // View holder
    private class PostingHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Posting mPosting;
        private ImageView mPhoto;
        private TextView mPostingId;
        private TextView mDollar;
        private TextView mRent;
        private TextView mRooms;
        private TextView mBaths;
        private TextView mAddress;
        private TextView mCity;
        private TextView mState;
        private TextView mZip;
        private TextView mLeftParan;
        private TextView mStrRooms;
        private TextView mComma;
        private TextView mStrBaths;
        private TextView mRightParan;

        public PostingHolder(View itemView)  {
            super(itemView);
            // Create reference
            mPhoto = (ImageView) itemView
                    .findViewById(R.id.list_post_image);
            mPostingId = (TextView) itemView
                    .findViewById(R.id.list_post_id);
            mDollar = (TextView) itemView
                    .findViewById(R.id.list_post_dollar);
            mRent = (TextView) itemView
                    .findViewById(R.id.list_post_rent);
            mRooms = (TextView) itemView
                    .findViewById(R.id.list_post_rooms);
            mBaths = (TextView) itemView
                    .findViewById(R.id.list_post_baths);
            mAddress = (TextView) itemView
                    .findViewById(R.id.list_post_address);
            mCity = (TextView) itemView
                    .findViewById(R.id.list_post_city);
            mState = (TextView) itemView
                    .findViewById(R.id.list_post_state);
            mZip = (TextView) itemView
                    .findViewById(R.id.list_post_zip);
            mLeftParan = (TextView) itemView
                    .findViewById(R.id.list_post_left_paran);
            mStrRooms = (TextView) itemView
                    .findViewById(R.id.list_post_str_rooms);
            mComma = (TextView) itemView
                    .findViewById(R.id.list_post_comma);
            mStrBaths = (TextView) itemView
                    .findViewById(R.id.list_post_str_baths);
            mRightParan = (TextView) itemView
                    .findViewById(R.id.list_post_right_param);

            // Set up the singleton (data container)
//            mPostingList = PostingList.get(getContext());


            // Listener
            itemView.setOnClickListener(this);
        }

        // Click Handler for the list element
        @Override
        public void onClick(View v) {

            // Call UpdatePostDetailActivity
//            Intent intent = UpdatePostDetailActivity.newIntent(getActivity(), mPosting.getPostId());
            Intent intent = SearchResultDetailActivity.newIntent(getActivity(), mPosting.getPostId());

            startActivity(intent);
        }

        public void bindPosting(Posting posting) {
            mPosting = posting;
            mPostingId.setText(mPosting.getPostId());
            mDollar.setText("$");
            mRent.setText(mPosting.getRent());
            mRooms.setText(mPosting.getRooms());
            mBaths.setText(mPosting.getBaths());
            mAddress.setText(mPosting.getStreet());
            mCity.setText(mPosting.getCity());
            mState.setText(mPosting.getState());
            mZip.setText(mPosting.getZip());
            mLeftParan.setText("(");
            mStrRooms.setText("Room:");
            mComma.setText(", ");
            mStrBaths.setText("Bath:");
            mRightParan.setText(")");
        }
    }

    // Adapter
    private class PostingAdapter extends RecyclerView.Adapter<PostingHolder> {

        private List<Posting> mPostings;

        public PostingAdapter(List<Posting> postings) {
            mPostings = postings;
        }

        @Override
        public PostingHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_post_tenant, parent, false);
            return new PostingHolder(view);
        }

        @Override
        public void onBindViewHolder(PostingHolder holder, int position) {
            Posting posting = mPostings.get(position);
            holder.bindPosting(posting);
        }

        @Override
        public int getItemCount() {
            return mPostings.size();
        }
    }

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
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        mPostingRecyclerView = (RecyclerView) view
                .findViewById(R.id.favorites_recycle_view);
        mPostingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Add empty adaptor
        List<Posting> postings = new ArrayList<Posting>();
        mAdapter = new PostingAdapter(postings);
        mPostingRecyclerView.setAdapter(mAdapter);

        // Retrieve facebook profile
        getFacebookProfile();

        // Setup navigation drawer
        setNavigationDrawer(view);

        // Pull postings from db
        // Get posting for the user



        getPostingFromDb();


        return view;

    }


    // Update List view / recycle view
    private void updateUI() {
//        PostingList postingList = PostingList.get(getActivity());
//        List<Posting> postings = postingList.getPostings();
        List<Posting> postings = mPostingList;

        mAdapter = new PostingAdapter(postings);
        mPostingRecyclerView.setAdapter(mAdapter);
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

    // Helper to GET the postings from the db
    private void getPostingFromDb() {
        System.out.println("I am inside getPostingDb");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Set up http call
                String endPoint = "http://ec2-52-39-228-217.us-west-2.compute.amazonaws.com:3000/favorites/";

                endPoint += mUserEmail;

                Log.d("endpoint",endPoint);
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

                        if (mGETResponse == "") {
                            System.out.println("Say Hi");
                        }

                        Log.d("EDIT_POST", mGETResponse);

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

                if (!mGETResponse.equals("null\n")) {
                    // Setup mPostingList
                    Gson gson = new Gson();
                    Posting postings[] = gson.fromJson(mGETResponse, Posting[].class);
                    Log.d("POST_EDIT", postings[0].getEmail());

                    mPostingList = Arrays.asList(postings);
                    Log.d("POST_EDIT", mPostingList.toString());

                    // Update UI
                    updateUI();
                }
                else {
                    System.out.println("Response is null");
                    Toast.makeText(getContext(), "Results Unavailable!", Toast.LENGTH_LONG);
                }
            }

        }.execute();

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

    // Helper to set up the navi drawer
    private void setNavigationDrawer(View view) {
        // Initialize NavigationView
        mNavigationView = (NavigationView) view.findViewById(R.id.navigation_view_favorites);
        Log.d("In set navigationdrawer",mNavigationView.toString());
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
        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout_favorites);
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

        // Add Toggle switch for the navigation drawer
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_navi_drawer);

    }


}
