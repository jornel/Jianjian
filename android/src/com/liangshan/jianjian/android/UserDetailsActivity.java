/**
 * 
 */
package com.liangshan.jianjian.android;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import com.liangshan.jianjian.android.location.LocationUtils;
import com.liangshan.jianjian.android.util.RemoteResourceManager;
import com.liangshan.jianjian.android.util.StringFormatters;
import com.liangshan.jianjian.android.util.UserUtils;
import com.liangshan.jianjian.types.User;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
    private RemoteResourceManager mRrm;
    private RemoteResourceManagerObserver mResourcesObserver;
    private Handler mHandler;
    
    
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

        if (mStateHolder.getLoadType() >= LOAD_TYPE_USER_PARTIAL) {  //no task during the user partial
            User user = mStateHolder.getUser();
            ensureUiPhoto(user);
           
            tvUsername.setText(StringFormatters.getUserFullName(user));
            tvLastSeen.setText("");
            
            //set the btnFriend
            
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
                        mStateHolder.startTaskFriend(UserDetailsActivity.this, StateHolder.TASK_FRIEND_ACCEPT);
                    }
                });
            }else {
                btnFriend.setVisibility(View.VISIBLE);
                btnFriend.setText(getString(R.string.user_details_activity_friend_add));
                btnFriend.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.setEnabled(false);
                        mStateHolder.startTaskFriend(UserDetailsActivity.this, StateHolder.TASK_FRIEND_ADD);
                    }                   
                });
            }
            
            if (mStateHolder.getLoadType() >= LOAD_TYPE_USER_FULL) {
                
                viewProgressBar.setVisibility(View.GONE);
                //tvBadges.setText(String.valueOf(user.getBadgeCount()));
                //tvTips.setText(String.valueOf(user.getTipCount()));
                
                // The rest of the items depend on if we're viewing ourselves or not.
                if (mStateHolder.getIsLoggedInUser()) {
                    viewAddFriends.setVisibility(View.VISIBLE);
                    
                    
                    if(user.getFriendCount() > 0){
                        viewFriends.setVisibility(View.VISIBLE); 
                        tvFriends.setText(
                                getResources().getString(
                                        R.string.user_details_activity_friends_in_common_text,
                                        user.getFriendCount()));
                        viewFriends.setFocusable(true);
                        ivFriends.setVisibility(View.VISIBLE);
                        
                      //todo start friends activity
                    }
                    
                    //start add friends activity
                    
                    viewAddFriends.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startAddFriendsActivity();
                        }
                    });
                    viewAddFriends.setFocusable(true);
                } else {
                    viewFriends.setVisibility(View.VISIBLE); 
                    
                    if(user.getFriendCount() > 0){
                        viewFriends.setVisibility(View.VISIBLE); 
                        tvFriends.setText(
                                getResources().getString(
                                        R.string.user_details_activity_friends_in_common_text,
                                        user.getFriendCount()));
                        
                      //start friends activity
                        
                     if (user.getFriendCount() > 0) {
                          viewFriends.setOnClickListener(new OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  startFriendsInCommonActivity();
                              }
                          });
                          viewFriends.setFocusable(true);
                          ivFriends.setVisibility(View.VISIBLE);
                        }
                    }
                    
                }                
                
            } else {
                // Haven't done a full load.
                if (mStateHolder.getRanOnce()) {
                    viewProgressBar.setVisibility(View.GONE);
                }
            }
            
        }else {
            // Haven't done a full load.
            if (mStateHolder.getRanOnce()) {
                viewProgressBar.setVisibility(View.GONE);
            }
        }
        
        
        // Regardless of load state, if running a task, show titlebar progress bar.
        if (mStateHolder.getIsTaskRunning()) {
            setProgressBarIndeterminateVisibility(true);
        } else {
            setProgressBarIndeterminateVisibility(false);
        }
        
        // Disable friend button if running friend task.
        if (mStateHolder.getIsRunningFriendTask()) {
            btnFriend.setEnabled(false);
        } else {
            btnFriend.setEnabled(true);
        }
        
    }


    /**
     * @param user
     */
    private void ensureUiPhoto(User user) {
        
        ImageView ivPhoto = (ImageView)findViewById(R.id.userDetailsActivityPhoto);
        
        if (user == null || user.getPhoto() == null) {
            ivPhoto.setImageResource(R.drawable.blank_boy);
            return;
        }
        
        Uri uriPhoto = Uri.parse(user.getPhoto());
        
        if (mRrm.exists(uriPhoto)) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(mRrm.getInputStream(Uri.parse(user
                        .getPhoto())));
                ivPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                setUserPhotoMissing(ivPhoto, user);
            }
        } else {
            mRrm.request(uriPhoto);
            setUserPhotoMissing(ivPhoto, user);
        }
        
        ivPhoto.postInvalidate();
        
        /*
        ivPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStateHolder.getLoadType() == LOAD_TYPE_USER_FULL) {
                    User user = mStateHolder.getUser();
                    
                    // If "_thumbs" exists, remove it to get the url of the
                    // full-size image.
                    String photoUrl = user.getPhoto().replace("_thumbs", "");
                    
                    // If we're viewing our own page, clicking the thumbnail should send the user
                    // to our built-in image viewer. Here we can give them the option of setting
                    // a new photo for themselves.
                    Intent intent = new Intent(UserDetailsActivity.this, FetchImageForViewIntent.class);
                    intent.putExtra(FetchImageForViewIntent.IMAGE_URL, photoUrl);
                    intent.putExtra(FetchImageForViewIntent.PROGRESS_BAR_MESSAGE, getResources()
                            .getString(R.string.user_activity_fetch_full_image_message));
                    
                    if (mStateHolder.getIsLoggedInUser()) {
                        intent.putExtra(FetchImageForViewIntent.LAUNCH_VIEW_INTENT_ON_COMPLETION, false);
                        startActivityForResult(intent, ACTIVITY_REQUEST_CODE_FETCH_IMAGE);
                    } else {
                        startActivity(intent);
                    }
                }
            }
        });
        */
        
    }
    
    private void setUserPhotoMissing(ImageView ivPhoto, User user) {
        if ("male".equals(user.getGender())) {
            ivPhoto.setImageResource(R.drawable.blank_boy);
        } else {
            ivPhoto.setImageResource(R.drawable.blank_girl);
        }
    }
    
    /**
     * 
     */
    private void startAddFriendsActivity() {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * 
     */
    private void startFriendsInCommonActivity() {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * @param user
     * @param mReason
     */
    public void onUserDetailsTaskComplete(User user, Exception mReason) {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * Even if the caller supplies us with a User object parcelable, it won't
     * have all the badge etc extra info in it. As soon as the activity starts,
     * we launch this task to fetch a full user object, and merge it with
     * whatever is already supplied in mUser.
     */
    private static class UserDetailsTask extends AsyncTask<String, Void, User> {

        private UserDetailsActivity mActivity;
        private Exception mReason;

        public UserDetailsTask(UserDetailsActivity activity) {
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            mActivity.ensureUi();
        }

        @Override
        protected User doInBackground(String... params) {
            try {
                
                return ((Jianjianroid) mActivity.getApplication()).getJianjian().user(
                        params[0],
                        false,//to do
                        false,
                        false,
                        LocationUtils.createFoursquareLocation(((Jianjianroid) mActivity
                                .getApplication()).getLastKnownLocation()));
            } catch (Exception e) {
                mReason = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            if (mActivity != null) {
                mActivity.onUserDetailsTaskComplete(user, mReason);
            }
        }

        @Override
        protected void onCancelled() {
            if (mActivity != null) {
                mActivity.onUserDetailsTaskComplete(null, mReason);
            }
        }

        public void setActivity(UserDetailsActivity activity) {
            mActivity = activity;
        }
    }


    private static class StateHolder {
        public static final int TASK_FRIEND_ACCEPT = 0;
        public static final int TASK_FRIEND_ADD    = 1;
        private User mUser;
        private boolean mIsLoggedInUser;
        private UserDetailsTask mTaskUserDetails;
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
                mTaskUserDetails = new UserDetailsTask(activity);
                mTaskUserDetails.execute(userId);
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
    
    private class RemoteResourceManagerObserver implements Observer {
        @Override
        public void update(Observable observable, Object data) {
            mHandler.post(mRunnableUpdateUserPhoto);
        }
    }
    
    private Runnable mRunnableUpdateUserPhoto = new Runnable() {
        @Override 
        public void run() {
            ensureUiPhoto(mStateHolder.getUser());
        }
    };


}
