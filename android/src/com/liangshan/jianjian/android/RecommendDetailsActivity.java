/**
 * 
 */
package com.liangshan.jianjian.android;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import com.liangshan.jianjian.android.util.NotificationsUtil;
import com.liangshan.jianjian.android.util.RemoteResourceManager;
import com.liangshan.jianjian.android.util.StringFormatters;
import com.liangshan.jianjian.android.widget.CommentsListAdapter;
import com.liangshan.jianjian.general.Jianjian;
import com.liangshan.jianjian.types.Comment;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.Product;
import com.liangshan.jianjian.types.RecommendMsg;
import com.liangshan.jianjian.types.User;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author jornel
 *
 */
public class RecommendDetailsActivity extends ListActivity {
    private static final String TAG = "RecommendDetailsActivity";

    private static final boolean DEBUG = JianjianSettings.DEBUG;
    
    public static final String EXTRA_RecommendMsg_PARCEL = Jianjianroid.PACKAGE_NAME
    + ".RecommendDetailsActivity.EXTRA_RecommendMsg_PARCEL";
    
    private StateHolder mStateHolder;
    private RemoteResourceManager mRrm;
    private RemoteResourceManagerObserver mResourcesObserver;
    private Handler mHandler;
    private CommentsListAdapter mListAdapter;
    
    
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
                mStateHolder.setRecUser(mStateHolder.getRecommendMsg().getFromUser());
                mStateHolder.setProduct(mStateHolder.getRecommendMsg().getProduct());
                mStateHolder.startTaskShowComments(this);
                
            } else {
                Log.i(TAG, "Starting " + TAG + " as default recommend msg.");
                RecommendMsg recommend = new RecommendMsg();
                mStateHolder.setRecommendMsg(recommend);
            }
        }

        mHandler = new Handler();
        mRrm = ((Jianjianroid) getApplication()).getRemoteResourceManager();
        mResourcesObserver = new RemoteResourceManagerObserver();
        mRrm.addObserver(mResourcesObserver);
        
        ensureUi();
        
        
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        if (isFinishing()) {
            mStateHolder.cancelTasks();
            mHandler.removeCallbacks(mRunnableUpdateUserPhoto);

            RemoteResourceManager rrm = ((Jianjianroid) getApplication()).getRemoteResourceManager();
            rrm.deleteObserver(mResourcesObserver);
            
        }
    }
    
    @Override 
    protected void onDestroy() {
        super.onDestroy();
        
        unregisterReceiver(mLoggedOutReceiver);
    }
    /**
     * 
     */
    private void ensureUi() {
        
        TextView tvUsername = (TextView)findViewById(R.id.recommendDetailsActivityUsername);
        TextView tvProduct = (TextView)findViewById(R.id.recommendDetailsActivityProduct);
        TextView tvVenue = (TextView)findViewById(R.id.recommendDetailsActivityGeneralVenueValue);
        TextView tvPrice = (TextView)findViewById(R.id.recommendDetailsActivityPrice);
        TextView tvDate = (TextView)findViewById(R.id.recommendDetailsActivityDate);
        TextView tvDescription = (TextView)findViewById(R.id.recommendDetailsActivityDescription);
        RelativeLayout rlVenue = (RelativeLayout)findViewById(R.id.recommendDetailsActivityVenue);
        
        
        
        
        Button btCommentConfirmButton = (Button)findViewById(R.id.commentConfirmButton);
        
        btCommentConfirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etCommentEdit = (EditText)findViewById(R.id.commentEditText);
                String commentBody = etCommentEdit.getText().toString();
                if(commentBody != null ||commentBody != ""){
                    mStateHolder.startTaskSendComment(RecommendDetailsActivity.this, commentBody);
                    mStateHolder.setIsRunningSendCommentTask(true);
                }
                
            }
        });
        
        
        User user = mStateHolder.getRecUser();
        Product product = mStateHolder.getProduct();
        if(user.getNick()!=null){
            tvUsername.setText(user.getNick());
        }
        if(product!= null&& product.getName()!=null){
            tvProduct.setText(product.getName());
        }
        if(product.getVenue()!=null){
            tvVenue.setText(product.getVenue().getName());
            rlVenue.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    String lat = mStateHolder.getProduct().getVenue().getGeolat();
                    String lon = mStateHolder.getProduct().getVenue().getGeolong();
                    Intent i = new Intent( 
                            Intent.ACTION_VIEW, 
                            Uri.parse("http://ditu.google.cn/maps?hl=zh&mrt=loc&q="+lat+","+lon)); 
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
                            & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
                    i.setClassName("com.google.android.apps.maps", 
                            "com.google.android.maps.MapsActivity"); 
                    startActivity(i);
                }
                
            });
        }else{
            rlVenue.setVisibility(View.GONE);
            tvVenue.setVisibility(View.GONE);
        }
        String price = mStateHolder.getRecommendMsg().getPrice();
        String price2 = getString(R.string.hint_product_currency);
        if(price != null && price != ""&& !price.trim().equals(price2)){
            tvPrice.setText(price);
        }else{
            tvPrice.setVisibility(View.GONE);
        }
        String date = mStateHolder.getRecommendMsg().getCreateDate();
        if(date != null){
            tvDate.setText(StringFormatters.getOlderTimeString(date));
        }else{
            tvDate.setVisibility(View.GONE);
        }
        String description = mStateHolder.getRecommendMsg().getDescription();
        if(description != null){
            tvDescription.setText(description);
        }else{
            tvDescription.setVisibility(View.GONE);
        }
        
        LinearLayout llComment = (LinearLayout)findViewById(R.id.commentProgress);
        
        llComment.setVisibility(View.VISIBLE);
        
        
        ensureUiPhoto();
       
        
    }
    
    /**
     * @param user
     */
    protected void ensureUiPhoto() {
        ImageView ivUserPhoto = (ImageView)findViewById(R.id.recommendDetailsActivityPhoto);
        
        User user = mStateHolder.getRecommendMsg().getFromUser();
        
        if (user == null || user.getPhoto() == null || user.getPhoto()=="") {
            ivUserPhoto.setImageResource(R.drawable.blank_boy);
            ensureUiProductPhoto();
            return;
        }
        
        Uri uriPhoto = Uri.parse(user.getPhoto());
        
        if (mRrm.exists(uriPhoto)) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(mRrm.getInputStream(Uri.parse(user
                        .getPhoto())));
                ivUserPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                setUserPhotoMissing(ivUserPhoto, user);
            }
        } else {
            mRrm.request(uriPhoto);
            setUserPhotoMissing(ivUserPhoto, user);
        }
        
        ivUserPhoto.postInvalidate();
        ensureUiProductPhoto();

    }
    
    protected void ensureUiProductPhoto() {
        ImageView ivRecommendImage = (ImageView)findViewById(R.id.recommendImage);
        
        if(mStateHolder.getRecommendMsg().getPhoto()==null 
                || mStateHolder.getRecommendMsg().getPhoto().get(0) == null
                || mStateHolder.getRecommendMsg().getPhoto().get(0) == ""){
            ivRecommendImage.setVisibility(View.GONE);
            return;
        }
        String image = mStateHolder.getRecommendMsg().getPhoto().get(0);
        
        Uri uriImage = Uri.parse(image);
        
        if (mRrm.exists(uriImage)) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(mRrm.getInputStream(uriImage));
                ivRecommendImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                ivRecommendImage.setVisibility(View.GONE);
            }
        } else {
            mRrm.request(uriImage);
            
        }
        
        ivRecommendImage.postInvalidate();
        
    }
    

    @Override
    public Object onRetainNonConfigurationInstance() {
        mStateHolder.setActivityForTasks(null);
        return mStateHolder;
    }
    
    private void setUserPhotoMissing(ImageView ivPhoto, User user) {
        if ("male".equals(user.getGender())||"1".equals(user.getGender())) {
            ivPhoto.setImageResource(R.drawable.blank_boy);
        } else {
            ivPhoto.setImageResource(R.drawable.blank_girl);
        }
    }
    
    private class RemoteResourceManagerObserver implements Observer {
        @Override
        public void update(Observable observable, Object data) {
            mHandler.post(mRunnableUpdateUserPhoto);
        }
    }
    
    private Runnable mRunnableUpdateUserPhoto = new Runnable() {
        @Override 
        public void run() {
            ensureUiPhoto();
        }
    };
    
    /**
     * @param friends
     * @param mReason
     */
    public void onShowCommentsTaskComplete(Group<Comment> comments, Exception ex) {
        
        LinearLayout llComment = (LinearLayout)findViewById(R.id.commentProgress);
        
        llComment.setVisibility(View.GONE);
        
        TextView tvEmptyComment = (TextView)findViewById(R.id.emptyComment);
        LinearLayout tvListview = (LinearLayout)findViewById(R.id.commentlist);
        tvListview.setVisibility(View.VISIBLE);
        if(mListAdapter != null){
            mListAdapter.removeObserver();
            mListAdapter.clear();
        }

        mListAdapter = new CommentsListAdapter(
                this, ((Jianjianroid) getApplication()).getRemoteResourceManager());
        
        if(comments != null&&!comments.isEmpty()){
            mStateHolder.setComments(comments);
            
            mListAdapter.setGroup(mStateHolder.getComments());
            
            ListView listView = getListView();
            listView.setAdapter(mListAdapter);
            tvEmptyComment.setVisibility(View.GONE);
            
            
        }else if(ex != null){
            tvEmptyComment.setVisibility(View.VISIBLE);
            NotificationsUtil.ToastReasonForFailure(this, ex);
        }else if(comments == null|| comments.size()==0){
            tvEmptyComment.setVisibility(View.VISIBLE);
        }
        
        getListView().setAdapter(mListAdapter);
        setListViewHeightBasedOnChildren(getListView());
        
        mStateHolder.setIsRunningShowCommentsTask(false);
        
    }
    
    /**
     * @param comment
     * @param mReason
     */
    public void onSendCommentsTaskComplete(Comment comment, Exception ex) {
        // TODO Auto-generated method stub
        if(ex!=null){
            //NotificationsUtil.ToastReasonForFailure(this, ex);
            Toast.makeText(this, getResources().getString(R.string.commit_failed),  
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.commit_finished),  
                    Toast.LENGTH_LONG).show();
            EditText etCommentEdit = (EditText)findViewById(R.id.commentEditText);
            etCommentEdit.setText("");
            
            mStateHolder.startTaskShowComments(this);
        }
        mStateHolder.setIsRunningSendCommentTask(false);
    }
    
    private void setListViewHeightBasedOnChildren(ListView listView) { 
        ListAdapter listAdapter = listView.getAdapter();  
        if (listAdapter == null) { 
            // pre-condition 
            return; 
        } 

        int totalHeight = 0; 
        for (int i = 0; i < listAdapter.getCount(); i++) { 
            View listItem = listAdapter.getView(i, null, listView); 
            listItem.measure(0, 0); 
            totalHeight += listItem.getMeasuredHeight(); 
        } 

        ViewGroup.LayoutParams params = listView.getLayoutParams(); 
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)); 
        listView.setLayoutParams(params); 
    } 
    
    private static class ShowCommentsTask extends AsyncTask<String, Void, Group<Comment>> {
        
        private RecommendDetailsActivity mActivity;
        private Exception mReason;

        public ShowCommentsTask(RecommendDetailsActivity activity) {
            mActivity = activity;
        }
        
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Group<Comment> doInBackground(String... params) {
            try {
                Jianjianroid jianjianroid = (Jianjianroid) mActivity.getApplication();
                Jianjian jianjian = jianjianroid.getJianjian();
                
                Group<Comment> comments = jianjian.commentlist(mActivity.mStateHolder.getRecommendMsg().getFragmentId());

                return comments;
            } catch (Exception e) {
                mReason = e;
            }
            return null;
        }


        @Override
        protected void onPostExecute(Group<Comment> comments) {
            if (mActivity != null) {
                mActivity.onShowCommentsTaskComplete(comments, mReason);
            }
        }

        @Override
        protected void onCancelled() {
            if (mActivity != null) {
                mActivity.onShowCommentsTaskComplete(null, mReason);
            }
        }
        
        public void setActivity(RecommendDetailsActivity activity) {
            mActivity = activity;
        }
    }
    
    private static class SendCommentTask extends AsyncTask<String, Void, Comment> {
        
        private RecommendDetailsActivity mActivity;
        private Exception mReason;
        private String mBody;

        public SendCommentTask(RecommendDetailsActivity activity, String body) {
            mActivity = activity;
            mBody = body;
        }
        
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Comment doInBackground(String... params) {
            try {
                Jianjianroid jianjianroid = (Jianjianroid) mActivity.getApplication();
                Jianjian jianjian = jianjianroid.getJianjian();
                
                Comment comment = jianjian.sendComment(mActivity.mStateHolder.getRecommendMsg().getFragmentId(),mBody);

                return comment;
            } catch (Exception e) {
                mReason = e;
            }
            return null;
        }


        @Override
        protected void onPostExecute(Comment comment) {
            if (mActivity != null) {
                mActivity.onSendCommentsTaskComplete(comment, mReason);
            }
        }

        @Override
        protected void onCancelled() {
            if (mActivity != null) {
                mActivity.onSendCommentsTaskComplete(null, mReason);
            }
        }
        
        public void setActivity(RecommendDetailsActivity activity) {
            mActivity = activity;
        }
    }
    
    private static class StateHolder {
        private RecommendMsg mRecommend;
        private User mRecUser;
        private Product mProduct;
        private ShowCommentsTask mTaskShowComments;
        private SendCommentTask mTaskSendComment;
        private boolean mIsRunningShowCommentsTask;
        private boolean mIsRunningSendCommentTask;
        private Group<Comment> mComments;
        
        public StateHolder() {
            mIsRunningShowCommentsTask = false;
            mIsRunningSendCommentTask = false;
            mRecommend = new RecommendMsg();
            mRecUser = new User();
            mProduct = new Product();
            
        }

        /**
         * @param recommendDetailsActivity
         */
        public void setActivityForTasks(RecommendDetailsActivity recommendDetailsActivity) {
            if (mTaskShowComments != null) {
                mTaskShowComments.setActivity(recommendDetailsActivity);
            }
            if (mTaskSendComment != null) {
                mTaskSendComment.setActivity(recommendDetailsActivity);
            }
            
        }
        
        public void startTaskShowComments(RecommendDetailsActivity activity) {
            if(mIsRunningShowCommentsTask != true){
                mIsRunningShowCommentsTask = true;
                mTaskShowComments = new ShowCommentsTask(activity);
                mTaskShowComments.execute();
            }
        }
        public void startTaskSendComment(RecommendDetailsActivity activity,String body) {
            if(mIsRunningSendCommentTask != true){
                mIsRunningSendCommentTask = true;
                mTaskSendComment = new SendCommentTask(activity,body);
                mTaskSendComment.execute();
            }
        }
        
        public Product getProduct() {
            return mProduct;            
        }

        /**
         * @param product
         */
        public void setProduct(Product product) {
            mProduct = product;            
        }
        
        public Group<Comment> getComments() {
            return mComments;            
        }

        /**
         * @param product
         */
        public void setComments(Group<Comment> comments) {
            mComments = comments;            
        }

        /**
         * @return
         */
        public User getRecUser() {
            
            return mRecUser;
        }
        public void setRecUser(User user) {
            mRecUser = user;
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
        
        public boolean getIsRunningShowCommentsTask() {
            return mIsRunningShowCommentsTask;
        }
        
        public void setIsRunningShowCommentsTask(boolean isRunning) {
            mIsRunningShowCommentsTask = isRunning;
        }
        
        public boolean getIsRunningSendCommentTask() {
            return mIsRunningSendCommentTask;
        }
        
        public void setIsRunningSendCommentTask(boolean isRunning) {
            mIsRunningSendCommentTask = isRunning;
        }
        public void cancelTasks() {
            if (mTaskShowComments != null) {
                mTaskShowComments.setActivity(null);
                mTaskShowComments.cancel(true);
            }
            if (mTaskSendComment != null) {
                mTaskSendComment.setActivity(null);
                mTaskSendComment.cancel(true);
            }
        }
        
    }







}
