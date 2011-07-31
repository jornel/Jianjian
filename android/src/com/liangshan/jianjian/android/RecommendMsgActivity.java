/**
 * 
 */
package com.liangshan.jianjian.android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

/**
 * @author jornel
 *
 */
public class RecommendMsgActivity extends Activity {
    private static final String TAG = "RecommendMsgActivity";

    private static final boolean DEBUG = JianjianSettings.DEBUG;
    
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            finish();
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.recommendmsg_activity);
        registerReceiver(mLoggedOutReceiver, new IntentFilter(Jianjianroid.INTENT_ACTION_LOGGED_OUT));
        
        
    }

}
