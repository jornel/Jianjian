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
public class FriendInvitation implements JianjianType,Parcelable {
    
    
    private String mInvitationId;
    private String mDate;
    private User mFromUser;
    

    
    public FriendInvitation(){
        mFromUser = new User();
    }
    
    public FriendInvitation(Parcel in) {
        mInvitationId = ParcelUtils.readStringFromParcel(in);
        mDate = ParcelUtils.readStringFromParcel(in);
                
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
        ParcelUtils.writeStringToParcel(out, mInvitationId);
        ParcelUtils.writeStringToParcel(out, mDate);
        if (mFromUser != null) {
            out.writeInt(1);
            out.writeParcelable(mFromUser, flags);
        } else {
            out.writeInt(0);
        }

    }
    
    public String getInvitationId() {
        return mInvitationId;
    }
    public void setInvitationId(String id) {
        mInvitationId = id;
    }
    
    public String getDate() {
        return mDate;
    }
    public void setDate(String date) {
        mDate = date;
    }
    
    public User getFromUser() {
        return mFromUser;
    }
    public void setFromUser(User user) {
        mFromUser = user;
    }   
   


}
