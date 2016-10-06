package com.jojikubota.android.rentme.Controller;

import android.content.Intent;
import android.support.v4.app.Fragment;


public class SavedSearchesActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new SavedSearchesFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("in Activity.onAcitivyResult");
        super.onActivityResult(requestCode, resultCode, data);
    }
}
