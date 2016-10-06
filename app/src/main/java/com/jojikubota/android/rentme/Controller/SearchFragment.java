
package com.jojikubota.android.rentme.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.facebook.login.LoginManager;
import com.jojikubota.android.rentme.AuthenticationActivity;
import com.jojikubota.android.rentme.Model.PostingList;
import com.jojikubota.android.rentme.R;


/**
 * Created by joji on 4/17/16.
 */

public class SearchFragment extends Fragment {


    private EditText mkeyword;
    private EditText mCity;
    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;

    private EditText mType;
    private EditText mRent;
    private ImageButton mSearchButton;
    private ImageButton mSaveSearchButton;
    private ImageButton mResetButton;
    private PostingList mPostingList;

    private Uri mUri;
    private static final int REQUEST_CHOOSER = 1000;
    private static final String STATE_URI = "state_uri";

    // For navigation drawer
    private ListView mDrawerList;
    //Nikhil: titles for navigation drawer
    private String[] mTitles;
    //Nikhil: icons for navigation drawer
    private int[] mIcons;
    private ArrayAdapter<String> mArrayAdapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;


    //Nikhil: RecyclerViews
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;

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
        View view = inflater.inflate(R.layout.activity_tenant_search, container, false);


        // Create references to the input texts
        mkeyword = (EditText) view.findViewById(R.id.keyword);
        mCity = (EditText) view.findViewById(R.id.city);
        mType = (EditText) view.findViewById(R.id.type);
        mRent = (EditText) view.findViewById(R.id.rent);
        mSearchButton = (ImageButton) view.findViewById(R.id.search_button);
        mSaveSearchButton=(ImageButton) view.findViewById(R.id.save_search_button);
        mResetButton=(ImageButton) view.findViewById(R.id.reset_button);


        mSaveSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("Save Search BUTTON TAPPED");

                Intent intent = new Intent(getActivity(), SavedSearchesActivity.class);
                intent.putExtra("keyword", mkeyword.getText().toString());
                intent.putExtra("city", mCity.getText().toString());
                intent.putExtra("type", mType.getText().toString());
                intent.putExtra("rent", mRent.getText().toString());

                startActivity(intent);

                // Dismiss the keyboard
                InputMethodManager imm = (InputMethodManager) (getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                mkeyword.setText("");
                mCity.setText("");
                mType.setText("");
                mRent.setText("");

            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("Search BUTTON TAPPED");

                Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
                intent.putExtra("keyword", mkeyword.getText().toString());
                intent.putExtra("city", mCity.getText().toString());
                intent.putExtra("type", mType.getText().toString());
                intent.putExtra("rent", mRent.getText().toString());

                startActivity(intent);


                // Dismiss the keyboard
                InputMethodManager imm = (InputMethodManager) (getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                mkeyword.setText("");
                mCity.setText("");
                mType.setText("");
                mRent.setText("");

            }
        });

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("Reset BUTTON TAPPED");

                // Dismiss the keyboard
                InputMethodManager imm = (InputMethodManager) (getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                mkeyword.setText("");
                mCity.setText("");
                mType.setText("");
                mRent.setText("");

            }
        });

        setNavigationDrawer(view);

        return view;
    }

    // Handle results from the camera or image gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println("in Fragment.onActivityResult");

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            // canceled
            return;
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


    // Helper to set up the navi drawer
    private void setNavigationDrawer(View view) {
        // Initialize NavigationView
        mNavigationView = (NavigationView) view.findViewById(R.id.navigation_view_search);
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
                        intent = new Intent(getActivity(), SearchActivity.class);
                        break;
                    case R.id.favorite:
                        intent = new Intent(getActivity(), FavoritesActivity.class);
                        break;
                    case R.id.saved_searches:
                        intent = new Intent(getActivity(), SavedSearchesActivity.class);
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
        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout_search);
        mDrawerToggle =
                new ActionBarDrawerToggle(getActivity(), mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
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
}