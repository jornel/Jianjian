/**
 * 
 */
package com.liangshan.jianjian.types;

import java.util.ArrayList;
import com.liangshan.jianjian.util.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author jornel
 *
 */
public class User implements Parcelable, JianjianType {
    
    
    private int mFriendCount;
    private int mRecMsgCount;
    private int mBadgesCount;
    private int mPoints;
    private String mUserid;
    private String mEmail;
    private String mMsisdn;        
    private String mGender;
    private String mPhoto;
    private String mUsername;
    private String mNick;
    private String mCity;
    private String mFriendstatus;
    private RecommendMsg mLastRecMsg;
    private ArrayList<User> mFriendsInCommon;

    
    
    public User() {
    }

    /**
     * @param in
     */
    public User(Parcel in) {
        mFriendCount = in.readInt();
        mRecMsgCount = in.readInt();
        mBadgesCount = in.readInt();
        mPoints = in.readInt();
        mUserid = ParcelUtils.readStringFromParcel(in);
        mEmail = ParcelUtils.readStringFromParcel(in);
        mMsisdn = ParcelUtils.readStringFromParcel(in);
        mGender = ParcelUtils.readStringFromParcel(in);
        mPhoto = ParcelUtils.readStringFromParcel(in);
        mUsername = ParcelUtils.readStringFromParcel(in);
        mNick = ParcelUtils.readStringFromParcel(in);
        mCity = ParcelUtils.readStringFromParcel(in);
        mFriendstatus = ParcelUtils.readStringFromParcel(in);

        
        if (in.readInt() == 1) {
            mLastRecMsg = in.readParcelable(RecommendMsg.class.getClassLoader());
        }
        mFriendsInCommon = new ArrayList<User>();
        for (int i = 0; i < mFriendCount; i++) {
            User user = in.readParcelable(User.class.getClassLoader());
            mFriendsInCommon.add(user);
        }
        
    }
    
    public static final User.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mFriendCount);
        out.writeInt(mRecMsgCount);
        out.writeInt(mBadgesCount);
        out.writeInt(mPoints);
        ParcelUtils.writeStringToParcel(out, mUserid);
        ParcelUtils.writeStringToParcel(out, mEmail);
        ParcelUtils.writeStringToParcel(out, mMsisdn);
        ParcelUtils.writeStringToParcel(out, mGender);
        ParcelUtils.writeStringToParcel(out, mPhoto);
        ParcelUtils.writeStringToParcel(out, mUsername);
        ParcelUtils.writeStringToParcel(out, mNick);
        ParcelUtils.writeStringToParcel(out, mCity);
        ParcelUtils.writeStringToParcel(out, mFriendstatus);
        
        if (mLastRecMsg != null) {
            out.writeInt(1);
            out.writeParcelable(mLastRecMsg, flags);
        } else {
            out.writeInt(0);
        }
        
        if (mFriendsInCommon != null) {
            out.writeInt(mFriendsInCommon.size());
            for (int i = 0; i < mFriendsInCommon.size(); i++) {
                out.writeParcelable(mFriendsInCommon.get(i), flags);
            }
        } else {
            out.writeInt(0);
        }              
    }
    
    public int getFriendCount() {
        return mFriendCount;
    }
    
    public void setFriendCount(int friendCount) {
        mFriendCount = friendCount;
    }
    
    public int getRecMsgCount() {
        return mRecMsgCount;
    }
    
    public void setRecMsgCount(int recMsgCount) {
        mRecMsgCount = recMsgCount;
    }
    
    public int getBadgesCount() {
        return mBadgesCount;
    }
    
    public void setBadgesCount(int badgesCount) {
        mBadgesCount = badgesCount;
    }
    
    public int getPoints() {
        return mPoints;
    }
    
    public void setPoints(int points) {
        mPoints = points;
    }
    
    public String getUserid() {
        return mUserid;
    }
    
    public void setUserid(String userid) {
        mUserid = userid;
    }
    
    public String getEmail() {
        return mEmail;
    }
    
    public void setEmail(String email) {
        mEmail = email;
    }
    
    public String getMsisdn() {
        return mMsisdn;
    }
    
    public void setMsisdn(String msisdn) {
        mMsisdn = msisdn;
    }
    
    public String getGender() {
        return mGender;
    }
    
    public void setGender(String gender) {
        mGender = gender;
    }
    
    public String getPhoto() {
        return mPhoto;
    }
    
    public void setPhoto(String photo) {
        mPhoto = photo;
    }
    
    public String getUsername() {
        return mUsername;
    }
    
    public void setUsername(String username) {
        mUsername = username;
    }
    
    public String getNick() {
        return mNick;
    }
    
    public void setNick(String nick) {
        mNick = nick;
    }
    
    public String getCity() {
        return mCity;
    }
    
    public void setCity(String city) {
        mCity = city;
    }
    
    public String getFriendstatus() {
        return mFriendstatus;
    }

    public void setFriendstatus(String friendstatus) {
        mFriendstatus = friendstatus;
    }
    
    public RecommendMsg getLastRecMsg() {
        return mLastRecMsg;
    }
    
    public void setLastRecMsg(RecommendMsg lastRecMsg) {
        mLastRecMsg = lastRecMsg;
    }
    
    public ArrayList<User> getFriendsInCommon() {
        return mFriendsInCommon;
    }
    
    public void setFriendsInCommon(ArrayList<User> friends) {
        mFriendsInCommon = friends;
    }

}
