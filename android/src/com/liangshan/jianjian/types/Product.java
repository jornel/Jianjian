/**
 * 
 */
package com.liangshan.jianjian.types;

import com.liangshan.jianjian.util.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author ezhuche
 *
 */
public class Product implements JianjianType,Parcelable {
    
    private String mProductId;
    private String mName;
    private String mParentId;
    private int mNumRecommended;
    private Category mCategory;
    private Venue mVenue;
    
    public Product(){
    }
    
    public Product(Parcel in){
        mProductId = ParcelUtils.readStringFromParcel(in);
        mName = ParcelUtils.readStringFromParcel(in);
        mParentId = ParcelUtils.readStringFromParcel(in);
        mNumRecommended = in.readInt();
        if (in.readInt() == 1) {
            mCategory = in.readParcelable(Category.class.getClassLoader());
        }
        if (in.readInt() == 1) {
            mVenue = in.readParcelable(Venue.class.getClassLoader());
        }
    }
    
    public static final Product.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    /* (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        ParcelUtils.writeStringToParcel(out, mProductId);
        ParcelUtils.writeStringToParcel(out, mName);
        ParcelUtils.writeStringToParcel(out, mParentId);
        out.writeInt(mNumRecommended);
        if(mCategory != null){
            out.writeInt(1);
            out.writeParcelable(mCategory, flags);
        } else {
            out.writeInt(0);
        }
        if(mVenue != null){
            out.writeInt(1);
            out.writeParcelable(mVenue, flags);
        } else {
            out.writeInt(0);
        }

    }
    
    public String getProductId() {
        return mProductId;
    }

    public void setProductId(String id) {
        mProductId = id;
    }

    public Category getCategory() {
        return mCategory;
    }
    public void setCategory(Category c) {
        mCategory = c;
    }
    
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
    
    
    public Venue getVenue() {
        return mVenue;
    }
    public void setVenue(Venue venue) {
        mVenue = venue;
    }
    public String getParentId() {
        return mParentId;
    }
    public void setParentId(String id) {
        mParentId = id;
    }
    public int getNumComments() {
        return mNumRecommended;
    }
    public void setNumComments(String num) {
        mNumRecommended = Integer.parseInt(num);
    }
   
}
