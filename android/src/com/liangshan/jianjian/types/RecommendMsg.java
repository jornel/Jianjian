/**
 * 
 */
package com.liangshan.jianjian.types;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author ezhuche
 *
 */
public class RecommendMsg extends Fragment {
    
    //private String mMessageId;
    private Venue mVenue;
    private String mProductId;
    private String mProductName;
    private String mPrice;
    private String mDescription;
    private String mGeolat;
    private String mGeolong;
    private String[] mPhoto;
    private Boolean mIsPrivate;
    private int mNumComments;
    private String mParentId;
    
    public RecommendMsg(){
    }
    

    /* (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub

    }
    
    /*
    public String getMessageId() {
        return mMessageId;
    }

    public void setMessageId(String messageId) {
        mMessageId = messageId;
    }*/
    public Venue getVenue() {
        return mVenue;
    }
    public void setVenue(Venue venue) {
        mVenue = venue;
    }
    public String getProductId() {
        return mProductId;
    }
    public void setProductId(String productId) {
        mProductId = productId;
    }
    public String getProductName() {
        return mProductName;
    }
    public void setProductName(String productName) {
        mProductName = productName;
    }
    public String getPrice() {
        return mProductName;
    }
    public void setPrice(String price) {
        mPrice = price;
    }
    public String getDescription() {
        return mDescription;
    }
    public void setDescription(String description) {
        mDescription = description;
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
    public String[] getPhoto() {
        return mPhoto;
    }
    public void setPhoto(String[] photo) {
        mPhoto = photo;
    }
    
    public Boolean getIsPrivate() {
        return mIsPrivate;
    }
    public void setIsPrivate(String isprivate) {
        if(isprivate.equalsIgnoreCase("true")){
            mIsPrivate = true;
        }else {
            mIsPrivate = false;
        }
    }
    public int getNumComments() {
        return mNumComments;
    }
    public void setNumComments(String num_comments) {
        mNumComments = Integer.parseInt(num_comments);
    }
    public String getParentId() {
        return mParentId;
    }
    public void setParentId(String id) {
        mParentId = id;
    }
    
   


}
