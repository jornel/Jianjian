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
public class Fragment implements JianjianType,Parcelable {
    
    private String mFragmentId;
    private String mType;
    private User mFromUser;
    private String mCreateDate;
    
    public Fragment(){
    }
    
    public Fragment(Parcel in) {
        mFragmentId = ParcelUtils.readStringFromParcel(in);
        mType = ParcelUtils.readStringFromParcel(in);
        mCreateDate = ParcelUtils.readStringFromParcel(in);
                
        if (in.readInt() == 1) {
            mFromUser = in.readParcelable(User.class.getClassLoader()) ;
        }
        
    }
    

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
        ParcelUtils.writeStringToParcel(out, mFragmentId);
        ParcelUtils.writeStringToParcel(out, mType);
        ParcelUtils.writeStringToParcel(out, mCreateDate);
        if (mFromUser != null) {
            out.writeInt(1);
            out.writeParcelable(mFromUser, flags);
        } else {
            out.writeInt(0);
        }

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
