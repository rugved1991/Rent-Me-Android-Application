package com.jojikubota.android.rentme.Controller;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;


public class NewPostActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new NewPostFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("in Activity.onAcitivyResult");
        super.onActivityResult(requestCode, resultCode, data);
    }

}
