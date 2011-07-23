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
public class Fragment implements JianjianType,Parcelable {
    
    private String mFragmentId;
    private String mType;
    private User mFromUser;
    private String mCreateDate;
    
    public Fragment(){
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
    
    public String getFragmentId() {
        return mFragmentId;
    }

    public void setFragmentId(String id) {
        mFragmentId = id;
    }

    public String getType() {
        return mType;
    }
    public void setType(String type) {
        mType = type;
    }
    
    public User getFromUser() {
        return mFromUser;
    }

    public void setFromUser(User user) {
        mFromUser = user;
    }
    public String getCreateDate() {
        return mCreateDate;
    }
    public void setCreateDate(String date) {
        mCreateDate = date;
    }
   
}
