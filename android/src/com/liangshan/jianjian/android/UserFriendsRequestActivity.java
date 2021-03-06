/**
 * 
 */
package com.liangshan.jianjian.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.liangshan.jianjian.android.app.LoadableListActivity;
import com.liangshan.jianjian.android.util.NotificationsUtil;
import com.liangshan.jianjian.android.widget.FriendsRequestAdapter;
import com.liangshan.jianjian.general.Jianjian;
import com.liangshan.jianjian.types.FriendInvitation;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.User;

/**
 * @author jornel
 *
 */
public class UserFriendsRequestActivity extends LoadableListActivity {
    static final String TAG = "UserFriendsRequestActivity";

    public static final String EXTRA_USER_NAME = Jianjianroid.PACKAGE_NAME
            + ".UserFriendsRequestActivity.EXTRA_USER_NAME";

    public static final String EXTRA_USER_ID = Jianjianroid.PACKAGE_NAME
            + ".UserFriendsRequestActivity.EXTRA_USER_ID";
    
    private StateHolder mStateHolder;
    private FriendsRequestAdapter mListAdapter;
    //private LinearLayout footerview;
    
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(mLoggedOutReceiver, new IntentFilter(Jianjianroid.INTENT_ACTION_LOGGED_OUT));
        
        Object retained = getLastNonConfigurationInstance();
        
        if (retained != null && retained instanceof StateHolder) {
            mStateHolder = (StateHolder) retained;
            mStateHolder.setActivityForTask(this);
        } else {
            if (getIntent().hasExtra(EXTRA_USER_NAME)) {
                mStateHolder = new StateHolder(getIntent().getStringExtra(EXTRA_USER_NAME),getIntent().getStringExtra(EXTRA_USER_ID));
                mStateHolder.startTaskFriendsRequest(this);
            } else {
                Log.e(TAG, TAG + " requires username as intent extra.");
                finish();
                return;
            }
        }
        
