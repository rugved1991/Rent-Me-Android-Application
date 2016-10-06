
package com.jojikubota.android.rentme.Controller;



import android.content.Intent;
import android.support.v4.app.Fragment;





public class SearchActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return new SearchFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("in Activity.onAcitivyResult");
        super.onActivityResult(requestCode, resultCode, data);
    }

}
