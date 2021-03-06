/**
 * 
 */
package com.liangshan.jianjian.android;

import java.io.IOException;

import com.liangshan.jianjian.android.error.JianjianError;
import com.liangshan.jianjian.android.error.JianjianException;
import com.liangshan.jianjian.android.error.LocationException;
import com.liangshan.jianjian.android.location.LocationUtils;
import com.liangshan.jianjian.android.widget.VenuePickerAdapter;
import com.liangshan.jianjian.general.Jianjian;
import com.liangshan.jianjian.general.Jianjian.JLocation;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.Venue;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author ezhuche
 *
 */
public class VenuePickerDialog extends Dialog {
    
    private static final String TAG = "VenuePickerDialog";
    private static final boolean DEBUG = JianjianSettings.DEBUG;
    private Jianjianroid mApplication;
    private Group<Venue> mVenuelist;    
    private Venue mChosenVenue;
    private ViewFlipper mViewFlipper;
    private int mFirstDialogHeight;
    
    /**
     * @param context
     */
    public VenuePickerDialog(Context context, Group<Venue> venuelist, Jianjianroid application) {
        super(context);
        mApplication = application;
        mVenuelist = venuelist;
        mChosenVenue = null;
        
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.venue_picker_dialog);
        setTitle(getContext().getResources().getString(R.string.venue_picker_dialog_title));
        
        mViewFlipper = (ViewFlipper) findViewById(R.id.venuePickerViewFlipper);
        mFirstDialogHeight = -1;
        
