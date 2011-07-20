package com.liangshan.jianjian.android;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;

import com.liangshan.jianjian.android.app.LoadableListActivityWithViewAndHeader;
import com.liangshan.jianjian.android.error.JianjianException;
import com.liangshan.jianjian.android.location.LocationUtils;
import com.liangshan.jianjian.android.widget.SegmentedButton;
import com.liangshan.jianjian.android.widget.SeparatedListAdapter;
import com.liangshan.jianjian.android.widget.SegmentedButton.OnClickListenerSegmentedButton;
import com.liangshan.jianjian.types.Fragment;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.RecommendMsg;
import com.liangshan.jianjian.util.UiUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.AdapterView.OnItemClickListener;

public class FriendsActivity extends LoadableListActivityWithViewAndHeader {    
    static final String TAG = "FriendsActivity";
    static final boolean DEBUG = JianjianSettings.DEBUG;
    
    private StateHolder mStateHolder;
    private ViewGroup mLayoutEmpty;
    
    private static final int SORT_METHOD_RECENT = 0;
    private static final int SORT_METHOD_NEARBY = 1;
    
    private static final int MENU_MORE_MAP             = 20;
    private static final int MENU_MORE_LEADERBOARD     = 21;
    
    private static final int MENU_REFRESH   = 0;
    private static final int MENU_EXIT = 1;
    public static final long SLEEP_TIME_IF_NO_LOCATION = 3000L;
    private LinkedHashMap<Integer, String> mMenuMoreSubitems;
    private SeparatedListAdapter mListAdapter;
    
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
        
        SegmentedButton buttons = getHeaderButton();
        buttons.clearButtons();
        buttons.addButtons(
                getString(R.string.friendsactivity_btn_recent),
                getString(R.string.friendsactivity_btn_nearby));
        if (mStateHolder.getSortMethod() == SORT_METHOD_RECENT) {
            buttons.setPushedButtonIndex(0);
        } else {
            buttons.setPushedButtonIndex(1);
        }
        
        buttons.setOnClickListener(new OnClickListenerSegmentedButton() {
            @Override
            public void onClick(int index) {
                if (index == 0) {
                    mStateHolder.setSortMethod(SORT_METHOD_RECENT);
                } else {
                    mStateHolder.setSortMethod(SORT_METHOD_NEARBY);
                }
                
                ensureUiListView();
            }
        });
        
        mMenuMoreSubitems = new LinkedHashMap<Integer, String>();
        mMenuMoreSubitems.put(MENU_MORE_MAP, getResources().getString(
                R.string.friendsactivity_menu_map));
        mMenuMoreSubitems.put(MENU_MORE_LEADERBOARD, getResources().getString(
                R.string.friendsactivity_menu_leaderboard));
        
