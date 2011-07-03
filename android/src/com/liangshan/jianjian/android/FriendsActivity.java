package com.liangshan.jianjian.android;

import com.liangshan.jianjian.android.app.LoadableListActivityWithViewAndHeader;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.RecommendMsg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

public class FriendsActivity extends LoadableListActivityWithViewAndHeader {    
    static final String TAG = "FriendsActivity";
    static final boolean DEBUG = JianjianSettings.DEBUG;
    
    private StateHolder mStateHolder;
    private ViewGroup mLayoutEmpty;
    
    private static final int SORT_METHOD_RECENT = 0;
    private static final int SORT_METHOD_NEARBY = 1;
    
    private static final int MENU_REFRESH   = 0;
    private static final int MENU_EXIT = 1;
    
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
        registerReceiver(mLoggedOutReceiver, new IntentFilter(Jianjianroid.INTENT_ACTION_LOGGED_OUT));
        
        if (getLastNonConfigurationInstance() != null) {
            mStateHolder = (StateHolder) getLastNonConfigurationInstance();
            mStateHolder.setActivity(this);
        } else {
            mStateHolder = new StateHolder();
            mStateHolder.setSortMethod(SORT_METHOD_RECENT);
        }
        
        ensureUi();
        
        Jianjianroid mJianjianroid = (Jianjianroid)getApplication();
        if (mJianjianroid.isReady()) {
            if (!mStateHolder.getRanOnce()) {
                mStateHolder.startTask(this);
            }
        }
        
        setContentView(R.layout.friends_list_activity);
        
    }
    

    @Override
    public void onResume() {
        super.onResume();

        ((Jianjianroid) getApplication()).requestLocationUpdates(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        
        ((Jianjianroid) getApplication()).removeLocationUpdates();

        if (isFinishing()) {
            //mListAdapter.removeObserver();
            unregisterReceiver(mLoggedOutReceiver);
            mStateHolder.cancel();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(Menu.NONE, MENU_REFRESH, Menu.NONE, R.string.refresh)
            .setIcon(R.drawable.ic_menu_refresh);
        
        menu.add(Menu.NONE, MENU_EXIT, Menu.NONE, R.string.preferences_exit_title);

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_REFRESH:
                //mStateHolder.startTaskUserDetails(this, mStateHolder.getUser().getId());
                return true;
            case MENU_EXIT:
                android.os.Process.killProcess(android.os.Process.myPid());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() {
        mStateHolder.setActivity(null);
        return mStateHolder;
    }
    
    @Override
    public int getNoSearchResultsStringId() {
        return R.string.no_friend_recommends;
    }
    
    /**
     * 
     */
    private void ensureUi() {
        // TODO Auto-generated method stub
        
    }
    
    private static class StateHolder {
        private Group<RecommendMsg> mRecMsgs;
        private int mSortMethod;
        private boolean mRanOnce;
        private boolean mIsRunningTask;
        //private TaskCheckins mTaskCheckins;
        
        public StateHolder() {
            mRanOnce = false;
            mIsRunningTask = false; 
            mRecMsgs = new Group<RecommendMsg>();
        }
        
        public int getSortMethod() {
            return mSortMethod;
        }
        
        public void setSortMethod(int sortMethod) {
            mSortMethod = sortMethod;
        }
        
        public Group<RecommendMsg> getCheckins() {
            return mRecMsgs;
        }
        
        public void setCheckins(Group<RecommendMsg> recommends) {
            mRecMsgs = recommends;
        }
        
        public boolean getRanOnce() {
            return mRanOnce;
        }
        
        public void setRanOnce(boolean ranOnce) {
            mRanOnce = ranOnce;
        }
        
        public void setActivity(FriendsActivity activity) {
            if (mIsRunningTask) {
                //mTaskCheckins.setActivity(activity);
            }
        }
        public void startTask(FriendsActivity activity) {
            if (!mIsRunningTask) {
                //mTaskCheckins = new TaskCheckins(activity);
                //mTaskCheckins.execute();
                mIsRunningTask = true;
            }
        }
        public void cancel() {
            if (mIsRunningTask) {
                //mTaskCheckins.cancel(true);
                mIsRunningTask = false;
            }
        }
        
    }

}
