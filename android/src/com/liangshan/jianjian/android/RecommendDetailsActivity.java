/**
 * 
 */
package com.liangshan.jianjian.android;

import com.liangshan.jianjian.types.RecommendMsg;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

/**
 * @author jornel
 *
 */
public class RecommendDetailsActivity extends Activity {
    private static final String TAG = "RecommendDetailsActivity";

    private static final boolean DEBUG = JianjianSettings.DEBUG;
    
    public static final String EXTRA_RecommendMsg_PARCEL = Jianjianroid.PACKAGE_NAME
    + ".RecommendDetailsActivity.EXTRA_RecommendMsg_PARCEL";
    
    private StateHolder mStateHolder;
    
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
        setContentView(R.layout.recommend_details_activity);
        registerReceiver(mLoggedOutReceiver, new IntentFilter(Jianjianroid.INTENT_ACTION_LOGGED_OUT));
        Object retained = getLastNonConfigurationInstance();
        
        if (retained != null) {
            mStateHolder = (StateHolder) retained;
            mStateHolder.setActivityForTasks(this);
        } else {
            mStateHolder = new StateHolder();
            if (getIntent().hasExtra(EXTRA_RecommendMsg_PARCEL)) {
                Log.i(TAG, "Starting " + TAG + " with full recommend parcel.");
                RecommendMsg recommend = getIntent().getExtras().getParcelable(EXTRA_RecommendMsg_PARCEL);
                mStateHolder.setRecommendMsg(recommend);
            } else {
                Log.i(TAG, "Starting " + TAG + " as default recommend msg.");
                RecommendMsg recommend = new RecommendMsg();
                mStateHolder.setRecommendMsg(recommend);   
            }
        }
        
        ensureUi();
        
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        if (isFinishing()) {
            unregisterReceiver(mLoggedOutReceiver);
        }
    }
    /**
     * 
     */
    private void ensureUi() {
        // TODO Auto-generated method stub
        TextView tvUsername = (TextView)findViewById(R.id.recommendDetailsActivityUsername);
        if(mStateHolder.getRecommendMsg().getFromUser().getNick()!=null){
            tvUsername.setText(mStateHolder.getRecommendMsg().getFromUser().getNick());
        }
        
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        mStateHolder.setActivityForTasks(null);
        return mStateHolder;
    }
    
    private static class StateHolder {
        private RecommendMsg mRecommend;

        /**
         * @param recommendDetailsActivity
         */
        public void setActivityForTasks(RecommendDetailsActivity recommendDetailsActivity) {
            // TODO Auto-generated method stub
            
        }

        /**
         * @param recommend
         */
        public void setRecommendMsg(RecommendMsg recommend) {
            mRecommend = recommend;            
        }
        /**
         * @param recommend
         */
        public RecommendMsg getRecommendMsg() {
            return mRecommend;            
        }
        
    }

}
