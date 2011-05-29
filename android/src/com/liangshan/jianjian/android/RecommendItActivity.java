/**
 * 
 */
package com.liangshan.jianjian.android;


import java.net.URLEncoder;
import java.util.List;

import com.liangshan.jianjian.android.location.LocationUtils;
import com.liangshan.jianjian.general.Jianjian;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.RecommendMsg;
import com.liangshan.jianjian.types.Venue;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
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
    private LinearLayout mPickupVenueLayout;
    private ImageView mPickupVenueIcon;
    private TextView mPickupVenueTextView;
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
        mPickupVenueLayout = (LinearLayout) findViewById(R.id.pickupVenueLayout);
        mPickupVenueIcon = (ImageView) findViewById(R.id.pickupVenueIcon);
        mPickupVenueTextView = (TextView) findViewById(R.id.pickupVenueTextView);
        mPriceEditText = (EditText) findViewById(R.id.priceEditText);
        mCurrencySpinner = (Spinner) findViewById(R.id.currencySpinner);
        mRecommendDesEditText = (EditText) findViewById(R.id.recommendDesEditText);
        mTakePhotoImgButton = (ImageButton) findViewById(R.id.takePhotoImgButton);
        
        
        mPickupVenueLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_PICK_CATEGORY);
            }
        });
        mPickupVenueLayout.setEnabled(false);
        
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
    
    /**
     * @param recommdmsg
     * @param mReason
     */
    public void onAddandRecommendItTaskComplete(RecommendMsg recommdmsg, Exception mReason) {
        // TODO Auto-generated method stub
        
        mStateHolder.setIsRunningTaskAddandRecommendIt(false);
        stopProgressBar();
        
    }
    
    /**
     * @param venuelist
     * @param mReason
     */
    public void onGetVenueListTaskComplete(Group<Venue> venuelist, Exception mReason) {
        // TODO Auto-generated method stub
        
        mStateHolder.setIsRunningTaskGetVenueList(false);
        
    }
    
    /**
     * @param venuelist
     * @param mReason
     */
    public void onTakePhotoTaskComplete(Bitmap photo, Exception mReason) {
        // TODO Auto-generated method stub
        
        mStateHolder.setIsRunningTaskTakePhoto(false);
        
    }
    
    private static class AddandRecommendItTask extends AsyncTask<Void, Void, RecommendMsg> {

        private RecommendItActivity mActivity;
        private String[] mParams;
        private Exception mReason;
        private Jianjianroid mJianjianroid;
        private String mErrorMsgForRecommendIt;
        private String mSubmittingMsg;

        public AddandRecommendItTask(RecommendItActivity activity, 
                            String[] params) {
            mActivity = activity;
            mParams = params;
            mJianjianroid = (Jianjianroid) activity.getApplication();
            mErrorMsgForRecommendIt = activity.getResources().getString(
                    R.string.add_recommend_it_fail);
            mSubmittingMsg = activity.getResources().getString(
                    R.string.submitting_the_recommendation);
        }

        public void setActivity(RecommendItActivity activity) {
            mActivity = activity;
        }
        
        @Override
        protected void onPreExecute() {
            mActivity.startProgressBar(mSubmittingMsg);
        }

        @Override
        protected RecommendMsg doInBackground(Void... params) {
            try {
                Jianjian jianjian = mJianjianroid.getJianjian();
                Location location = mJianjianroid.getLastKnownLocationOrThrow();

                /*    return jianjian.addVenue(
                            mParams[0], // name
                            mParams[1], // address
                            mParams[2], // cross street
                            mParams[3], // city
                            mParams[4], // state,
                            mParams[5], // zip
                            mParams[6], // phone
                            mParams[7], // category id
                            LocationUtils.createJianjianLocation(location));
                */
                //LocationUtils.createJianjianLocation(location);
            } catch (Exception e) {
                Log.e(TAG, "Exception during recommend the product.", e);
                mReason = e;
            }
            
            return null;
        }

        @Override
        protected void onPostExecute(RecommendMsg recommdmsg) {
            if (DEBUG) Log.d(TAG, "onPostExecute()");
            if (mActivity != null) {
                mActivity.onAddandRecommendItTaskComplete(recommdmsg, mReason);
            }
        }

        @Override
        protected void onCancelled() {
            if (mActivity != null) {
                mActivity.onAddandRecommendItTaskComplete(null, mReason);
            }
        }
    }
    
    private static class GetVenueListTask extends AsyncTask<Void, Void, Group<Venue>> {

        private RecommendItActivity mActivity;
        private Exception mReason;

        public GetVenueListTask(RecommendItActivity activity) {
            mActivity = activity;
        }

        public void setActivity(RecommendItActivity activity) {
            mActivity = activity;
        }
        
        @Override
        protected void onPreExecute() {
            mActivity.setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Group<Venue> doInBackground(Void... params) {
            try {
                Jianjianroid mJianjianroid = (Jianjianroid) mActivity.getApplication();
                Jianjian jianjian = mJianjianroid.getJianjian();
                Location location = mJianjianroid.getLastKnownLocationOrThrow();
                int page=1;
                return jianjian.getVenuesByLocation(LocationUtils.createJianjianLocation(location),page);
            } catch (Exception e) {
                if (DEBUG)
                    Log.d(TAG, "GetVenueListTask: Exception doing get venue list request.", e);
                mReason = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Group<Venue> venuelist) {
            if (DEBUG) Log.d(TAG, "GetCategoriesTask: onPostExecute()");
            if (mActivity != null) {
                mActivity.onGetVenueListTaskComplete(venuelist, mReason);
            }
        }

        @Override
        protected void onCancelled() {
            if (mActivity != null) {
                mActivity.onGetVenueListTaskComplete(null,
                        new Exception("Get categories task request cancelled."));
            }
        }
    }
    
    private static class TakePhotoTask extends AsyncTask<Void, Void, Bitmap> {

        private RecommendItActivity mActivity;
        private Exception mReason;

        public TakePhotoTask(RecommendItActivity activity) {
            mActivity = activity;
        }

        public void setActivity(RecommendItActivity activity) {
            mActivity = activity;
        }
        
        @Override
        protected void onPreExecute() {
            mActivity.setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                Jianjianroid jianjianroid = (Jianjianroid) mActivity.getApplication();
                Jianjian jianjian = jianjianroid.getJianjian();
                return null;
            } catch (Exception e) {
                if (DEBUG)
                    Log.d(TAG, "TakePhotoTask: Exception doing taking photo.", e);
                mReason = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap photoTaken) {
            if (DEBUG) Log.d(TAG, "GetCategoriesTask: onPostExecute()");
            if (mActivity != null) {
                mActivity.onTakePhotoTaskComplete(photoTaken, mReason);
            }
        }

        @Override
        protected void onCancelled() {
            if (mActivity != null) {
                mActivity.onTakePhotoTaskComplete(null,
                        new Exception("Get categories task request cancelled."));
            }
        }
    }

    private static class StateHolder {
        
        private boolean mIsRunningTaskAddandRecommendIt;
        private boolean mIsRunningTaskGetVenueList;
        private boolean mIsRunningTaskTakePhoto;
        
        private String mError;
        
        public StateHolder() {
        }
        
        public void setActivity(RecommendItActivity activity) {
        }
        
        public void setIsRunningTaskAddandRecommendIt(boolean isRunning) {
            mIsRunningTaskAddandRecommendIt = isRunning;
        }
        
        public boolean getIsRunningTaskAddandRecommendIt() {
            return mIsRunningTaskAddandRecommendIt;
        }
        
        public void setIsRunningTaskGetVenueList(boolean isRunning) {
            mIsRunningTaskGetVenueList = isRunning;
        }
        
        public boolean getIsRunningTaskGetVenueList() {
            return mIsRunningTaskGetVenueList;
        }
        
        public void setIsRunningTaskTakePhoto(boolean isRunning) {
            mIsRunningTaskTakePhoto = isRunning;
        }
        
        public boolean getIsRunningTaskTakePhoto() {
            return mIsRunningTaskTakePhoto;
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
