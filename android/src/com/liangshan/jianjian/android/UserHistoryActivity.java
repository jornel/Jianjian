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
import com.liangshan.jianjian.android.widget.HistoryListAdapter;
import com.liangshan.jianjian.general.Jianjian;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.RecommendMsg;

/**
 * @author jornel
 *
 */
public class UserHistoryActivity extends LoadableListActivity {
    static final String TAG = "UserHistoryActivity";

    public static final String EXTRA_USER_NAME = Jianjianroid.PACKAGE_NAME
            + ".UserHistoryActivity.EXTRA_USER_NAME";

    public static final String EXTRA_USER_ID = Jianjianroid.PACKAGE_NAME
            + ".UserHistoryActivity.EXTRA_USER_ID";
    
    private StateHolder mStateHolder;
    private HistoryListAdapter mListAdapter;
    
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
                mStateHolder.startTaskHistory(this);
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
        mListAdapter = new HistoryListAdapter(
                this, ((Jianjianroid) getApplication()).getRemoteResourceManager());
        mListAdapter.setGroup(mStateHolder.getHistory());
        
        ListView listView = getListView();
        listView.setAdapter(mListAdapter);
        listView.setSmoothScrollbarEnabled(true);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
                Object obj = (Object)mListAdapter.getItem(position);
                if (obj != null) {
                    startRecommendMsgActivity((RecommendMsg)obj);
                }
            }
        });
        
        if (mStateHolder.getIsRunningHistoryTask()) {
            setLoadingView();
        } else if (mStateHolder.getFetchedOnce() && mStateHolder.getHistory().size() == 0) {
            setEmptyView();
        }

        setTitle(getString(R.string.user_history_activity_title, mStateHolder.getUsername()));
        
    }
    
    private void startRecommendMsgActivity(RecommendMsg recommend) {
        // TODO Auto-generated method stub
        
    }



    @Override
    public int getNoSearchResultsStringId() {
        return R.string.user_history_activity_no_info;
    }
    
    private void onHistoryTaskComplete(Group<RecommendMsg> group, Exception ex) {

    }
    
    /**
     * Gets friends of the current user we're working for.
     */
    private static class HistoryTask extends AsyncTask<String, Void, Group<RecommendMsg>> {
        
        private UserHistoryActivity mActivity;
        private Exception mReason;

        public HistoryTask(UserHistoryActivity activity) {
            mActivity = activity;
        }
        
        @Override
        protected void onPreExecute() {
            mActivity.setLoadingView();
        }

        @Override
        protected Group<RecommendMsg> doInBackground(String... params) {
            try {
                Jianjianroid jianjianroid = (Jianjianroid) mActivity.getApplication();
                Jianjian jianjian = jianjianroid.getJianjian();
                
                // Prune out shouts for now.
                
                Group<RecommendMsg> history = jianjian.history(mActivity.mStateHolder.getUserid(),null, 1);
                /*
                Group<RecommendMsg> venuesOnly = new Group<RecommendMsg>();
                for (RecommendMsg it : history) {
                    if (it.getVenue() != null) {
                        venuesOnly.add(it);
                    }
                }
                
                return venuesOnly;*/
                return null;
            } catch (Exception e) {
                mReason = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Group<RecommendMsg> recommends) {
            if (mActivity != null) {
                mActivity.onHistoryTaskComplete(recommends, mReason);
            }
        }

        @Override
        protected void onCancelled() {
            if (mActivity != null) {
                mActivity.onHistoryTaskComplete(null, mReason);
            }
        }
        
        public void setActivity(UserHistoryActivity activity) {
            mActivity = activity;
        }
    }
    
    private static class StateHolder {
        private String mUsername;
        private String mUserid;
        private Group<RecommendMsg> mHistory;
        private HistoryTask mTaskHistory;
        private boolean mIsRunningHistoryTask;
        private boolean mFetchedOnce;
        
        public StateHolder(String username,String userid) {
            mUsername = username;
            mUserid = userid;
            mIsRunningHistoryTask = false;
            mFetchedOnce = false;
            mHistory = new Group<RecommendMsg>();
        }
        
        public String getUsername() {
            return mUsername;
        }
        
        public String getUserid() {
            return mUserid;
        }
        
        public Group<RecommendMsg> getHistory() {
            return mHistory;
        }
        
        public void setHistory(Group<RecommendMsg> history) {
            mHistory = history;
        }
        
        public void startTaskHistory(UserHistoryActivity activity) {
            mIsRunningHistoryTask = true;
            mTaskHistory = new HistoryTask(activity);
            mTaskHistory.execute();
        }
        
        public void setActivityForTask(UserHistoryActivity activity) {
            if (mTaskHistory != null) {
                mTaskHistory.setActivity(activity);
            }
        }
        
        public void setIsRunningHistoryTask(boolean isRunning) {
            mIsRunningHistoryTask = isRunning;
        }

        public boolean getIsRunningHistoryTask() {
            return mIsRunningHistoryTask;
        }
        
        public void setFetchedOnce(boolean fetchedOnce) {
            mFetchedOnce = fetchedOnce;
        }
        
        public boolean getFetchedOnce() {
            return mFetchedOnce;
        }
        
        public void cancelTasks() {
            if (mTaskHistory != null) {
                mTaskHistory.setActivity(null);
                mTaskHistory.cancel(true);
            }
        }
        
    }
    
    
}