        mViewFlipper.addView(makePage(mVenuelist));
               
        
    }
    
    /**
     * After the user has dismissed the dialog, the parent activity can use this
     * to see which category they picked, if any. Will return null if no
     * category was picked.
     */
    public Venue getChosenVenue() {
        return mChosenVenue;
    }

    /**
     * @param mVenues
     * @return
     */
    private View makePage(Group<Venue> venuelist) {
        
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.venue_picker_page, null);
        
        VenuePickerPage page = new VenuePickerPage();
        page.setApplication(mApplication);
        page.ensureUI(view, mPageListItemSelected, venuelist);
        view.setTag(page);
        
        if (mViewFlipper.getChildCount() == 1 && mFirstDialogHeight == -1) {
            mFirstDialogHeight = mViewFlipper.getChildAt(0).getHeight();
        }
        if (mViewFlipper.getChildCount() > 0) {
            view.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, mFirstDialogHeight));
        }
        
        return view;
    }
    
    private static class VenuePickerPage {
        protected static final long mSleepTimeInMs = 3000L;
        private VenuePickerAdapter mListAdapter;
        private Group<Venue> mVenueList;
        private PageListItemSelected mClickListener;
        private Jianjianroid mJianjianroid;
        private Jianjian mJianjian;
        private Group<Venue> mMoreVenueList;
        private LinearLayout footerview;
        private int venuePage;
        
        public VenuePickerPage(){
            venuePage = 1;
        }
        
        

        /**
         * @param view
         * @param mPageListItemSelected
         * @param venuelist
         * @param remoteResourceManager
         */
        public void ensureUI(View view, PageListItemSelected clickListener,
                Group<Venue> venuelist) {
            mVenueList = venuelist;
            mClickListener = clickListener;
            mListAdapter = new VenuePickerAdapter(view.getContext(), mVenueList);
            ListView listview = (ListView) view.findViewById(R.id.venuePickerListView);
            //TextView txt=new TextView(view.getContext());
            //txt.setText("Show MORE");
            //listview.addFooterView(txt);
            //LinearLayout footerview = (LinearLayout) view.findViewById(R.id.venueloadingMoreVenueButton);  
            //TextView footerview = (TextView) view.findViewById(R.id.venueMorePickerName);
            footerview = (LinearLayout) LayoutInflater.from(
                    view.getContext()).inflate(R.layout.venue_list_footer,null);
            
            footerview.setClickable(true);
            footerview.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                                                                                                
                    if(mVenueList.isHasMore()){
                        venuePage+=1;
                    }
                    //Location location = mJianjianroid.getLastKnownLocation();
                    try {
                        updateVenueListByPage(venuePage);
                        /*Location location = mJianjianroid.getLastKnownLocationOrThrow();
                        //mMoreVenueList = jianjian.getVenuesByLocation(new JLocation("31.220302","121.351007",null,null,null),venuePage);
                        mMoreVenueList = mJianjian.getVenuesByLocation(LocationUtils.createJianjianLocation(location),venuePage);
                        if(mVenueList.addAll(mMoreVenueList)){
                            mVenueList.setHasMore(mMoreVenueList.isHasMore());
                        }
                        mListAdapter.updateVenueList(mVenueList);                        
                        mListAdapter.notifyDataSetChanged();
                        if(!mVenueList.isHasMore()){
                            footerview.setVisibility(View.GONE);
                        }*/
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                }
            });
            if(!mVenueList.isHasMore()){
                footerview.setVisibility(View.GONE);
            }
            listview.addFooterView(footerview);
            listview.setAdapter(mListAdapter);
            listview.setOnItemClickListener(mOnItemClickListener);


            LinearLayout llRootCategory = (LinearLayout) view
            .findViewById(R.id.venuePickerRefreshVenueButton);
            llRootCategory.setClickable(true);
            llRootCategory.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    
                    mJianjianroid.clearLastKnownLocation();
                    mJianjianroid.removeLocationUpdates();
                    mJianjianroid.requestLocationUpdates(true);
                    try{
                        Thread.sleep(mSleepTimeInMs);
                        
                        updateVenueListByPage(1);
                        /*
                        Location location = mJianjianroid.getLastKnownLocationOrThrow();
                        mVenueList = mJianjian.getVenuesByLocation(LocationUtils.createJianjianLocation(location),1);
                        mListAdapter.updateVenueList(mVenueList);
                        mListAdapter.notifyDataSetChanged();
                        if(!mVenueList.isHasMore()){
                            footerview.setVisibility(View.GONE);
                        }*/
                        
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    
                }
            });            
            //llRootCategory.setVisibility(View.GONE);
                       
        }
        
        /**
         * @param mApplication
         */
        public void setApplication(Jianjianroid mApplication) {
            mJianjianroid = mApplication;
            mJianjian = mJianjianroid.getJianjian();
            
        }
        
        private void updateVenueListByPage(int page)throws Exception {
            Group<Venue> venues;
            Location location = mJianjianroid.getLastKnownLocationOrThrow();
            venues = mJianjian.getVenuesByLocation(LocationUtils.createJianjianLocation(location),page);
            if(page > 1){
                if(mVenueList.addAll(venues)){
                    mVenueList.setHasMore(venues.isHasMore());
                }
            }else if(page == 1){
                mVenueList = venues;
            }
            mListAdapter.updateVenueList(mVenueList);
            mListAdapter.notifyDataSetChanged();
            if(!mVenueList.isHasMore()){
                footerview.setVisibility(View.GONE);
            }
            
        }

        private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                mClickListener.onPageListItemSelcected((Venue) mListAdapter.getItem(position));
            }
        };
        
    }
    
    private PageListItemSelected mPageListItemSelected = new PageListItemSelected() {
        @Override
        public void onPageListItemSelcected(Venue venue) {
                // This is a leaf node, finally the user's selection. Record the
                // category
                // then cancel ourselves, parent activity should pick us up
                // after that.
                mChosenVenue = venue;
                cancel();
        }

        @Override
        public void onCategorySelected(Venue venue) {
            // The user has chosen the category parent listed at the top of the
            // current page.
            mChosenVenue = venue;
            cancel();
        }
    };

    private interface PageListItemSelected {
        public void onPageListItemSelcected(Venue venue);

        public void onCategorySelected(Venue category);
    }



}
