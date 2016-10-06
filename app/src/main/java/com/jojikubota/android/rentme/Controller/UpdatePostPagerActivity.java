package com.jojikubota.android.rentme.Controller;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.jojikubota.android.rentme.Model.Posting;
import com.jojikubota.android.rentme.Model.PostingList;
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
import java.util.Arrays;
import java.util.List;

/**
 * Created by joji on 5/1/16.
 */
public class UpdatePostPagerActivity extends AppCompatActivity {

    // Variables
    private static final String EXTRA_POST_ID =
            "com.jojikubota.android.rentme.post_id";

    private ViewPager mViewPager;
    private List<Posting> mPostings;
    private String mGETResponse;

    // Custom Initializer
    public static Intent newIntent(Context packageContext, String postId) {
        Intent intent = new Intent(packageContext, UpdatePostPagerActivity.class);
        intent.putExtra(EXTRA_POST_ID, postId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_post_update);

        // Retrieve Extra
        final String postId = (String) getIntent()
                .getCharSequenceExtra(EXTRA_POST_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_pager_post_update);


        // Pull postings from db
        // Get posting for the user
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Set up http call
                String mEmail = "joji.kubota@sjsu.edu";
                String endPoint = "http://ec2-52-39-228-217.us-west-2.compute.amazonaws.com:3000/rent";
                endPoint += "/";
                endPoint += mEmail;

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
                // Setup mPostingList
                Gson gson = new Gson();
                Posting postings[] = gson.fromJson(mGETResponse, Posting[].class);
                Log.d("POST_EDIT", postings[0].getEmail());

                mPostings = Arrays.asList(postings);
                Log.d("POST_EDIT", mPostings.toString());


                //        mPostings = PostingList.get(this).getPostings();
                FragmentManager fragmentManager = getSupportFragmentManager();
                mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
                    @Override
                    public Fragment getItem(int position) {

                        Posting posting = mPostings.get(position);
                        return UpdatePostDetailFragment.newInstantce(posting.getPostId());
                    }

                    @Override
                    public int getCount() {
                        return mPostings.size();
                    }
                });

                for (int i = 0; i < mPostings.size(); i++) {
                    if (mPostings.get(i).getPostId().equals(postId)) {
                        mViewPager.setCurrentItem(i);
                        break;
                    }
                }

            }

        }.execute();

    }

}
