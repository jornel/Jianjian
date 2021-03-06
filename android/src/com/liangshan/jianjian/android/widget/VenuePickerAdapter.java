
package com.liangshan.jianjian.android.widget;


import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liangshan.jianjian.android.JianjianSettings;
import com.liangshan.jianjian.android.R;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.Venue;

public class VenuePickerAdapter extends BaseAdapter {

    private static final String TAG = "CheckinListAdapter";
    private static final boolean DEBUG = JianjianSettings.DEBUG;

    private LayoutInflater mInflater;
    private int mLayoutToInflate;
    private Group<Venue> mVenueList;

    public VenuePickerAdapter(Context context, Group<Venue> venuelist) {
        super();
        mVenueList = venuelist;
        mInflater = LayoutInflater.from(context);
        mLayoutToInflate = R.layout.venue_picker_list_item;

    }
    

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary
        // calls to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no
        // need to re-inflate it. We only inflate a new View when the
        // convertView supplied by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutToInflate, null);

            // Creates a ViewHolder and store references to the two children
            // views we want to bind data to.
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.venuePickerIcon);
            holder.name = (TextView) convertView.findViewById(R.id.venuePickerName);

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        Venue venue = (Venue) getItem(position);

        holder.name.setText(venue.getName());

        return convertView;
    }


    private static class ViewHolder {
        ImageView icon;
        TextView name;
    }

    @Override
    public int getCount() {
        return mVenueList.size();
    }

    @Override
    public Object getItem(int position) {
        return mVenueList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    /**
     * @param mVenueList2
     */
    public void updateVenueList(Group<Venue> newVenuelist) {
        mVenueList = newVenuelist;
        
    }

}
