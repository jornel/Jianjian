/**
 * 
 */
package com.liangshan.jianjian.android;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import com.liangshan.jianjian.android.error.JianjianException;
import com.liangshan.jianjian.android.location.LocationUtils;
import com.liangshan.jianjian.android.util.NotificationsUtil;
import com.liangshan.jianjian.android.util.RemoteResourceManager;
import com.liangshan.jianjian.android.util.UserUtils;
import com.liangshan.jianjian.general.Jianjian;
import com.liangshan.jianjian.types.FriendInvitation;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.User;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author ezhuche
 *
 */
public class UserDetailsActivity extends Activity {
    
    static final String TAG = "UserDetailsActivity";
    static final boolean DEBUG = JianjianSettings.DEBUG;
    
    public static final String EXTRA_USER_ID = Jianjianroid.PACKAGE_NAME 
    + ".UserDetailsActivity.EXTRA_USER_ID";
    public static final String EXTRA_USER_PARCEL = Jianjianroid.PACKAGE_NAME
    + ".UserDetailsActivity.EXTRA_USER_PARCEL";
    private static final int LOAD_TYPE_USER_NONE    = 0;
    private static final int LOAD_TYPE_USER_ID      = 1;
    private static final int LOAD_TYPE_USER_PARTIAL = 2;
    private static final int LOAD_TYPE_USER_FULL    = 3;
    
    private static final int MENU_REFRESH   = 0;
    private static final int MENU_LOGOUT = 1;
    private static final int MENU_EXIT = 2;
    
    
    private SharedPreferences mPrefs;
    
