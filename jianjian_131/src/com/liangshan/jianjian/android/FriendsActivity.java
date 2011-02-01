package com.liangshan.jianjian.android;


import android.app.ListActivity;
import android.os.Bundle;
import android.view.Window;

public class FriendsActivity extends ListActivity {    
    static final String TAG = "FriendsActivity";
    static final boolean DEBUG = JianjianSettings.DEBUG;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.friends_list_activity);
        
    }

}