        ensureUiListView();
    }
    
    private void ensureUiListView() {
        
        mListAdapter = new SeparatedListAdapter(this);
        if (mStateHolder.getSortMethod() == SORT_METHOD_RECENT) {
            sortCheckinsRecent(mStateHolder.getRecommends(), mListAdapter);
        } else {
            sortCheckinsDistance(mStateHolder.getRecommends(), mListAdapter);
        }
        ListView listView = getListView();
        listView.setAdapter(mListAdapter);
        listView.setDividerHeight(0);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                Checkin checkin = (Checkin) parent.getAdapter().getItem(position);
                if (checkin.getUser() != null) {
                    Intent intent = new Intent(FriendsActivity.this, UserDetailsActivity.class);
                    intent.putExtra(UserDetailsActivity.EXTRA_USER_PARCEL, checkin.getUser());
                    intent.putExtra(UserDetailsActivity.EXTRA_SHOW_ADD_FRIEND_OPTIONS, true);
                    startActivity(intent);
                }
                */
            }
        });
        
        // Prepare our no-results view. Something odd is going on with the layout parameters though.
        // If we don't explicitly set the layout to be fill/fill after inflating, the layout jumps
        // to a wrap/wrap layout. Furthermore, sdk 3 crashes with the original layout using two
        // buttons in a horizontal LinearLayout.
        LayoutInflater inflater = LayoutInflater.from(this);
        if (UiUtil.sdkVersion() > 3) {
            mLayoutEmpty = (ScrollView)inflater.inflate(
                    R.layout.friends_activity_empty, null);
            
            Button btnAddFriends = (Button)mLayoutEmpty.findViewById(
                    R.id.friendsActivityEmptyBtnAddFriends);
            btnAddFriends.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                    Intent intent = new Intent(FriendsActivity.this, AddFriendsActivity.class);
                    startActivity(intent);*/
                }
            });
            
            Button btnFriendRequests = (Button)mLayoutEmpty.findViewById(
                    R.id.friendsActivityEmptyBtnFriendRequests);
            btnFriendRequests.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                    Intent intent = new Intent(FriendsActivity.this, FriendRequestsActivity.class);
                    startActivity(intent);*/
                }
            });
            
        } else {
            // Inflation on 1.5 is causing a lot of issues, dropping full layout.
            mLayoutEmpty = (ScrollView)inflater.inflate(
                    R.layout.friends_activity_empty_sdk3, null);
        }
        
        mLayoutEmpty.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        
        if (mListAdapter.getCount() == 0) {
            setEmptyView(mLayoutEmpty);
        }
        
        if (mStateHolder.getIsRunningTask()) {
            setProgressBarIndeterminateVisibility(true);
            if (!mStateHolder.getRanOnce()) {
                setLoadingView();
            }
        } else {
            setProgressBarIndeterminateVisibility(false);
        }
    }
    
    /**
     * @param recommends
     * @param mListAdapter2
     */
    private void sortCheckinsDistance(Group<RecommendMsg> recommends,
            SeparatedListAdapter mListAdapter2) {
        // TODO Auto-generated method stub
        
    }


    /**
     * @param recommends
     * @param mListAdapter2
     */
    private void sortCheckinsRecent(Group<RecommendMsg> recommends,
            SeparatedListAdapter mListAdapter2) {
        // TODO Auto-generated method stub
        
    }
    
    private void onTaskStart() {
        setProgressBarIndeterminateVisibility(true);
        setLoadingView();
    }
    
    /**
     * @param recommends
     * @param mException
     */
    public void onTaskComplete(Group<RecommendMsg> recommends, Exception mException) {
        // TODO Auto-generated method stub
        
    }
    
    private static class TaskRecommends extends AsyncTask<Void, Void, Group<RecommendMsg>> {
        private Jianjianroid mJianjianroid;
        private FriendsActivity mActivity;
        private Exception mException;
        private int mPage;

        private TaskRecommends(FriendsActivity activity,int page){
            mJianjianroid = (Jianjianroid)activity.getApplication();
            mActivity = activity;
            mPage = page;
        }
        
        public void setActivity(FriendsActivity activity) {
            mActivity = activity;
        }
        /* (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Group<RecommendMsg> doInBackground(Void... params) {
            
            Group<RecommendMsg> recommends = null;
            try {
                recommends = getRecommends(mPage);
            } catch (Exception ex) {
                mException = ex;
            }

            return recommends;
        }
        
        /**
         * @return
         */
        private Group<RecommendMsg> getRecommends(int page) throws JianjianException, IOException{
            
            // If we're the startup tab, it's likely that we won't have a geo location
            // immediately. For now we can use this ugly method of sleeping for N
            // seconds to at least let network location get a lock. We're only trying
            // to discern between same-city, so we can even use LocationManager's
            // getLastKnownLocation() method because we don't care if we're even a few
            // miles off. The api endpoint doesn't require location, so still go ahead
            // even if we can't find a location.
            Location loc = mJianjianroid.getLastKnownLocation();
            if (loc == null) {
                try { Thread.sleep(SLEEP_TIME_IF_NO_LOCATION); } catch (InterruptedException ex) {}
                loc = mJianjianroid.getLastKnownLocation();
            }            
            
            Group<RecommendMsg> recommends = mJianjianroid.getJianjian().getRecommends(page, LocationUtils
                    .createJianjianLocation(loc));
            
            //Collections.sort(recommends, Comparators.getCheckinRecencyComparator());
            
            return recommends;
        }

        @Override
        protected void onPreExecute() {
            mActivity.onTaskStart();
        }

        @Override
        public void onPostExecute(Group<RecommendMsg> recommends) {
            if (mActivity != null) {
                mActivity.onTaskComplete(recommends, mException);
            }
        }
        
    }

    private static class StateHolder {
        private Group<RecommendMsg> mRecMsgs;
        private int mCurrentPage;
        private int mSortMethod;
        private boolean mRanOnce;
        private boolean mIsRunningTask;
        private TaskRecommends mTaskRecommends;
        
        public StateHolder() {
            mRanOnce = false;
            mIsRunningTask = false; 
            mRecMsgs = new Group<RecommendMsg>();
            mCurrentPage = 0;
        }
        
        public int getSortMethod() {
            return mSortMethod;
        }
        
        public void setSortMethod(int sortMethod) {
            mSortMethod = sortMethod;
        }
        public int getCurrentPage() {
            return mCurrentPage;
        }
        
        public void setCurrentPage(int page) {
            mCurrentPage = page;
        }
        
        public Group<RecommendMsg> getRecommends() {
            return mRecMsgs;
        }
        
        public void setRecommends(Group<RecommendMsg> recommends) {
            mRecMsgs = recommends;
        }
        
        public boolean getRanOnce() {
            return mRanOnce;
        }
        
        public void setRanOnce(boolean ranOnce) {
            mRanOnce = ranOnce;
        }
        
        public boolean getIsRunningTask() {
            return mIsRunningTask;
        }
        
        public void setIsRunningTask(boolean isRunning) {
            mIsRunningTask = isRunning;
        }
        
        public void setActivity(FriendsActivity activity) {
            if (mIsRunningTask) {
                mTaskRecommends.setActivity(activity);
            }
        }
        public void startTask(FriendsActivity activity) {
            if (!mIsRunningTask) {
                mTaskRecommends = new TaskRecommends(activity,mCurrentPage+1);
                mTaskRecommends.execute();
                mIsRunningTask = true;
            }
        }
        public void cancel() {
            if (mIsRunningTask) {
                mTaskRecommends.cancel(true);
                mIsRunningTask = false;
            }
        }
        
    }



}