    private StateHolder mStateHolder;
    private RemoteResourceManager mRrm;
    private RemoteResourceManagerObserver mResourcesObserver;
    private Handler mHandler;
    
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            finish();
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.user_details_activity);
        
        registerReceiver(mLoggedOutReceiver, new IntentFilter(Jianjianroid.INTENT_ACTION_LOGGED_OUT));
        
        Object retained = getLastNonConfigurationInstance();
        
        if(retained != null){
            mStateHolder = (StateHolder)retained;
            mStateHolder.setActivityForTasks(this);
        } else {
            mStateHolder = new StateHolder();
            
            if (getIntent().hasExtra(EXTRA_USER_PARCEL)) {
                Log.i(TAG, "Starting " + TAG + " with full user parcel.");
                User user = getIntent().getExtras().getParcelable(EXTRA_USER_PARCEL);
                mStateHolder.setUser(user);
                mStateHolder.setLoadType(LOAD_TYPE_USER_PARTIAL);
            } else if (getIntent().hasExtra(EXTRA_USER_ID)) {
                Log.i(TAG, "Starting " + TAG + " with user ID.");
                User user = new User();
                user.setUserid(getIntent().getExtras().getString(EXTRA_USER_ID));
                mStateHolder.setUser(user);
                mStateHolder.setLoadType(LOAD_TYPE_USER_ID);
            } else {
                Log.i(TAG, "Starting " + TAG + " as logged-in user.");
                User user = new User(); 
                user.setUserid(null);
                mStateHolder.setUser(user);
                mStateHolder.setLoadType(LOAD_TYPE_USER_ID);
            }
                
            mStateHolder.setIsLoggedInUser(
              mStateHolder.getUser().getUserid() == null ||
              mStateHolder.getUser().getUserid().equals(
                  ((Jianjianroid) getApplication()).getUserId()));
        }
        
        mHandler = new Handler();
        mRrm = ((Jianjianroid) getApplication()).getRemoteResourceManager();
        mResourcesObserver = new RemoteResourceManagerObserver();
        mRrm.addObserver(mResourcesObserver);
        
        ensureUi();

        if (mStateHolder.getLoadType() != LOAD_TYPE_USER_FULL && 
           !mStateHolder.getIsRunningUserDetailsTask() &&
           !mStateHolder.getRanOnce()) 
        {           
            mStateHolder.startTaskUserDetails(this, mStateHolder.getUser().getUserid());
            
            if(!mStateHolder.getIsRunningFriendInvitationTask()&&mStateHolder.getIsLoggedInUser()){
                mStateHolder.startTaskFriendInvitation(this);
            }
        }
        
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        if (isFinishing()) {
            mStateHolder.cancelTasks();
            mHandler.removeCallbacks(mRunnableUpdateUserPhoto);

            RemoteResourceManager rrm = ((Jianjianroid) getApplication()).getRemoteResourceManager();
            rrm.deleteObserver(mResourcesObserver);
        }
    }
    
    
    @Override 
    protected void onDestroy() {
        super.onDestroy();
        
        unregisterReceiver(mLoggedOutReceiver);
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(Menu.NONE, MENU_REFRESH, Menu.NONE, R.string.refresh)
            .setIcon(R.drawable.ic_menu_refresh);
            
        menu.add(Menu.NONE, MENU_LOGOUT, Menu.NONE, R.string.preferences_logout_title);
        
        menu.add(Menu.NONE, MENU_EXIT, Menu.NONE, R.string.preferences_exit_title);

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_REFRESH:
                mStateHolder.startTaskUserDetails(this, mStateHolder.getUser().getUserid());
                return true;
            case MENU_LOGOUT:
                mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                mPrefs.edit().clear().commit();
                // TODO: If we re-implement oAuth, we'll have to call
                // clearAllCrendentials here.
                ((Jianjianroid) getApplication()).getJianjian().setCredentials(null, null);

                Intent intent = new Intent(this, LoginActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                        | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                sendBroadcast(new Intent(Jianjianroid.INTENT_ACTION_LOGGED_OUT));
                startActivity(intent);

                return true;
            case MENU_EXIT:
                android.os.Process.killProcess(android.os.Process.myPid());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    

    
    private void ensureUi() {
        
        //get the View control
        View viewProgressBar = findViewById(R.id.userActivityDetailsProgress);
        TextView tvUsername = (TextView)findViewById(R.id.userDetailsActivityUsername);
        TextView tvLastSeen = (TextView)findViewById(R.id.userDetailsActivityHometownOrLastSeen);
        Button btnFriend = (Button)findViewById(R.id.userDetailsActivityFriendButton);
        View viewBadges = findViewById(R.id.userDetailsActivityGeneralBadges);
        View viewPoints = findViewById(R.id.userDetailsActivityGeneralPoints);
        TextView tvBadges = (TextView)findViewById(R.id.userDetailsActivityGeneralBadgesValue);
        TextView tvPoints = (TextView)findViewById(R.id.userDetailsActivityGeneralPointsValue);
        ImageView ivBadgesChevron = (ImageView)findViewById(R.id.userDetailsActivityGeneralBadgesChevron);
        View viewAddFriends = findViewById(R.id.userDetailsActivityAddFriends);
        View viewFriends = findViewById(R.id.userDetailsActivityFriends);
        TextView tvFriends = (TextView)findViewById(R.id.userDetailsActivityFriendsText);
        ImageView ivFriends = (ImageView)findViewById(R.id.userDetailsActivityFriendsChevron);
        View viewRecommends = findViewById(R.id.userDetailsActivityRecommends);
        TextView tvRecommends = (TextView)findViewById(R.id.userDetailsActivityRecommendsText);
        ImageView ivRecommendsChevron = (ImageView)findViewById(R.id.userDetailsActivityRecommendsChevron);
        //RelativeLayout rlFriendRequests = (RelativeLayout)findViewById(R.id.userDetailsActivityFriendRequest);
        //TextView tvFriendRequests = (TextView)findViewById(R.id.userDetailsActivityFriendRequestText);
        
        
        //set the initial value
        viewProgressBar.setVisibility(View.VISIBLE);
        tvUsername.setText("");
        tvLastSeen.setText("");
        //tvFriendRequests.setText("");
        viewBadges.setFocusable(false);
        viewPoints.setFocusable(false);
        tvBadges.setText("0");
        tvPoints.setText("0");
        ivBadgesChevron.setVisibility(View.INVISIBLE);
        btnFriend.setVisibility(View.INVISIBLE);
        
        viewRecommends.setFocusable(false);
        viewAddFriends.setFocusable(false);
        viewFriends.setFocusable(false);
        viewAddFriends.setVisibility(View.GONE);
        viewFriends.setVisibility(View.GONE);
        viewRecommends.setVisibility(View.GONE);
        //rlFriendRequests.setVisibility(View.GONE);
        ivFriends.setVisibility(View.INVISIBLE);
        ivRecommendsChevron.setVisibility(View.INVISIBLE);
        tvFriends.setText("");
        tvRecommends.setText("");

        if (mStateHolder.getLoadType() >= LOAD_TYPE_USER_PARTIAL) { // full load and partial load
            User user = mStateHolder.getUser();
            ensureUiPhoto(user);
           
            tvUsername.setText(user.getNick());
            
            if(user.getLastRecMsg()==null){
                tvLastSeen.setText(user.getCity());//to-do
                
            }
            
            

            
            if (mStateHolder.getLoadType() >= LOAD_TYPE_USER_FULL) {//just for the full load(most cases are full in jianjian)
                
                viewProgressBar.setVisibility(View.GONE);
                tvBadges.setText(String.valueOf(user.getBadgesCount()));
                tvPoints.setText(String.valueOf(user.getPoints()));
                
                if (user.getBadgesCount() > 0) {// show the badges, no sense in jianjian
                    viewBadges.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //startBadgesActivity();
                        }
                    });
                    viewBadges.setFocusable(true);
                    
                    ivBadgesChevron.setVisibility(View.VISIBLE);
                    
                }
                
                if (user.getPoints() >= 0) { // show the points, a little sense in jianjian
                    
                    viewPoints.setFocusable(true);
                    
                }
                //set the btnFriend
                
                if (mStateHolder.getIsLoggedInUser() || 
                        UserUtils.isFriend(user) || 
                        UserUtils.isFriendStatusPendingThem(user) ||
                        UserUtils.isFriendStatusFollowingThem(user)) {//for the owner and friends, don't show the btn
                    
                         btnFriend.setVisibility(View.INVISIBLE);
                }else if (UserUtils.isFriendStatusPendingYou(user)) {// show the comfirm btn for the request 
                    btnFriend.setVisibility(View.VISIBLE);
                    btnFriend.setText(getString(R.string.user_details_activity_friend_confirm));
                    btnFriend.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mStateHolder.startTaskFriend(UserDetailsActivity.this, StateHolder.TASK_FRIEND_ACCEPT);
                        }
                    });
                }else {// show the btn to add the friend
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
                
                // The rest of the items depend on if we're viewing ourselves or not.
                if (mStateHolder.getIsLoggedInUser()) {//myself
                    viewAddFriends.setVisibility(View.VISIBLE);
                    viewRecommends.setVisibility(View.VISIBLE);
                    
                    //show the own number of recommends
                    
                    tvRecommends.setText(getResources().getString(
                                    R.string.user_details_activity_recommends_text, user.getRecMsgCount()));
                    if (user.getRecMsgCount() > 0) {
                        viewRecommends.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               startRecommendsActivity();
                            }
                        });
                        viewRecommends.setFocusable(true);
                        ivRecommendsChevron.setVisibility(View.VISIBLE);
                    }
                    
                    //show the own friends
                    if(user.getFriendCount() > 0){
                        viewFriends.setVisibility(View.VISIBLE); 
                        tvFriends.setText(
                                getResources().getString(
                                        R.string.user_details_activity_friends_in_common_text,
                                        user.getFriendCount()));
                        viewFriends.setFocusable(true);
                        ivFriends.setVisibility(View.VISIBLE);
                        
                        viewFriends.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startFriendsInCommonActivity();
                            }
                        });
                        
                    }
                    
                    //start add friends activity
                    
                    viewAddFriends.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startAddFriendsActivity();
                        }
                    });
                    viewAddFriends.setFocusable(true);
                } else {//see the other users
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
        
        if (user == null || user.getPhoto() == null || user.getPhoto() =="") {
            ivPhoto.setImageResource(R.drawable.blank_boy);
            return;
        }
        
        Uri uriPhoto = Uri.parse(user.getPhoto());
        
        if (mRrm.exists(uriPhoto)) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(mRrm.getInputStream(uriPhoto));
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
        if ("male".equals(user.getGender())||"1".equals(user.getGender())) {
            ivPhoto.setImageResource(R.drawable.blank_boy);
        } else {
            ivPhoto.setImageResource(R.drawable.blank_girl);
        }
    }
    
    /**
     * 
     */
    private void startRecommendsActivity() {
        
        Intent intent = new Intent(UserDetailsActivity.this, UserHistoryActivity.class);
        intent.putExtra(UserHistoryActivity.EXTRA_USER_NAME, mStateHolder.getUser().getUsername());
        intent.putExtra(UserHistoryActivity.EXTRA_USER_ID, mStateHolder.getUser().getUserid());
        intent.putExtra(UserHistoryActivity.EXTRA_USER, mStateHolder.getUser());
        startActivity(intent); 
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
        
        Intent intent = new Intent(UserDetailsActivity.this, UserFriendsListActivity.class);
        intent.putExtra(UserFriendsListActivity.EXTRA_USER_ID, mStateHolder.getUser().getUserid());
        intent.putExtra(UserFriendsListActivity.EXTRA_USER_NAME, mStateHolder.getUser().getUsername());
        startActivity(intent); 
    }
    
    private void startFriendsRequestActivity() {
        Group<FriendInvitation> invs = mStateHolder.getFriendIvitations();
        if(invs == null||invs.size()==0){
            RelativeLayout rlFriendRequests = (RelativeLayout)findViewById(R.id.userDetailsActivityFriendRequest);
            rlFriendRequests.setVisibility(View.GONE);
            return;
        }
        
        Intent intent = new Intent(UserDetailsActivity.this, UserFriendsRequestActivity.class);
        intent.putExtra(UserFriendsRequestActivity.EXTRA_USER_ID, mStateHolder.getUser().getUserid());
        intent.putExtra(UserFriendsRequestActivity.EXTRA_USER_NAME, mStateHolder.getUser().getUsername());       
        startActivity(intent); 
    }
    
    private void onFriendTaskComplete(User user, int action, Exception ex) {
        mStateHolder.setIsRunningFriendTask(false);
        
        // The api isn't returning an updated friend status flag here, so we'll
        // overwrite it manually for now, assuming success if the user object
        // was not null.
        User userCurrent = mStateHolder.getUser();
        if (user != null) {
            switch (action) {
                case StateHolder.TASK_FRIEND_ACCEPT:
                    userCurrent.setUsername(user.getUsername());
                    userCurrent.setFriendstatus("true");
                    break;
                case StateHolder.TASK_FRIEND_ADD:
                    userCurrent.setIinvited("true");
                    break;
            }
        } else {
            NotificationsUtil.ToastReasonForFailure(this, ex);
        }
        
        ensureUi();
    }
    
    /**
     * @param invs
     * @param mReason
     */
    public void onFriendInvitationTaskComplete(Group<FriendInvitation> invs, Exception ex) {
        
        mStateHolder.setIsRunningFriendInvitationTask(false);
        
        RelativeLayout rlFriendRequests = (RelativeLayout)findViewById(R.id.userDetailsActivityFriendRequest);
        TextView tvFriendRequests = (TextView)findViewById(R.id.userDetailsActivityFriendRequestText);
        if( invs != null){
            if(invs.size()==0) {
                rlFriendRequests.setVisibility(View.GONE);
            } else {
                mStateHolder.setFriendIvitations(invs);
                rlFriendRequests.setVisibility(View.VISIBLE);
                rlFriendRequests.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFriendsRequestActivity();
                    }
                });
                tvFriendRequests.setText("");
                tvFriendRequests.setText(
                        getResources().getString(
                                R.string.user_details_activity_friend_requests_text,
                                invs.size()));
            }
        } else if (ex != null) {
            NotificationsUtil.ToastReasonForFailure(this, ex);
        } else {
            Toast.makeText(this, "A surprising new error for the friend requests has occurred!", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * @param user
     * @param mReason
     */
    public void onUserDetailsTaskComplete(User user, Exception ex) {
        mStateHolder.setIsRunningUserDetailsTask(false);
        mStateHolder.setRanOnce(true);
        if (user != null) {
            mStateHolder.setUser(user);
            mStateHolder.setLoadType(LOAD_TYPE_USER_FULL);
        } else if (ex != null) {
            NotificationsUtil.ToastReasonForFailure(this, ex);
        } else {
            Toast.makeText(this, "A surprising new error has occurred!", Toast.LENGTH_SHORT).show();
        }
        
        ensureUi();
        
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
                
                return ((Jianjianroid) mActivity.getApplication()).getJianjian().showUser(
                        params[0],//userid
                        LocationUtils.createJianjianLocation(((Jianjianroid) mActivity
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
    
    private static class FriendTask extends AsyncTask<Void, Void, User> {

        private UserDetailsActivity mActivity;
        private String mUserId;
        private int mAction;
        private Exception mReason;

        public FriendTask(UserDetailsActivity activity, String userId, int action) {
            mActivity = activity;
            mUserId = userId;
            mAction = action;
        }

        @Override
        protected void onPreExecute() {
            mActivity.ensureUi();
        }

        @Override
        protected User doInBackground(Void... params) {
            Jianjian jianjian = ((Jianjianroid) mActivity.getApplication()).getJianjian();
            try {
                switch (mAction) {
                    case StateHolder.TASK_FRIEND_ACCEPT:
                        return jianjian.friendApprove(mUserId);
                    case StateHolder.TASK_FRIEND_ADD:
                        return jianjian.friendSendrequest(mUserId);  
                    default:
                        throw new JianjianException("Unknown action type supplied.");
                }
            } catch (Exception e) {
                mReason = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            if (mActivity != null) {
                mActivity.onFriendTaskComplete(user, mAction, mReason);
            }
        }

        @Override
        protected void onCancelled() {
            if (mActivity != null) {
                mActivity.onFriendTaskComplete(null, mAction, mReason);
            }
        }

        public void setActivity(UserDetailsActivity activity) {
            mActivity = activity;
        }
    }
    
    private static class FriendInvitationTask extends AsyncTask<Void, Void, Group<FriendInvitation>> {

        private UserDetailsActivity mActivity;
        private String mUserId;
        private Exception mReason;

        public FriendInvitationTask(UserDetailsActivity activity, String userId) {
            mActivity = activity;
            mUserId = userId;
        }

        @Override
        protected void onPreExecute() {
            mActivity.ensureUi();
        }

        @Override
        protected Group<FriendInvitation> doInBackground(Void... params) {
            Jianjian jianjian = ((Jianjianroid) mActivity.getApplication()).getJianjian();
            try {
                return jianjian.getFriendInvitations(mUserId);    

            } catch (Exception e) {
                mReason = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Group<FriendInvitation> invs) {
            if (mActivity != null) {
                mActivity.onFriendInvitationTaskComplete(invs, mReason);
            }
        }

        @Override
        protected void onCancelled() {
            if (mActivity != null) {
                mActivity.onFriendInvitationTaskComplete(null, mReason);
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
        private Group<FriendInvitation> mInvitations;
        
        private FriendTask mTaskFriend;
        private boolean mIsRunningFriendTask;
        
        private FriendInvitationTask mTaskFriendInvitation;
        private boolean mIsRunningFriendInvitationTask;
        
        public StateHolder() {
            mIsRunningUserDetailsTask = false;
            mIsRunningFriendTask = false;
            mIsLoggedInUser = false;
            mRanOnce = false;
            mLoadType = LOAD_TYPE_USER_NONE;
        }
        
        /**
         * @param invs
         */
        public void setFriendIvitations(Group<FriendInvitation> invs) {
            mInvitations = invs;
        }
        
        public Group<FriendInvitation> getFriendIvitations() {
            return mInvitations;
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
                mTaskFriend = new FriendTask(activity, mUser.getUserid(), action);
                mTaskFriend.execute();
            }
        }
        
        public void startTaskFriendInvitation(UserDetailsActivity activity) {
            if (!mIsRunningFriendInvitationTask) {
                mIsRunningFriendInvitationTask = true;
                mTaskFriendInvitation = new FriendInvitationTask(activity, mUser.getUserid());
                mTaskFriendInvitation.execute();
            }
        }
        
        public void setActivityForTasks(UserDetailsActivity activity) {
            if (mTaskUserDetails != null) {
                mTaskUserDetails.setActivity(activity);
            }
            if (mTaskFriend != null) {
                mTaskFriend.setActivity(activity);
            }
            if (mTaskFriendInvitation != null) {
                mTaskFriendInvitation.setActivity(activity);
            }
            
        }
        
        public boolean getIsRunningUserDetailsTask() {
            return mIsRunningUserDetailsTask;
        }
        
        public void setIsRunningUserDetailsTask(boolean isRunning) {
            mIsRunningUserDetailsTask = isRunning;
        }
        
        public boolean getIsRunningFriendTask() {
            return mIsRunningFriendTask;
        }
        
        public void setIsRunningFriendTask(boolean isRunning) {
            mIsRunningFriendTask = isRunning;
        }
        
        public boolean getIsRunningFriendInvitationTask() {
            return mIsRunningFriendInvitationTask;
        }
        
        public void setIsRunningFriendInvitationTask(boolean isRunning) {
            mIsRunningFriendInvitationTask = isRunning;
        }
        
        public void cancelTasks() {
            if (mTaskUserDetails != null) {
                mTaskUserDetails.setActivity(null);
                mTaskUserDetails.cancel(true);
            }
            
            if (mTaskFriend != null) {
                mTaskFriend.setActivity(null);
                mTaskFriend.cancel(true);
            }
            if (mTaskFriendInvitation != null) {
                mTaskFriendInvitation.setActivity(null);
                mTaskFriendInvitation.cancel(true);
            }
        }
        
        public boolean getIsTaskRunning() {
            return mIsRunningUserDetailsTask|| mIsRunningFriendTask|| mIsRunningFriendInvitationTask;
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
