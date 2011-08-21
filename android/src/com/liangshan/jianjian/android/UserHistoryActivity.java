/**
 * 
 */
package com.liangshan.jianjian.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.liangshan.jianjian.android.app.LoadableListActivity;

/**
 * @author jornel
 *
 */
public class UserHistoryActivity extends LoadableListActivity {
    static final String TAG = "UserHistoryActivity";

    public static final String EXTRA_USER_NAME = Jianjianroid.PACKAGE_NAME
            + ".UserHistoryActivity.EXTRA_USER_NAME";
    
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
    }

    @Override
    public void onPause() {
        super.onPause();
        
        if (isFinishing()) {
            //mStateHolder.cancelTasks();
            unregisterReceiver(mLoggedOutReceiver);
        }
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() {
        //mStateHolder.setActivityForTaskFriends(null);
        //return mStateHolder;
        return null;
    
    }
}