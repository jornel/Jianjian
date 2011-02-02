/**
 * 
 */
package com.liangshan.jianjian.android;


import com.liangshan.jianjian.types.User;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * @author ezhuche
 *
 */
public class UserDetailsActivity extends Activity {
    
    static final String TAG = "UserDetailsActivity";
    static final boolean DEBUG = JianjianSettings.DEBUG;
    
    public static final String EXTRA_USER_ID = "com.liangshan.jianjian.android.UserDetailsActivity.EXTRA_USER_ID";
    public static final int LOAD_TYPE_USER_NONE = 0;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.user_details_activity);
        
        Object retained = getLastNonConfigurationInstance();
        
        
    }
    
    private static class StateHolder {
        public static final int TASK_FRIEND_ACCEPT = 0;
        public static final int TASK_FRIEND_ADD    = 1;
        private User mUser;
        private boolean mIsLoggedInUser;
        //private UserDetailsTask mTaskUserDetails;
        //private boolean mIsRunningUserDetailsTask;
        private boolean mRanOnce;
        private int mLoadType;
        
        //private FriendTask mTaskFriend;
        //private boolean mIsRunningFriendTask;
        
        public StateHolder() {
            //mIsRunningUserDetailsTask = false;
            //mIsRunningFriendTask = false;
            mIsLoggedInUser = false;
            mRanOnce = false;
            mLoadType = LOAD_TYPE_USER_NONE;
        }
        
        public boolean getIsLoggedInUser() {
            return mIsLoggedInUser;
        }
        
        public void setIsLoggedInUser(boolean isLoggedInUser) {
            mIsLoggedInUser = isLoggedInUser;
        }

        public User getUser() {
            return mUser;
        }

        public void setUser(User user) {
            mUser = user;
        }
        
        public int getLoadType() {
            return mLoadType;
        }
        
        public void setLoadType(int loadType) {
            mLoadType = loadType;
        }
        
        public boolean getRanOnce() {
            return mRanOnce;
        }
        
        public void setRanOnce(boolean ranOnce) {
            mRanOnce = ranOnce;
        }
        
    }

}
