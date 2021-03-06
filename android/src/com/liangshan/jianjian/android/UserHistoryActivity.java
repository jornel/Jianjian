/**
 * 
 */
package com.liangshan.jianjian.android;

import java.util.logging.Level;

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
import com.liangshan.jianjian.android.widget.HistoryListAdapter;
import com.liangshan.jianjian.general.Jianjian;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.RecommendMsg;
import com.liangshan.jianjian.types.User;

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

    public static final String EXTRA_USER = Jianjianroid.PACKAGE_NAME
            + ".UserHistoryActivity.EXTRA_USER";
    
    private StateHolder mStateHolder;
    private HistoryListAdapter mListAdapter;
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
            if (getIntent().hasExtra(EXTRA_USER)) {
                User user = getIntent().getExtras().getParcelable(EXTRA_USER);
                mStateHolder = new StateHolder(user);
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
        
        //if(mStateHolder.getCurrentPage()<=1){
            footerview = (LinearLayout) LayoutInflater.from(
                    getListView().getContext()).inflate(R.layout.recommend_list_footer,null);
            footerview.setClickable(true);
            footerview.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mStateHolder.setCurrentListItem(getListView().getCount());
                    
                    mStateHolder.startTaskHistory(UserHistoryActivity.this);
                }
                
            });
            
            getListView().addFooterView(footerview);
            
        //}
        
        if (mStateHolder.getIsRunningHistoryTask()) {
            setLoadingView();
        } else if (mStateHolder.getFetchedOnce() && mStateHolder.getHistory().size() == 0) {
            setEmptyView();
        }

        setTitle(getString(R.string.user_history_activity_title, mStateHolder.getUsername()));
        
    }
    
    private void startRecommendMsgActivity(RecommendMsg recommend) {
        
        if (recommend != null) {
            Intent intent = new Intent(UserHistoryActivity.this, RecommendDetailsActivity.class);
            intent.putExtra(RecommendDetailsActivity.EXTRA_RecommendMsg_PARCEL, recommend);
            startActivity(intent);
        }
    }



    @Override
    public int getNoSearchResultsStringId() {
        return R.string.user_history_activity_no_info;
    }
    
    private void onHistoryTaskComplete(Group<RecommendMsg> group, int page, Exception ex) {
        mStateHolder.setCurrentPage(page);
        
        mListAdapter.removeObserver();
        mListAdapter.clear();
        mListAdapter = new HistoryListAdapter(
                this, ((Jianjianroid) getApplication()).getRemoteResourceManager());
        
        if (group != null) {
            if(mStateHolder.getCurrentPage() <= 1 ){
                mStateHolder.setHistory(group);
            } else {
                mStateHolder.addHistory(group);
            }
            
            mListAdapter.setGroup(mStateHolder.getHistory());
            
        } 
        else if(mStateHolder.getCurrentPage()<=1){
            mStateHolder.setHistory(new Group<RecommendMsg>());
            mListAdapter.setGroup(mStateHolder.getHistory());
            
            
            NotificationsUtil.ToastReasonForFailure(this, ex);
        } else if(mStateHolder.getCurrentPage()>1){
            NotificationsUtil.ToastReasonForFailure(this, ex);
        }
        
        mStateHolder.setIsRunningHistoryTask(false);
        mStateHolder.setFetchedOnce(true);
        

        if(!mStateHolder.getHistory().isHasMore()){
            footerview.setVisibility(View.GONE);
        }
        
        
        if (mStateHolder.getHistory().size() == 0) {
            setEmptyView();
        }
        
        getListView().setAdapter(mListAdapter);
        getListView().setSelection(mStateHolder.getCurrentListItem()-2);

    }
    
    /**
     * Gets friends of the current user we're working for.
     */
    private static class HistoryTask extends AsyncTask<String, Void, Group<RecommendMsg>> {
        
        private UserHistoryActivity mActivity;
        private Exception mReason;
        private int mPage;

        public HistoryTask(UserHistoryActivity activity, int page) {
            mActivity = activity;
            mPage = page;
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
                
                Group<RecommendMsg> history = jianjian.history(mActivity.mStateHolder.getUserid(),null, mPage);

                //Log.i(TAG, "get history======");
                return filterRecFromJiepang(history);
            } catch (Exception e) {
                mReason = e;
            }
            return null;
        }

        /**
         * @param history
         * @return
         */
        private Group<RecommendMsg> filterRecFromJiepang(Group<RecommendMsg> history) {
            
            Group<RecommendMsg> recommends = new Group<RecommendMsg>();
            if(history.isHasMore()){
                recommends.setHasMore(true);
            } else {
                recommends.setHasMore(false);
            }
            for(RecommendMsg it:history){
                if(it.getProduct()!= null){ 
                    it.setFromUser(mActivity.mStateHolder.getUser());
                    recommends.add(it);                   
                }
            }
            
            return recommends;
        }

        @Override
        protected void onPostExecute(Group<RecommendMsg> recommends) {
            if (mActivity != null) {
                mActivity.onHistoryTaskComplete(recommends, mPage,mReason);
            }
        }

        @Override
        protected void onCancelled() {
            if (mActivity != null) {
                mActivity.onHistoryTaskComplete(null, mPage, mReason);
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
        private int mCurrentListItem;
        private int mCurrentPage;
        private User mUser;
        
        public StateHolder(User user) {
            mUsername = user.getUsername();
            mUserid = user.getUserid();
            mUser = user;
            mIsRunningHistoryTask = false;
            mFetchedOnce = false;
            mHistory = new Group<RecommendMsg>();
            mCurrentListItem = 0;
            mCurrentPage = 0;
        }
        
        public void setUser(User user){
            mUser = user;
        }
        public User getUser(){
            return mUser;
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
        
        public Group<RecommendMsg> getHistory() {
            return mHistory;
        }
        public void addHistory(Group<RecommendMsg> recommends2) {
            
            if(mHistory.addAll(recommends2)){
                mHistory.setHasMore(recommends2.isHasMore());
            }
        }
        
        public void setHistory(Group<RecommendMsg> history) {
            mHistory = history;
        }
        
        public void startTaskHistory(UserHistoryActivity activity) {
            if(mIsRunningHistoryTask != true){
                mIsRunningHistoryTask = true;
                mTaskHistory = new HistoryTask(activity, mCurrentPage+1);
                mTaskHistory.execute();
            }

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
