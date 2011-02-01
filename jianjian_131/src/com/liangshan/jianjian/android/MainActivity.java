package com.liangshan.jianjian.android;

import com.liangshan.jianjian.android.util.TabsUtil;
import com.liangshan.jianjian.android.util.UserUtils;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TabHost;


/**
 * @author Zhuoping Chen (joe@liangshan.com)
 */
public class MainActivity extends TabActivity {
	
	public static final String TAG = "MainActivity";
	public static final boolean DEBUG = JianjianSettings.DEBUG;
	
	private TabHost mTabHost;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Log.d(TAG, "onCreate()");
        setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL);
        
        if (DEBUG) Log.d(TAG, "Setting up main activity layout.");
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        setContentView(R.layout.main_activity);
        initTabHost();
    }
    private void initTabHost() {
        
        if (mTabHost != null) {
            throw new IllegalStateException("Trying to intialize already initializd TabHost");
        }
        
        mTabHost = getTabHost();
        
        //String[] startupTabValues = getResources().getStringArray(R.array.startup_tabs_values);
        
        //TODO: add the PreferenceManager for the startup tab       
        //String startupTab = startupTabValues[0];
        
        //if(startupTab.equalsIgnoreCase("Me")){
        //}
        Intent friendsIntent = new Intent(this, FriendsActivity.class);
        Intent productsIntent = new Intent(this, ProductsActivity.class);
        
        TabsUtil.addTab(mTabHost, getString(R.string.tab_main_nav_friends), R.drawable.tab_main_nav_friends_selector, 
                1, friendsIntent);
        
        TabsUtil.addTab(mTabHost, getString(R.string.tab_main_nav_products), R.drawable.tab_main_nav_nearby_selector, 
                2, productsIntent);
        
        Intent meIntent= new Intent(this, UserDetailsActivity.class);
        //TODO simulate the user id
        String userId = getString(R.string.testuserid1);
        String userGender = getString(R.string.male);
        meIntent.putExtra(UserDetailsActivity.EXTRA_USER_ID, userId == null ? "unknown"
                : userId);
        
        TabsUtil.addTab(mTabHost, getString(R.string.tab_main_nav_me), 
                UserUtils.getDrawableForMeTabByGender(userGender), 5, meIntent);
    }
}