/**
 * 
 */
package com.liangshan.jianjian.android;


import com.liangshan.jianjian.android.util.StringFormatters;
import com.liangshan.jianjian.android.util.UserUtils;
import com.liangshan.jianjian.types.User;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author ezhuche
 *
 */
public class UserDetailsActivity extends Activity {
    
    static final String TAG = "UserDetailsActivity";
    static final boolean DEBUG = JianjianSettings.DEBUG;
    
    public static final String EXTRA_USER_ID = "com.liangshan.jianjian.android.UserDetailsActivity.EXTRA_USER_ID";
    private static final int LOAD_TYPE_USER_NONE    = 0;
    private static final int LOAD_TYPE_USER_ID      = 1;
    private static final int LOAD_TYPE_USER_PARTIAL = 2;
    private static final int LOAD_TYPE_USER_FULL    = 3;
    private StateHolder mStateHolder;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.user_details_activity);
        
        Object retained = getLastNonConfigurationInstance();
        
        if(retained != null){
            mStateHolder = (StateHolder)retained;
        } else {
            mStateHolder = new StateHolder();
            
            
            
        }
        
    }
    
    private void ensureUi() {
        
        View viewProgressBar = findViewById(R.id.userActivityDetailsProgress);
        TextView tvUsername = (TextView)findViewById(R.id.userDetailsActivityUsername);
        TextView tvLastSeen = (TextView)findViewById(R.id.userDetailsActivityHometownOrLastSeen);
        Button btnFriend = (Button)findViewById(R.id.userDetailsActivityFriendButton);
        View viewBadges = findViewById(R.id.userDetailsActivityGeneralBadges);
        View viewTips = findViewById(R.id.userDetailsActivityGeneralTips);
        TextView tvBadges = (TextView)findViewById(R.id.userDetailsActivityGeneralBadgesValue);
        TextView tvTips = (TextView)findViewById(R.id.userDetailsActivityGeneralTipsValue);
        ImageView ivBadgesChevron = (ImageView)findViewById(R.id.userDetailsActivityGeneralBadgesChevron);
        ImageView ivTipsChevron = (ImageView)findViewById(R.id.userDetailsActivityGeneralTipsChevron);
        View viewAddFriends = findViewById(R.id.userDetailsActivityAddFriends);
        View viewFriends = findViewById(R.id.userDetailsActivityFriends);
        TextView tvFriends = (TextView)findViewById(R.id.userDetailsActivityFriendsText);
        ImageView ivFriends = (ImageView)findViewById(R.id.userDetailsActivityFriendsChevron);
        
        viewProgressBar.setVisibility(View.VISIBLE);
        tvUsername.setText("");
        tvLastSeen.setText("");
        viewBadges.setFocusable(false);
        viewTips.setFocusable(false);
        tvBadges.setText("0");
        tvTips.setText("0");
        ivBadgesChevron.setVisibility(View.INVISIBLE);
        ivTipsChevron.setVisibility(View.INVISIBLE);
        btnFriend.setVisibility(View.INVISIBLE);
        
        viewAddFriends.setFocusable(false);
        viewFriends.setFocusable(false);
        viewAddFriends.setVisibility(View.GONE);
        viewFriends.setVisibility(View.GONE);
        ivFriends.setVisibility(View.INVISIBLE);
        tvFriends.setText("");

        if (mStateHolder.getLoadType() >= LOAD_TYPE_USER_PARTIAL) {
            User user = mStateHolder.getUser();
            ensureUiPhoto(user);
           
            tvUsername.setText(StringFormatters.getUserFullName(user));
            tvLastSeen.setText("");
            
            if (mStateHolder.getIsLoggedInUser() || 
                    UserUtils.isFriend(user) || 
                    UserUtils.isFriendStatusPendingThem(user) ||
                    UserUtils.isFriendStatusFollowingThem(user)) {
                
                     btnFriend.setVisibility(View.INVISIBLE);
            }else if (UserUtils.isFriendStatusPendingYou(user)) {
                btnFriend.setVisibility(View.VISIBLE);
                btnFriend.setText(getString(R.string.user_details_activity_friend_confirm));
                btnFriend.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //mStateHolder.startTaskFriend(UserDetailsActivity.this, StateHolder.TASK_FRIEND_ACCEPT);
                    }
                });
            }
            
        }   
    }
    
    
    /**
     * @param user
     */
    private void ensureUiPhoto(User user) {
        // TODO Auto-generated method stub
        
    }


    private static class StateHolder {
        public static final int TASK_FRIEND_ACCEPT = 0;
        public static final int TASK_FRIEND_ADD    = 1;
        private User mUser;
        private boolean mIsLoggedInUser;
        //private UserDetailsTask mTaskUserDetails;
        private boolean mIsRunningUserDetailsTask;
        private boolean mRanOnce;
        private int mLoadType;
        
        //private FriendTask mTaskFriend;
        private boolean mIsRunningFriendTask;
        
        public StateHolder() {
            mIsRunningUserDetailsTask = false;
            mIsRunningFriendTask = false;
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
        
        public void startTaskUserDetails(UserDetailsActivity activity, String userId) {
            if (!mIsRunningUserDetailsTask) {
                mIsRunningUserDetailsTask = true;
                //mTaskUserDetails = new UserDetailsTask(activity);
                //mTaskUserDetails.execute(userId);
            }
        }
        
        public void startTaskFriend(UserDetailsActivity activity, int action) {
            if (!mIsRunningFriendTask) {
                mIsRunningFriendTask = true;
                //mTaskFriend = new FriendTask(activity, mUser.getId(), action);
                //mTaskFriend.execute();
            }
        }
        
        public void setIsRunningUserDetailsTask(boolean isRunning) {
            mIsRunningUserDetailsTask = isRunning;
        }
        public boolean getIsTaskRunning() {
            return mIsRunningUserDetailsTask;
        }
        
        public boolean getIsRunningFriendTask() {
            return mIsRunningFriendTask;
        }
        
        public void setIsRunningFriendTask(boolean isRunning) {
            mIsRunningFriendTask = isRunning;
        }
        
        
        
    }

}
