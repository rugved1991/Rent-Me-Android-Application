package com.jojikubota.android.rentme.Controller;

import android.support.v4.app.Fragment;

/**
 * Created by joji on 4/24/16.
 */
public class EditPostActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new EditPostFragment();
    }
}