        ensureUi();
    }



    @Override
    public void onPause() {
        super.onPause();
        
        if (isFinishing()) {
            mStateHolder.cancelTasks();
            unregisterReceiver(mLoggedOutReceiver);
        }
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() {
        mStateHolder.setActivityForTask(null);
        return mStateHolder;
    }
    
    /**
     * 
     */
    private void ensureUi() {
        mListAdapter = new FriendsRequestAdapter(
                this, ((Jianjianroid) getApplication()).getRemoteResourceManager());
        if(mStateHolder.getFriendsRequest()!=null){
            mListAdapter.setGroup(mStateHolder.getFriendsRequest()); 
        }
        
        
        ListView listView = getListView();
        listView.setAdapter(mListAdapter);
        listView.setSmoothScrollbarEnabled(true);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
                Object obj = (Object)mListAdapter.getItem(position);
                FriendInvitation friendRequest = (FriendInvitation) obj;
                User friend = friendRequest.getFromUser();
                if (obj != null) {
                    startFriendDetailActivity(friend);
                }
            }
        });
        
        /*if(mStateHolder.getCurrentPage()<=1){
            footerview = (LinearLayout) LayoutInflater.from(
                    getListView().getContext()).inflate(R.layout.recommend_list_footer,null);
            footerview.setClickable(true);
            footerview.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mStateHolder.setCurrentListItem(getListView().getCount());
                    
                    //mStateHolder.startTaskFriends(UserFriendsRequestActivity.this);
                }
                
            });
            
            getListView().addFooterView(footerview);
            
        }*/
        
        if (mStateHolder.getIsRunningFriendsTask()) {
            setLoadingView();
        } else if (mStateHolder.getFetchedOnce() && mStateHolder.getFriendsRequest().size() == 0) {
            setEmptyView();
        }

        setTitle(getString(R.string.user_friend_request_activity_title, mStateHolder.getUsername()));
        
    }
    
    private void startFriendDetailActivity(User user) {
        Intent intent = new Intent(UserFriendsRequestActivity.this, UserDetailsActivity.class);
        intent.putExtra(UserDetailsActivity.EXTRA_USER_PARCEL, user);
        //intent.putExtra(UserDetailsActivity.EXTRA_SHOW_ADD_FRIEND_OPTIONS, true);
        startActivity(intent);
        
    }



    @Override
    public int getNoSearchResultsStringId() {
        return R.string.user_friend_request_activity_no_info;
    }
    /*
    private void onFriendInvitationTaskComplete(Group<User> group, int page, Exception ex) {
        mStateHolder.setCurrentPage(page);
        
        mListAdapter.removeObserver();
        mListAdapter.clear();
        mListAdapter = new FriendsListAdapter(
                this, ((Jianjianroid) getApplication()).getRemoteResourceManager());
        
        if (group != null) {
            if(mStateHolder.getCurrentPage() <= 1 ){
                mStateHolder.setFriends(group);
            } else {
                mStateHolder.addFriends(group);
            }
            
            mListAdapter.setGroup(mStateHolder.getFriends());
            
        } 
        else if(mStateHolder.getCurrentPage()<=1){
            mStateHolder.setFriends(new Group<User>());
            mListAdapter.setGroup(mStateHolder.getFriends());
            
            
            NotificationsUtil.ToastReasonForFailure(this, ex);
        } else if(mStateHolder.getCurrentPage()>1){
            NotificationsUtil.ToastReasonForFailure(this, ex);
        }
        
        mStateHolder.setIsRunningFriendsTask(false);
        mStateHolder.setFetchedOnce(true);
        

        if(!mStateHolder.getFriends().isHasMore()){
            footerview.setVisibility(View.INVISIBLE);
        }
        
        
        if (mStateHolder.getFriends().size() == 0) {
            setEmptyView();
        }
        
        getListView().setAdapter(mListAdapter);
        getListView().setSelection(mStateHolder.getCurrentListItem()-2);

    }
    */
    
    /**
     * @param invs
     * @param mReason
     */
    public void onFriendInvitationTaskComplete(Group<FriendInvitation> invs, Exception ex) {
       
        mListAdapter.removeObserver();
        mListAdapter.clear();
        
        mListAdapter = new FriendsRequestAdapter(
                this, ((Jianjianroid) getApplication()).getRemoteResourceManager());
        if(invs != null){
            mStateHolder.setFriendsRequest(invs);
            mListAdapter.setGroup(invs);
            
        } else {
            mStateHolder.setFriendsRequest(new Group<FriendInvitation>());
            mListAdapter.setGroup(mStateHolder.getFriendsRequest());
            NotificationsUtil.ToastReasonForFailure(this, ex);
        }
        
        mStateHolder.setIsRunningFriendsTask(false);
        mStateHolder.setFetchedOnce(true);
        
        if (mStateHolder.getFriendsRequest().size() == 0) {
            setEmptyView();
        }
        getListView().setAdapter(mListAdapter);
    }
    
    

    
    private static class FriendInvitationTask extends AsyncTask<Void, Void, Group<FriendInvitation>> {

        private UserFriendsRequestActivity mActivity;
        private String mUserId;
        private Exception mReason;

        public FriendInvitationTask(UserFriendsRequestActivity activity, String userId) {
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

        public void setActivity(UserFriendsRequestActivity activity) {
            mActivity = activity;
        }
    }
    
    private static class StateHolder {
        private String mUsername;
        private String mUserid;
        private Group<FriendInvitation> mFriendsRequest;
        private FriendInvitationTask mTaskFriendsRequest;
        private boolean mIsRunningFriendsTask;
        private boolean mFetchedOnce;
        private int mCurrentListItem;
        private int mCurrentPage;
        
        public StateHolder(String username,String userid) {
            mUsername = username;
            mUserid = userid;
            mIsRunningFriendsTask = false;
            mFetchedOnce = false;
            
            mCurrentListItem = 0;
            mCurrentPage = 0;
        }
        /*
        public void setCurrentListItem(int count) {
            mCurrentListItem = count;
        }
        public int getCurrentListItem() {
            return mCurrentListItem;
        }
        
        public int getCurrentPage() {
            return mCurrentPage;
        }
        
        public void setCurrentPage(int page) {
            mCurrentPage = page;
        }
        */
        
        public String getUsername() {
            return mUsername;
        }
        
        public String getUserid() {
            return mUserid;
        }
        
        public Group<FriendInvitation> getFriendsRequest() {
            return mFriendsRequest;
        }

        
        public void setFriendsRequest(Group<FriendInvitation> invs) {
            mFriendsRequest = invs;
        }
        
        public void startTaskFriendsRequest(UserFriendsRequestActivity activity) {
            if(mIsRunningFriendsTask != true){
                mIsRunningFriendsTask = true;
                mTaskFriendsRequest = new FriendInvitationTask(activity,getUserid());
                mTaskFriendsRequest.execute();
            }

        }
        
        public void setActivityForTask(UserFriendsRequestActivity activity) {
            if (mTaskFriendsRequest != null) {
                mTaskFriendsRequest.setActivity(activity);
            }
        }
        
        public void setIsRunningFriendsTask(boolean isRunning) {
            mIsRunningFriendsTask = isRunning;
        }

        public boolean getIsRunningFriendsTask() {
            return mIsRunningFriendsTask;
        }
        
        public void setFetchedOnce(boolean fetchedOnce) {
            mFetchedOnce = fetchedOnce;
        }
        
        public boolean getFetchedOnce() {
            return mFetchedOnce;
        }
        
        public void cancelTasks() {
            if (mTaskFriendsRequest != null) {
                mTaskFriendsRequest.setActivity(null);
                mTaskFriendsRequest.cancel(true);
            }
        }
        
    }

    
}
