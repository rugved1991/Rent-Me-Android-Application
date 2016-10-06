package com.jojikubota.android.rentme.Model;

/**
 * Created by joji on 5/16/16.
 */
public class Favorite {

    private String mEmail;
    private String isFav;

    // For mFavoriteStatus
    public String getFavoriteStatus() {
        return isFav;
    }

    public void setFavoriteStatus(String fav) {
        isFav = fav;
    }

    // For mEmail
    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }


}
