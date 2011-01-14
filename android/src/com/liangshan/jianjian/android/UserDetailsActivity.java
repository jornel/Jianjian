/**
 * 
 */
package com.liangshan.jianjian.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * @author ezhuche
 *
 */
public class UserDetailsActivity extends Activity {
    
    static final String TAG = "UserDetailsActivity";
    static final boolean DEBUG = JianjianSettings.DEBUG;
    
    public static final String EXTRA_USER_ID = "com.liangshan.jianjian.android.UserDetailsActivity.EXTRA_USER_ID";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.user_details_activity);
        
    }

}
