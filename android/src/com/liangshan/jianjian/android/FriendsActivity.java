package com.liangshan.jianjian.android;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;

import com.liangshan.jianjian.android.app.LoadableListActivityWithViewAndHeader;
import com.liangshan.jianjian.android.error.JianjianException;
import com.liangshan.jianjian.android.location.LocationUtils;
import com.liangshan.jianjian.android.util.EventTimestampSort;
import com.liangshan.jianjian.android.util.NotificationsUtil;
import com.liangshan.jianjian.android.widget.RecommendListAdapter;
import com.liangshan.jianjian.android.widget.SegmentedButton;
import com.liangshan.jianjian.android.widget.SeparatedListAdapter;
import com.liangshan.jianjian.android.widget.SegmentedButton.OnClickListenerSegmentedButton;
import com.liangshan.jianjian.types.Event;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.RecommendMsg;
import com.liangshan.jianjian.util.Comparators;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
    private LinearLayout footerview;
    private ListView listView;
    
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
            if(mListAdapter != null){
                mListAdapter.removeObserver();
            }
            
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
                mStateHolder.setCurrentPage(0);
                mStateHolder.setCurrentListItem(0);
                mStateHolder.setRecommends(new Group<RecommendMsg>());
                mStateHolder.startTask(this);
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
        
        ImageView ivRefresh = (ImageView)findViewById(R.id.list_refresh); 
        
        ivRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressBar pbRefresh = (ProgressBar)findViewById(R.id.list_refresh_progress);
                pbRefresh.setVisibility(View.VISIBLE);
                ImageView ivRefresh = (ImageView)findViewById(R.id.list_refresh);
                ivRefresh.setVisibility(View.GONE);
                mStateHolder.setCurrentPage(0);
                mStateHolder.setCurrentListItem(0);
                mStateHolder.setRecommends(new Group<RecommendMsg>());
                mStateHolder.startTask(FriendsActivity.this);
            }
        });
        
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
        
        //if(mListAdapter == null){
            mListAdapter = new SeparatedListAdapter(this);
        //}
        
        if (mStateHolder.getSortMethod() == SORT_METHOD_RECENT) {
            sortRecommendsRecent(mStateHolder.getRecommends(), mListAdapter);
        } else {
            sortRecommendsDistance(mStateHolder.getRecommends(), mListAdapter);
        }
        listView = getListView();
        listView.setAdapter(mListAdapter);
        listView.setDividerHeight(0);
        
        
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
                RecommendMsg recommend = (RecommendMsg) parent.getAdapter().getItem(position);
                if (recommend != null) {
                    Intent intent = new Intent(FriendsActivity.this, RecommendDetailsActivity.class);
                    intent.putExtra(RecommendDetailsActivity.EXTRA_RecommendMsg_PARCEL, recommend);
                    startActivity(intent);
                }
                
            }
        });
        
        
        footerview = (LinearLayout) LayoutInflater.from(
                listView.getContext()).inflate(R.layout.recommend_list_footer,null);
        
        footerview.setClickable(true);
        footerview.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mStateHolder.setCurrentListItem(listView.getCount());
                
                mStateHolder.startTask(FriendsActivity.this);
            }
            
        });
        footerview.setVisibility(View.VISIBLE);
        if(!mStateHolder.getRanOnce()){
            listView.addFooterView(footerview);
        }
        
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
    private void sortRecommendsDistance(Group<RecommendMsg> recommends,
            SeparatedListAdapter listAdapter) {
        // TODO Auto-generated method stub
        
    }


    /**
     * @param recommends
     * @param mListAdapter2
     */
    private void sortRecommendsRecent(Group<RecommendMsg> recommends,
            SeparatedListAdapter listAdapter) {
        
        // Sort all by timestamp first.
        Collections.sort(recommends, Comparators.getRecommendsRecencyComparator());
        
        // We'll group in different section adapters based on some time thresholds.
        Group<RecommendMsg> recent = new Group<RecommendMsg>();
        Group<RecommendMsg> today = new Group<RecommendMsg>();
        Group<RecommendMsg> yesterday = new Group<RecommendMsg>();
        Group<RecommendMsg> older = new Group<RecommendMsg>();
        
        EventTimestampSort timestamps = new EventTimestampSort();
        
        DateFormat format= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (RecommendMsg it: recommends){
            try { 
                Date date = format.parse(it.getCreateDate());
                if (date.after(timestamps.getBoundaryRecent())) {
                    recent.add(it);
                } else if (date.after(timestamps.getBoundaryToday())) {
                    today.add(it); 
                } else if (date.after(timestamps.getBoundaryYesterday())) {
                    yesterday.add(it);
                } else {
                    older.add(it);
                }
            } catch (Exception ex) {
                Log.e(TAG, "exception during parsing date: " + ex.getMessage());
                older.add(it);
            }
        }
        if (recent.size() > 0) {
            RecommendListAdapter adapter = new RecommendListAdapter(this, 
                    ((Jianjianroid) getApplication()).getRemoteResourceManager());
            adapter.setGroup(recent);
            listAdapter.addSection(getResources().getString(
                    R.string.friendsactivity_title_sort_recent), adapter);
        }
        
        if (today.size() > 0) {
            RecommendListAdapter adapter = new RecommendListAdapter(this, 
                    ((Jianjianroid) getApplication()).getRemoteResourceManager());
            adapter.setGroup(today);
            listAdapter.addSection(getResources().getString(
                    R.string.friendsactivity_title_sort_today), adapter);
        }
        if (yesterday.size() > 0) {
            RecommendListAdapter adapter = new RecommendListAdapter(this, 
                    ((Jianjianroid) getApplication()).getRemoteResourceManager());
            adapter.setGroup(yesterday);
            listAdapter.addSection(getResources().getString(
                    R.string.friendsactivity_title_sort_yesterday), adapter);
        }
        if (older.size() > 0) {
            RecommendListAdapter adapter = new RecommendListAdapter(this, 
                    ((Jianjianroid) getApplication()).getRemoteResourceManager());
            adapter.setGroup(older);
            listAdapter.addSection(getResources().getString(
                    R.string.friendsactivity_title_sort_older), adapter);
        }
        
    }
    
    private void onTaskStart() {
        setProgressBarIndeterminateVisibility(true);
        setLoadingView();
    }
    
    /**
     * @param page 
     * @param recommends
     * @param mException
     */
    public void onTaskComplete(Group<Event> events, int page, Exception ex) {
        mStateHolder.setRanOnce(true);
        mStateHolder.setIsRunningTask(false);
        mStateHolder.setCurrentPage(page);
        setProgressBarIndeterminateVisibility(false);
        
        ProgressBar pbRefresh = (ProgressBar)findViewById(R.id.list_refresh_progress);
        pbRefresh.setVisibility(View.GONE);
        ImageView ivRefresh = (ImageView)findViewById(R.id.list_refresh);
        ivRefresh.setVisibility(View.VISIBLE);
        
        // Clear list for new batch.
        mListAdapter.removeObserver();
        mListAdapter.clear();
        //if(mListAdapter == null){
            mListAdapter = new SeparatedListAdapter(this);
        //}
        
        if(events != null){
            Group<RecommendMsg> recommends = filterEventsFromJiepang(events);
            
            if(mStateHolder.getCurrentPage() <= 1 ){
                mStateHolder.setRecommends(recommends);
            } else {
                mStateHolder.addRecommends(recommends);
            }
            
            if (mStateHolder.getSortMethod() == SORT_METHOD_RECENT) {
                sortRecommendsRecent(mStateHolder.getRecommends(), mListAdapter);
            } else {
                sortRecommendsDistance(mStateHolder.getRecommends(), mListAdapter);
            }
        } else if (ex != null) {
            if(mStateHolder.getRecommends()== null){
                Group<RecommendMsg> recommends = new Group<RecommendMsg>();
                mStateHolder.setRecommends(recommends);
            }

            NotificationsUtil.ToastReasonForFailure(this, ex);
        }
        
        if (mStateHolder.getRecommends().size() == 0) {
            setEmptyView(mLayoutEmpty);
        }
        
        getListView().setAdapter(mListAdapter);
        
        if(mStateHolder.getRecommends()!=null&&!mStateHolder.getRecommends().isHasMore()){
            footerview.setVisibility(View.GONE);
        }
        //-1 footerview
        getListView().setSelection(mStateHolder.getCurrentListItem()-2);
        //getListView().setSelection(3);
    }
    
    /**
     * @param events
     * @return
     */
    private Group<RecommendMsg> filterEventsFromJiepang(Group<Event> events) {
        
        Group<RecommendMsg> recommends = new Group<RecommendMsg>();
        
        if(events.isHasMore()){
            recommends.setHasMore(true);
        } else {
            recommends.setHasMore(false);
        }
        
        //remove the events which doesn't come from jianjian and then set it to recommendMsg group
        for (Event it : events){
            if(it.getFragment() instanceof RecommendMsg){
                RecommendMsg re = (RecommendMsg) it.getFragment();
                if(re.getProduct()!= null){
                    if(it.getCreateDate()!=null){
                        re.setCreateDate(it.getCreateDate());
                    }
                    if(it.getEventId()!=null){
                        re.setFragmentId(it.getEventId());
                    }
                    
                    recommends.add(re);
                    
                }
            }          
        }
        
        return recommends;
    }



    private static class TaskRecommends extends AsyncTask<Void, Void, Group<Event>> {
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
        protected Group<Event> doInBackground(Void... params) {
            
            Group<Event> events = null;
            try {
                events = getEvents(mPage);
            } catch (Exception ex) {
                mException = ex;
            }

            return events;
        }
        
        /**
         * @return
         */
        private Group<Event> getEvents(int page) throws JianjianException, IOException{
            
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
            
            Group<Event> events = mJianjianroid.getJianjian().getEvents(page, LocationUtils
                    .createJianjianLocation(loc));
            
            Collections.sort(events, Comparators.getEventRecencyComparator());
            
            return events;
        }

        @Override
        protected void onPreExecute() {
            mActivity.onTaskStart();
        }

        @Override
        public void onPostExecute(Group<Event> events) {
            if (mActivity != null) {
                mActivity.onTaskComplete(events, mPage, mException);
            }
        }
        
    }

    private static class StateHolder {
        private Group<RecommendMsg> mRecommends;
        private int mCurrentPage;
        private int mCurrentListItem;
        private int mSortMethod;
        private boolean mRanOnce;
        private boolean mIsRunningTask;
        private TaskRecommends mTaskRecommends;
        
        public StateHolder() {
            mRanOnce = false;
            mIsRunningTask = false; 
            mRecommends = new Group<RecommendMsg>();
            mCurrentPage = 0;
            mCurrentListItem = 0;
        }
        
        /**
         * @param count
         */
        public void setCurrentListItem(int count) {
            mCurrentListItem = count;
        }
        public int getCurrentListItem() {
            return mCurrentListItem;
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
            return mRecommends;
        }
        
        public void setRecommends(Group<RecommendMsg> recommends) {
            mRecommends = recommends;
        }
        
        public void addRecommends(Group<RecommendMsg> recommends2) {
            
            if(mRecommends.addAll(recommends2)){
                mRecommends.setHasMore(recommends2.isHasMore());
            }
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
