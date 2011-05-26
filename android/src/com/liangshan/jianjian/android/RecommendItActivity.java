/**
 * 
 */
package com.liangshan.jianjian.android;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
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
    private static final int DIALOG_PICK_CATEGORY = 1;
    private static final int DIALOG_ERROR = 2;
    
    private StateHolder mStateHolder;
    
    private Button mRecommendItButton;
    private EditText mNameEditText;
    private LinearLayout mPickupPlaceLayout;
    private ImageView mPickupPlaceIcon;
    private TextView mPickupPlaceTextView;
    private EditText mPriceEditText;
    private Spinner mCurrencySpinner;
    private EditText mRecommendDesEditText;
    private ImageButton mTakePhotoImgButton;
    
    private ProgressDialog mDlgProgress;
    
    
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
        mPickupPlaceLayout = (LinearLayout) findViewById(R.id.pickupPlaceLayout);
        mPickupPlaceIcon = (ImageView) findViewById(R.id.pickupPlaceIcon);
        mPickupPlaceTextView = (TextView) findViewById(R.id.pickupPlaceTextView);
        mPriceEditText = (EditText) findViewById(R.id.priceEditText);
        mCurrencySpinner = (Spinner) findViewById(R.id.currencySpinner);
        mRecommendDesEditText = (EditText) findViewById(R.id.recommendDesEditText);
        mTakePhotoImgButton = (ImageButton) findViewById(R.id.takePhotoImgButton);
        
        mPickupPlaceLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_PICK_CATEGORY);
            }
        });
        mPickupPlaceLayout.setEnabled(false);
        
        mRecommendItButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
                String productName = mNameEditText.getText().toString();
                String price = mPriceEditText.getText().toString();
                String currency = mCurrencySpinner.getSelectedItem().toString();
                String recommendDes = mRecommendDesEditText.getText().toString();
                
            }
        });
        
        mNameEditText.addTextChangedListener(mNameFieldWatcher);
        Object retained = getLastNonConfigurationInstance();
        
        
        if (retained != null && retained instanceof StateHolder) {
            mStateHolder = (StateHolder) retained;
            mStateHolder.setActivity(this);
            
        } else {
            mStateHolder = new StateHolder();
            
            
            // If passed the venue parcelable, then we are in 'edit' mode.
            /*if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(EXTRA_VENUE_TO_EDIT)) {
                Venue venue = getIntent().getExtras().getParcelable(EXTRA_VENUE_TO_EDIT);
                if (venue != null) {
                    mStateHolder.setVenueBeingEdited(venue);
                    setFields(venue);
                
                    setTitle(getResources().getString(R.string.add_venue_activity_label_edit_venue));
                    
                    mAddOrEditVenueButton.setText(getResources().getString(
                            R.string.add_venue_activity_btn_submit_edits));
                } else {
                    Log.e(TAG, "Null venue parcelable supplied at startup, will finish immediately.");
                    finish();
                }
                
            } else {
                //mStateHolder.startTaskAddressLookup(this);
            }*/
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        ((Jianjianroid) getApplication()).requestLocationUpdates(true);
        
        /*if (mStateHolder.getIsRunningTaskAddOrEditVenue()) {
            startProgressBar();
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        ((Jianjianroid) getApplication()).removeLocationUpdates();
        
        stopProgressBar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLoggedOutReceiver);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        mStateHolder.setActivity(null);
        return mStateHolder;
    }
    
    
    private void startProgressBar(String message) {
        if (mDlgProgress == null) {
            mDlgProgress = ProgressDialog.show(this, null, message);
        }
        mDlgProgress.show();
    }

    private void stopProgressBar() {
        if (mDlgProgress != null) {
            mDlgProgress.dismiss();
            mDlgProgress = null;
        }
    }
    
    private void showDialogError(String message) {
        mStateHolder.setError(message);
        showDialog(DIALOG_ERROR);
    }

    private static class StateHolder {
        
        private String mError;
        
        public StateHolder() {
        }
        
        public void setActivity(RecommendItActivity activity) {
        }
        
        public String getError() {
            return mError;
        }
        
        public void setError(String error) {
            mError = error;
        }
    
    }
    
    
    
    private boolean canEnableSaveButton() {
        return mNameEditText.getText().length() > 0;
    }


}
