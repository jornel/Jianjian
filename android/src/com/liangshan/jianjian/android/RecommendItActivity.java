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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author ezhuche
 *
 */
public class RecommendItActivity extends Activity {
    
    static final String TAG = "RecommendItActivity";
    static final boolean DEBUG = JianjianSettings.DEBUG;
    
    private Button mRecommendItButton;
    private EditText mNameEditText;
    private LinearLayout pickupPlaceLayout;
    private ImageView pickupPlaceIcon;
    private TextView pickupPlaceTextView;
    private EditText mPriceEditText;
    private Spinner currencySpinner;
    private EditText recommendDesEditText;
    private ImageButton takePhotoImgButton;
    
    
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            finish();
        }
    };
    
    private TextWatcher mNameFieldWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mRecommendItButton.setEnabled(canEnableSaveButton());
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Log.d(TAG, "onCreate()");
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.recommend_it_activity);
        
        registerReceiver(mLoggedOutReceiver, new IntentFilter(Jianjianroid.INTENT_ACTION_LOGGED_OUT));
        
        mRecommendItButton = (Button) findViewById(R.id.recommendItButton);
        mNameEditText = (EditText) findViewById(R.id.nameEditText);
        mPriceEditText = (EditText) findViewById(R.id.priceEditText);
        
        
        
    }
    
    private boolean canEnableSaveButton() {
        return mNameEditText.getText().length() > 0;
    }


}
