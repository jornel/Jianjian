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
public class Category implements JianjianType,Parcelable {
    
    private String mCategoryId;
    private String mName;
    
    public Category(){
    }
    public Category(Parcel in){
        mCategoryId = ParcelUtils.readStringFromParcel(in);
        mName = ParcelUtils.readStringFromParcel(in);
    }
    
    public static final Category.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
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
        ParcelUtils.writeStringToParcel(out, mCategoryId);
        ParcelUtils.writeStringToParcel(out, mName);

    }
    
    public String getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(String id) {
        mCategoryId = id;
    }
    
    public String getName() {
        return mName;
    }

    public void setFromUser(String name) {
        mName = name;
    }
   
}
