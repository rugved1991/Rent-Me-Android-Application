package com.jojikubota.android.rentme.Controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by joji on 5/1/16.
 */
public class SearchResultDetailActivity extends SingleFragmentActivity {

    private static final String EXTRA_RESULT_ID =
            "com.jojikubota.android.rentme.result_id";

    public static Intent newIntent(Context packageContext, String resultId) {
        Intent intent = new Intent(packageContext, SearchResultDetailActivity.class);
        intent.putExtra(EXTRA_RESULT_ID, resultId);

        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String resultId = (String) getIntent().getCharSequenceExtra(EXTRA_RESULT_ID);
        return SearchResultDetailFragment.newInstance(resultId);
    }

}
