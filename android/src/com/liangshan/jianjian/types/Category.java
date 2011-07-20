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
public class Category implements JianjianType,Parcelable {
    
    private String mCategoryId;
    private String mName;
    
    public Category(){
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
