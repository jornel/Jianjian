/**
 * 
 */
package com.liangshan.jianjian.android;


import java.io.File;

import com.liangshan.jianjian.android.location.LocationUtils;
import com.liangshan.jianjian.android.util.ImageUtil;
import com.liangshan.jianjian.android.util.NotificationsUtil;
import com.liangshan.jianjian.general.Jianjian;

import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.RecommendMsg;
import com.liangshan.jianjian.types.Venue;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author ezhuche
 *
 */
public class RecommendItActivity extends Activity {
    
    static final String TAG = "RecommendItActivity";
    static final boolean DEBUG = JianjianSettings.DEBUG;
    private static final int DIALOG_PICK_CATEGORY = 1;
    private static final int DIALOG_ERROR = 2;
    protected static final int TAKE_PHOTO_WITH_DATA = 100;
    protected static final int mSleepTimeInSec = 20;
    
    private StateHolder mStateHolder;
    
    private Button mRecommendItButton;
    private EditText mNameEditText;
    private LinearLayout mPickupVenueLayout;
    private ImageView mPickupVenueIcon;
    private TextView mPickupVenueTextView;
    private EditText mPriceEditText;
    private TextView mCurrencyText;
    //private Spinner mCurrencySpinner;
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
        mCurrencyText = (TextView) findViewById(R.id.currencyText);
        //mCurrencySpinner = (Spinner) findViewById(R.id.currencySpinner);
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
                //temporary price
                price = price + mCurrencyText.getText().toString();
                //String currency = mCurrencySpinner.getSelectedItem().toString();
                String recommendDes = mRecommendDesEditText.getText().toString();
                //Bitmap bitmapPhoto = mTakePhotoImgButton.getDrawingCache();
                File mPhotoFile = null;
                
                if(mStateHolder != null&&mStateHolder.getPhotoBitmap()!= null){
                    mPhotoFile = ImageUtil.getBitmapFile(mStateHolder.getPhotoBitmap());
                }

                Venue chosenVenue = mStateHolder.getChosenVenue();
                if (TextUtils.isEmpty(productName)) {
                    showDialogError(getResources().getString(
                            R.string.recommend_it_activity_error_no_product_name));
                    return;
                } else if (chosenVenue == null) {
                    showDialogError(getResources().getString(
                            R.string.recommend_it_activity_error_no_chosen_venue));
                    return;
                }
                
                
                
