/**
 * 
 */
package com.liangshan.jianjian.android;


import com.joelapenna.foursquared.R;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.Venue;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ViewFlipper;

/**
 * @author ezhuche
 *
 */
public class VenuePickerDialog extends Dialog {
    
    private static final String TAG = "VenuePickerDialog";
    private static final boolean DEBUG = JianjianSettings.DEBUG;
    private Jianjianroid mApplication;
    private Group<Venue> mVenues;    
    private Venue mChosenVenue;
    private ViewFlipper mViewFlipper;

    /**
     * @param context
     */
    public VenuePickerDialog(Context context, Group<Venue> venuelist, Jianjianroid application) {
        super(context);
        mApplication = application;
        mVenues = venuelist;
        mChosenVenue = null;
        
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.venue_picker_dialog);
    }



}
