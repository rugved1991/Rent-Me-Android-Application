package com.jojikubota.android.rentme.Model;

/**
 * Created by rugve_000 on 5/16/2016.
 */
public class SavedSearch {

    private String mKeyword;
    private String mCity;
    private String mType;
    private String mRent;


    public String getKeyword() {
        return mKeyword;
    }

    public void setKeyword(String mKeyword) {
        this.mKeyword = mKeyword;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String mCity) {
        this.mCity = mCity;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public String getRent() {
        return mRent;
    }

    public void setRent(String mRent) {
        this.mRent = mRent;
    }
}