                mStateHolder.startTaskAddandRecommendIt(
                        RecommendItActivity.this, 
                        new String[]{
                                productName,
                                price,
                                recommendDes,
                                chosenVenue.getId()
                        },
                        mPhotoFile != null? mPhotoFile:null);
                
            }
        });
        
        mTakePhotoImgButton.setImageDrawable(getResources().getDrawable(R.drawable.addphoto_checkin));
                //getResources().getDrawable(R.drawable.addphoto_checkin
        mTakePhotoImgButton.setEnabled(true);
        mTakePhotoImgButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                
                mTakePhotoImgButton.setImageDrawable(getResources().getDrawable(R.drawable.addphoto_checkin));
                mStateHolder.setPhotoBitmap(null);
                Intent intent = new Intent(RecommendItActivity.this, TakePhotoActivity.class);
                startActivityForResult(intent,TAKE_PHOTO_WITH_DATA);
            }
            
        });
        
        mNameEditText.addTextChangedListener(mNameFieldWatcher);
        Object retained = getLastNonConfigurationInstance();
        
        
        if (retained != null && retained instanceof StateHolder) {
            mStateHolder = (StateHolder) retained;
            mStateHolder.setActivity(this);
            mPickupVenueLayout.setEnabled(true);
            if(mStateHolder.getPhotoBitmap() != null){
                mTakePhotoImgButton.setImageBitmap(mStateHolder.getPhotoBitmap());
            }
            
        } else {
            mStateHolder = new StateHolder();
            mPickupVenueTextView.setText(getResources().getString(R.string.loading_venue_product));
            mStateHolder.startTaskGetVenueList(this);            
            
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Jianjianroid mJianjianroid = (Jianjianroid) this.getApplication();
        
        mJianjianroid.requestLocationUpdates(true);
        
        if(!mStateHolder.getIsRunningTaskGetVenueList()&&!mPickupVenueLayout.isEnabled()){
            mStateHolder.startTaskGetVenueList(this); 
        } 

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
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode != RESULT_OK)  {
            return;  
        }       
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {  
            case TAKE_PHOTO_WITH_DATA: {
                mTakePhotoImgButton.setScaleType(ImageView.ScaleType.FIT_XY);
                Bitmap returned_image = (Bitmap) data.getParcelableExtra(TakePhotoActivity.EXTRA_PHOTO_RETURNED);
                mTakePhotoImgButton.setImageBitmap(returned_image);
                mStateHolder.setPhotoBitmap(returned_image);
            }  
        }  
    }  
    
    /*
    private boolean loadLocationinSeconds(int n){
        Jianjianroid mJianjianroid = (Jianjianroid) this.getApplication();
        Boolean hasLocation = false;
        Location lastLocation = mJianjianroid.getLastKnownLocation();
        if(lastLocation == null){
            try{               
                //check the lastLocation each second
                for(int i=0;i<n;++i){
                    //startProgressBar("loading current location...");
                    Thread.sleep(1000L);
                    if(n%5==0){
                        Toast.makeText(this, n+" seconds passed. No location found",  
                                Toast.LENGTH_LONG).show();
                    }
                    if(mJianjianroid.getLastKnownLocation()!= null){
                        hasLocation = true; 
                        break;
                    }
                }
                stopProgressBar();
            }catch(Exception e){
                NotificationsUtil.ToastReasonForFailure(this, e);
            }
        }else{
            hasLocation = true;
        }
        return hasLocation;
    }*/
    
    
    
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
     * @param recommendmsg
     * @param mReason
     */
    public void onAddandRecommendItTaskComplete(RecommendMsg recommendmsg, Exception mReason) {
        
        
        mStateHolder.setIsRunningTaskAddandRecommendIt(false);
        stopProgressBar();
        if(recommendmsg != null){
            Toast.makeText(this, getResources().getString(R.string.recommend_finished),  
                    Toast.LENGTH_LONG).show(); //NotificationsUtil.ToastReasonForFailure(this,new Exception("recommend finished"));
        }
        
        
    }
    
    /**
     * @param venuelist
     * @param mReason
     */
    public void onGetVenueListTaskComplete(Group<Venue> venuelist, Exception mReason) {
        
        
        mStateHolder.setIsRunningTaskGetVenueList(false);
        
        if(venuelist != null){
            mStateHolder.setVenueList(venuelist);
            mPickupVenueLayout.setEnabled(true);
            mPickupVenueTextView.setText(getResources().getString(R.string.pickup_venue_product));
            
        } else {
            mStateHolder.setVenueList(new Group<Venue>());
            NotificationsUtil.ToastReasonForFailure(this, mReason);
        }
        
        stopIndeterminateProgressBar();
        
    }
    
    /**
     * 
     */
    private void stopIndeterminateProgressBar() {
        // TODO Auto-generated method stub
        if (mStateHolder.getIsRunningTaskTakePhoto() == false &&
                mStateHolder.getIsRunningTaskGetVenueList() == false) {
                setProgressBarIndeterminateVisibility(false);
            }
    }

    /**
     * @param venuelist
     * @param mReason
     */
    public void onTakePhotoTaskComplete(Bitmap photo, Exception mReason) {
        // TODO Auto-generated method stub
        
        mStateHolder.setIsRunningTaskTakePhoto(false);
        
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
                Location location = null;
                
                //if(loadLocationinSeconds(mSleepTimeInSec,mJianjianroid)){
                Thread.sleep(1000L);
                location = mJianjianroid.getLastKnownLocationOrThrow();         
                //}else{
                //    NotificationsUtil.ToastReasonForFailure(mActivity, new LocationException());
                //}
                
                //Location location = mJianjianroid.getLastKnownLocationOrThrow();
                int page=1;
                //Location location = mJianjianroid.getLastKnownLocation();
                //return jianjian.getVenuesByLocation(new JLocation("31.220302","121.351007",null,null,null),page);
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
                        new Exception("Get Venue List task request cancelled."));
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
    
    private static class AddandRecommendItTask extends AsyncTask<Void, Void, RecommendMsg> {

        private RecommendItActivity mActivity;
        private String[] mParams;
        private File mPhotoFile;
        private Exception mReason;
        private Jianjianroid mJianjianroid;
        private String mErrorMsgForRecommendIt;
        private String mSubmittingMsg;

        public AddandRecommendItTask(RecommendItActivity activity, 
                            String[] params,File photoFile) {
            mActivity = activity;
            mParams = params;
            mPhotoFile = photoFile;
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
                String loginName = mJianjianroid.getLoginName();
                String password = mJianjianroid.getPassword();

                return jianjian.recommendItToAllFriends(
                        mParams[0], // productName
                        mParams[1], // price
                        mParams[2], // recommendDes
                        mParams[3], // VenueId
                        mPhotoFile,     // Photo,
                        loginName,
                        password,
                        LocationUtils.createJianjianLocation(location));
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

    private static class StateHolder {
        
        private boolean mIsRunningTaskAddandRecommendIt;
        private boolean mIsRunningTaskGetVenueList;
        private boolean mIsRunningTaskTakePhoto;
        private Group<Venue> mVenueList;
        private String mError;
        private GetVenueListTask mTaskGetVenueList;
        private TakePhotoTask mTaskTakePhoto;
        private AddandRecommendItTask mTaskAddandRecommendIt;
        private Venue mChosenVenue;
        private Bitmap mPhoto;
        
        public StateHolder() {
            mVenueList = new Group<Venue>();
            mIsRunningTaskAddandRecommendIt = false;
            mIsRunningTaskGetVenueList = false;
            mIsRunningTaskTakePhoto = false;
            
        }
        
        public void setActivity(RecommendItActivity activity) {
            if (mTaskGetVenueList != null) {
                mTaskGetVenueList.setActivity(activity);
            }
            if (mTaskTakePhoto != null) {
                mTaskTakePhoto.setActivity(activity);
            }
            if (mTaskAddandRecommendIt != null) {
                mTaskAddandRecommendIt.setActivity(activity);
            }
        }
        
        public Group<Venue> getVenueList() {
            return mVenueList;
        }
        
        public void setVenueList(Group<Venue> venuelist) {
            mVenueList = venuelist;
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
        
        public void startTaskGetVenueList(RecommendItActivity activity) {
            mIsRunningTaskGetVenueList = true;
            mTaskGetVenueList = new GetVenueListTask(activity);
            mTaskGetVenueList.execute();
        }
        
        public void startTaskTakePhoto(RecommendItActivity activity) {
            mIsRunningTaskTakePhoto = true;
            mTaskTakePhoto = new TakePhotoTask(activity);
            mTaskTakePhoto.execute();
        }
        
        public void startTaskAddandRecommendIt(RecommendItActivity activity,String[] params,File photoFile) {
            mIsRunningTaskAddandRecommendIt = true;
            mTaskAddandRecommendIt = new AddandRecommendItTask(activity, params, photoFile);
            mTaskAddandRecommendIt.execute();
        }
        
        public Venue getChosenVenue() {
            return mChosenVenue;
        }
        
        public void setChosenVenu(Venue venue) {
            mChosenVenue = venue;
        }
        
        public Bitmap getPhotoBitmap() {
            return mPhoto;
        }
        
        public void setPhotoBitmap(Bitmap photo) {
            mPhoto = photo;
        }
        
    
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_PICK_CATEGORY:
                // When the user cancels the dialog (by hitting the 'back' key), we
                // finish this activity. We don't listen to onDismiss() for this
                // action, because a device rotation will fire onDismiss(), and our
                // dialog would not be re-displayed after the rotation is complete.
                VenuePickerDialog dlg = new VenuePickerDialog(
                    this, 
                    mStateHolder.getVenueList(), 
                    ((Jianjianroid)getApplication()));
                dlg.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        VenuePickerDialog dlg = (VenuePickerDialog)dialog;
                        setChosenVenueView(dlg.getChosenVenue());
                        removeDialog(DIALOG_PICK_CATEGORY);
                    }
                });
                return dlg;
                
            case DIALOG_ERROR:
                AlertDialog dlgInfo = new AlertDialog.Builder(this)
                    .setIcon(0)
                    .setTitle(getResources().getString(R.string.recommendit_progress_bar_title_recommendit))
                    .setMessage(mStateHolder.getError()).create();
                dlgInfo.setOnDismissListener(new OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        removeDialog(DIALOG_ERROR);
                    }
                });
            
            return dlgInfo;
        }
        return null;
    }
    
    
    
    /**
     * @param chosenVenue
     */
    protected void setChosenVenueView(Venue venue) {
        
        if(venue == null){
            mPickupVenueTextView.setText(getResources().getString(R.string.pickup_venue_product));
            return;
        }
        
        mPickupVenueTextView.setText(venue.getName());
        
        mStateHolder.setChosenVenu(venue);
        
        if (canEnableSaveButton()) {
            mRecommendItButton.setEnabled(canEnableSaveButton());
        }
        
        
    }

    private boolean canEnableSaveButton() {
        return mNameEditText.getText().length() > 0;
    }






}
