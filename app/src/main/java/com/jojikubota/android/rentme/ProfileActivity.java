package com.jojikubota.android.rentme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

/**
 * Created by rugve_000 on 5/6/2016.
 */
public class ProfileActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        Bundle iBundle=getIntent().getExtras();
        String name=iBundle.get("name").toString();
        String surname=iBundle.get("surname").toString();
        String imageUrl=iBundle.get("imageURL").toString();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_login,menu);
        return true;
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        if(id==R.id.action_settings){
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logout(){
    LoginManager.getInstance().logOut();
    Intent login = new Intent(ProfileActivity.this, AuthenticationActivity.class);
    startActivity(login);
    finish();
    }

    */

}
