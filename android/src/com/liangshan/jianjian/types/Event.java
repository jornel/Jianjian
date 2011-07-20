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
public class Event implements JianjianType,Parcelable {
    
    private String mEventId;
    private String mCreateDate;
    private String mType;
    private Fragment mFragment;
    
    public Event(){
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
    
    public String getEventId() {
        return mEventId;
    }

    public void setEventId(String id) {
        mEventId = id;
    }
    public String getCreateDate() {
        return mCreateDate;
    }
    public void setCreateDate(String date) {
        mCreateDate = date;
    }
    public String getType() {
        return mType;
    }
    public void setType(String type) {
        mType = type;
    }
    
    public Fragment getFragment() {
        return mFragment;
    }

    public void setFragment(Fragment element) {
        mFragment = element;
    }
   
}
