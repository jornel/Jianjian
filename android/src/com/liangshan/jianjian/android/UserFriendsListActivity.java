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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.liangshan.jianjian.android.app.LoadableListActivity;
import com.liangshan.jianjian.android.util.NotificationsUtil;
import com.liangshan.jianjian.android.widget.FriendsListAdapter;
import com.liangshan.jianjian.general.Jianjian;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.User;

/**
 * @author jornel
 *
 */
public class UserFriendsListActivity extends LoadableListActivity {
    static final String TAG = "UserFriendsActivity";

    public static final String EXTRA_USER_NAME = Jianjianroid.PACKAGE_NAME
            + ".UserFriendsActivity.EXTRA_USER_NAME";

    public static final String EXTRA_USER_ID = Jianjianroid.PACKAGE_NAME
            + ".UserFriendsActivity.EXTRA_USER_ID";
    
    private StateHolder mStateHolder;
    private FriendsListAdapter mListAdapter;
    private LinearLayout footerview;
    
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
                mStateHolder.startTaskFriends(this);
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
        mListAdapter = new FriendsListAdapter(
                this, ((Jianjianroid) getApplication()).getRemoteResourceManager());
        mListAdapter.setGroup(mStateHolder.getFriends());
        
        ListView listView = getListView();
        listView.setAdapter(mListAdapter);
        listView.setSmoothScrollbarEnabled(true);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
                Object obj = (Object)mListAdapter.getItem(position);
                if (obj != null) {
                    startFriendDetailActivity((User)obj);
                }
            }
        });
        
        //if(mStateHolder.getCurrentPage()<=1){
            footerview = (LinearLayout) LayoutInflater.from(
                    getListView().getContext()).inflate(R.layout.recommend_list_footer,null);
            footerview.setClickable(true);
            footerview.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mStateHolder.setCurrentListItem(getListView().getCount());
                    
                    mStateHolder.startTaskFriends(UserFriendsListActivity.this);
                }
                
            });
            
            getListView().addFooterView(footerview);
            
        //}
        
        if (mStateHolder.getIsRunningFriendsTask()) {
            setLoadingView();
        } else if (mStateHolder.getFetchedOnce() && mStateHolder.getFriends().size() == 0) {
            setEmptyView();
        }

        setTitle(getString(R.string.user_friend_activity_title, mStateHolder.getUsername()));
        
    }
    
    private void startFriendDetailActivity(User user) {
        Intent intent = new Intent(UserFriendsListActivity.this, UserDetailsActivity.class);
        intent.putExtra(UserDetailsActivity.EXTRA_USER_PARCEL, user);
        //intent.putExtra(UserDetailsActivity.EXTRA_SHOW_ADD_FRIEND_OPTIONS, true);
        startActivity(intent);
        
    }



    @Override
    public int getNoSearchResultsStringId() {
        return R.string.user_history_activity_no_info;
    }
    
    private void onFriendsTaskComplete(Group<User> group, int page, Exception ex) {
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
    
    /**
     * Gets friends of the current user we're working for.
     */
    private static class FriendsTask extends AsyncTask<String, Void, Group<User>> {
        
        private UserFriendsListActivity mActivity;
        private Exception mReason;
        private int mPage;

        public FriendsTask(UserFriendsListActivity activity, int page) {
            mActivity = activity;
            mPage = page;
        }
        
        @Override
        protected void onPreExecute() {
            mActivity.setLoadingView();
        }

        @Override
        protected Group<User> doInBackground(String... params) {
            try {
                Jianjianroid jianjianroid = (Jianjianroid) mActivity.getApplication();
                Jianjian jianjian = jianjianroid.getJianjian();
                
                // Prune out shouts for now.
                
                Group<User> friends = jianjian.friendlist(mActivity.mStateHolder.getUserid(),mPage);

                //Log.i(TAG, "get Friends======");
                return friends;
            } catch (Exception e) {
                mReason = e;
            }
            return null;
        }


        @Override
        protected void onPostExecute(Group<User> friends) {
            if (mActivity != null) {
                mActivity.onFriendsTaskComplete(friends, mPage,mReason);
            }
        }

        @Override
        protected void onCancelled() {
            if (mActivity != null) {
                mActivity.onFriendsTaskComplete(null, mPage, mReason);
            }
        }
        
        public void setActivity(UserFriendsListActivity activity) {
            mActivity = activity;
        }
    }
    
    private static class StateHolder {
        private String mUsername;
        private String mUserid;
        private Group<User> mFriends;
        private FriendsTask mTaskFriends;
        private boolean mIsRunningFriendsTask;
        private boolean mFetchedOnce;
        private int mCurrentListItem;
        private int mCurrentPage;
        
        public StateHolder(String username,String userid) {
            mUsername = username;
            mUserid = userid;
            mIsRunningFriendsTask = false;
            mFetchedOnce = false;
            mFriends = new Group<User>();
            mCurrentListItem = 0;
            mCurrentPage = 0;
        }
        
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
        
        public String getUsername() {
            return mUsername;
        }
        
        public String getUserid() {
            return mUserid;
        }
        
        public Group<User> getFriends() {
            return mFriends;
        }
        public void addFriends(Group<User> friends2) {
            
            if(mFriends.addAll(friends2)){
                mFriends.setHasMore(friends2.isHasMore());
            }
        }
        
        public void setFriends(Group<User> Friends) {
            mFriends = Friends;
        }
        
        public void startTaskFriends(UserFriendsListActivity activity) {
            if(mIsRunningFriendsTask != true){
                mIsRunningFriendsTask = true;
                mTaskFriends = new FriendsTask(activity, mCurrentPage+1);
                mTaskFriends.execute();
            }

        }
        
        public void setActivityForTask(UserFriendsListActivity activity) {
            if (mTaskFriends != null) {
                mTaskFriends.setActivity(activity);
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
            if (mTaskFriends != null) {
                mTaskFriends.setActivity(null);
                mTaskFriends.cancel(true);
            }
        }
        
    }
    
    
}
