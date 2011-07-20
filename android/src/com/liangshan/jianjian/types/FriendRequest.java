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
public class FriendRequest extends Fragment {
    
    //private String mMessageId;
    private String mDate;

    
    public FriendRequest(){
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
    public String getDate() {
        return mDate;
    }
    public void setDate(String date) {
        mDate = date;
    }

    
   


}
