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
public class Product implements JianjianType,Parcelable {
    
    private String mProductId;
    private String mName;
    private Category mCategory;
    private String mPrice;
    
    public Product(){
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
    
    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }
   
}
