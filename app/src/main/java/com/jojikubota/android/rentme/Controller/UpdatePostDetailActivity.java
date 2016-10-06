package com.jojikubota.android.rentme.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by joji on 5/1/16.
 */
public class UpdatePostDetailActivity extends SingleFragmentActivity {

    private static final String EXTRA_POST_ID =
            "com.jojikubota.android.rentme.post_id";

    public static Intent newIntent(Context packageContext, String postId) {
        Intent intent = new Intent(packageContext, UpdatePostDetailActivity.class);
        intent.putExtra(EXTRA_POST_ID, postId);

        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String postId = (String) getIntent().getCharSequenceExtra(EXTRA_POST_ID);
        return UpdatePostDetailFragment.newInstantce(postId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("in Activity.onAcitivyResult");
        super.onActivityResult(requestCode, resultCode, data);
    }


}
