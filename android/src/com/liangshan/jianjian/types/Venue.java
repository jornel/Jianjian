/**
 * Copyright 2011
 */

package com.liangshan.jianjian.types;

import com.liangshan.jianjian.util.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 *
 * @author Joe Chen
 * 
 */
public class Venue implements JianjianType, Parcelable {

    private String mAddress;
    private String mCity;
    private String mCityid;
    private String mCrossstreet;
    private String mDistance;
    private String mGeolat;
    private String mGeolong;
    private boolean mHasTodo;
    private String mId;
    private String mName;
    private String mPhone;
    private String mState;


    public Venue() {
    }

    private Venue(Parcel in) {
        mAddress = ParcelUtils.readStringFromParcel(in);
        mCity = ParcelUtils.readStringFromParcel(in);
        mCityid = ParcelUtils.readStringFromParcel(in);
        mCrossstreet = ParcelUtils.readStringFromParcel(in);
        mDistance = ParcelUtils.readStringFromParcel(in);
        mGeolat = ParcelUtils.readStringFromParcel(in);
        mGeolong = ParcelUtils.readStringFromParcel(in);
        mHasTodo = in.readInt() == 1;
        mId = ParcelUtils.readStringFromParcel(in);
        mName = ParcelUtils.readStringFromParcel(in);
        mPhone = ParcelUtils.readStringFromParcel(in);
        mState = ParcelUtils.readStringFromParcel(in);
    }
    
    public static final Parcelable.Creator<Venue> CREATOR = new Parcelable.Creator<Venue>() {
        public Venue createFromParcel(Parcel in) {
            return new Venue(in);
        }

        @Override
        public Venue[] newArray(int size) {
            return new Venue[size];
        }
    };
      
    
    @Override
    public void writeToParcel(Parcel out, int flags) {
        ParcelUtils.writeStringToParcel(out, mAddress);
        ParcelUtils.writeStringToParcel(out, mCity);
        ParcelUtils.writeStringToParcel(out, mCityid);
        ParcelUtils.writeStringToParcel(out, mCrossstreet);
        ParcelUtils.writeStringToParcel(out, mDistance);
        ParcelUtils.writeStringToParcel(out, mGeolat);
        ParcelUtils.writeStringToParcel(out, mGeolong);
        out.writeInt(mHasTodo ? 1 : 0);
        ParcelUtils.writeStringToParcel(out, mId);
        ParcelUtils.writeStringToParcel(out, mName);
        ParcelUtils.writeStringToParcel(out, mPhone);
        ParcelUtils.writeStringToParcel(out, mState);
      
    }

    @Override
    public int describeContents() {
        return 0;
    }
    
    
    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getCityid() {
        return mCityid;
    }

    public void setCityid(String cityid) {
        mCityid = cityid;
    }

    public String getCrossstreet() {
        return mCrossstreet;
    }

    public void setCrossstreet(String crossstreet) {
        mCrossstreet = crossstreet;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    public String getGeolat() {
        return mGeolat;
    }

    public void setGeolat(String geolat) {
        mGeolat = geolat;
    }

    public String getGeolong() {
        return mGeolong;
    }

    public void setGeolong(String geolong) {
        mGeolong = geolong;
    }

    public boolean getHasTodo() {
    	return mHasTodo;
    }
    
    public void setHasTodo(boolean hasTodo) {
    	mHasTodo = hasTodo;
    }
    
    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }
    
    
    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }


}