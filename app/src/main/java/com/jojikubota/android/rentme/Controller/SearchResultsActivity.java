
package com.jojikubota.android.rentme.Controller;

import android.support.v4.app.Fragment;

/**
 * Created by joji on 4/24/16.
 */
public class SearchResultsActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new SearchResultsFragment();
    }

    public String getSearchQuery(){

        StringBuilder searchQuery=new StringBuilder();

        String keyword=(getIntent().getStringExtra("keyword")).replaceAll(" ","%20");
        String city=(getIntent().getStringExtra("city")).replaceAll(" ","%20");
        String type=(getIntent().getStringExtra("type")).replaceAll(" ","%20");
        String rent=(getIntent().getStringExtra("rent")).replaceAll(" ","%20");




        if(keyword.equals("") && city.equals("")  && type.equals("") && rent.equals("")){

            return searchQuery.append("/foo/san%20jose/foo/foo").toString();
        }
        else {

            if (keyword.equals("")) {
                searchQuery.append("/foo");
            } else {
                searchQuery.append("/" + keyword);
            }
            if (city.equals("")) {

                searchQuery.append("/san%20jose");
            }
            else {
                searchQuery.append("/" + city);
            }
            if (type.equals("")) {
                searchQuery.append("/foo");
            } else {
                searchQuery.append("/" + type);
            }
            if (rent.equals("")) {
                searchQuery.append("/foo");
            } else {
                searchQuery.append("/" + rent);
            }
        }
        return searchQuery.toString();

    }
}
