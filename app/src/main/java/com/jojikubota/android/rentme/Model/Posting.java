package com.jojikubota.android.rentme.Model;

import android.graphics.Bitmap;

/**
 * Created by joji on 4/18/16.
 */
public class Posting {

    private String mPostId;
    private String mStreet;
    private String mCity;
    private String mState;
    private String mZip;
    private String mType;
    private String mRooms;
    private String mBaths;
    private String mSqft;
    private String mRent;
    private String mPhone;
    private String mEmail;
    private String mDescription;
//    private Bitmap mPhoto;
    private String mStatus;
    private int mCounter;

    // For mPostId
    public String getPostId() {
        return mPostId;
    }

    public void setPostId(String postId) {
        mPostId = postId;
    }

    // For mStreet
    public String getStreet() {
        return mStreet;
    }

    public void setStreet(String street) {
        mStreet = street;
    }

    // For mCity
    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    // For mState
    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    // For mZip
    public String getZip() {
        return mZip;
    }

    public void setZip(String zip) {
        mZip = zip;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    // for mRooms
    public String getRooms() {
        return mRooms;
    }

    public void setRooms(String rooms) {
        mRooms = rooms;
    }

    // For mBaths
    public String getBaths() {
        return mBaths;
    }

    public void setBaths(String baths) {
        mBaths = baths;
    }

    // For mSqft
    public String getSqft() {
        return mSqft;
    }

    public void setSqft(String sqft) {
        mSqft = sqft;
    }

    // For mRent
    public String getRent() {
        return mRent;
    }

    public void setRent(String rent) {
        mRent = rent;
    }

    // For mPhone
    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    // For mEmail
    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    // For mDescription
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

//    // For mImage
//    public Bitmap getPhoto() {
//        return mPhoto;
//    }
//
//    public void setPhoto(Bitmap photo) {
//        mPhoto = photo;
//    }

    // For mStatus
    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    // For mCounter
    public int getCounter() {
        return mCounter;
    }

    public void setCounter(int counter) {
        mCounter = counter;
    }

}
