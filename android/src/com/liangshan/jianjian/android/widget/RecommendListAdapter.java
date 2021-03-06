/**
 * Copyright 
 */

package com.liangshan.jianjian.android.widget;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import com.liangshan.jianjian.android.R;
import com.liangshan.jianjian.android.util.EventTimestampSort;
import com.liangshan.jianjian.android.util.RemoteResourceManager;
import com.liangshan.jianjian.android.util.StringFormatters;
import com.liangshan.jianjian.general.Jianjian;
import com.liangshan.jianjian.types.Event;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.RecommendMsg;
import com.liangshan.jianjian.types.User;
import com.liangshan.jianjian.util.UiUtil;

/**
 *   -Added local hashmap of cached timestamps processed at setGroup()
 *    time to conform to the same timestamp conventions other foursquare
 *    apps are using.
 */
public class RecommendListAdapter extends BaseRecommendAdapter implements ObservableAdapter {

    private LayoutInflater mInflater;

    private RemoteResourceManager mRrm;
    private RemoteResourceManagerObserver mResourcesObserver;
    private Handler mHandler = new Handler();
    private HashMap<String, String> mCachedTimestamps;
    private boolean mIsSdk3;
    private DateFormat df;
    
    
    public RecommendListAdapter(Context context, RemoteResourceManager rrm) {
        super(context);
        mInflater = LayoutInflater.from(context);
        mRrm = rrm;
        mResourcesObserver = new RemoteResourceManagerObserver();
        mCachedTimestamps = new HashMap<String, String>();

        mRrm.addObserver(mResourcesObserver);
        df = DateFormat.getDateInstance();
        
        mIsSdk3 = UiUtil.sdkVersion() == 3;
    }
    
    public void removeObserver() {
        mHandler.removeCallbacks(mUpdatePhotos);
        mRrm.deleteObserver(mResourcesObserver);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary
        // calls to findViewById() on each row.
        final ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no
        // need to re-inflate it. We only inflate a new View when the
        // convertView supplied by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.recommend_list_item, null);

            // Creates a ViewHolder and store references to the two children
            // views we want to bind data to.
            holder = new ViewHolder();
            holder.photo = (ImageView) convertView.findViewById(R.id.photo);
            holder.firstLine = (TextView) convertView.findViewById(R.id.firstLine);
            holder.secondLine = (TextView) convertView.findViewById(R.id.secondLine);
            holder.thirdLine = (TextView) convertView.findViewById(R.id.thirdLine);
            holder.timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);
            holder.commentnumTextView = (TextView) convertView.findViewById(R.id.commentnumText);
            
            
            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        RecommendMsg recommend = (RecommendMsg) getItem(position);
        final User user = recommend.getFromUser();
         

        try {
            Uri photoUri = Uri.parse(recommend.getPhoto().get(0));
            Bitmap bitmap = BitmapFactory.decodeStream(mRrm.getInputStream(photoUri));
            holder.photo.setImageBitmap(bitmap);
        } catch (Exception e) {
            if (Jianjian.FEMALE.equals(user.getGender())) {
                holder.photo.setImageResource(R.drawable.blank_girl);
                
            } else {
                holder.photo.setImageResource(R.drawable.blank_boy);
            }
        }
        
        Resources res = convertView.getContext().getResources();
        
        String recommendSymbol = res.getString(R.string.recommendSymbol);
        String venueSymbol = res.getString(R.string.venueSymbol);
        String currencySymbol = res.getString(R.string.hint_product_currency);

        String checkinMsgLine1 = StringFormatters.getRecommendMessageLine1(recommend, recommendSymbol);//某人 推荐了 某商品
        String checkinMsgLine2 = StringFormatters.getRecommendMessageLine2(recommend, venueSymbol,currencySymbol, true);//n 元，在某地
        String checkinMsgLine3 = StringFormatters.getRecommendMessageLine3(recommend);//描述
        String checkinMsgLine4 = mCachedTimestamps.get(recommend.getFragmentId());//推荐时间
        
        
        holder.firstLine.setText(checkinMsgLine1);
        if (!TextUtils.isEmpty(checkinMsgLine2)) {
            holder.secondLine.setVisibility(View.VISIBLE);
            holder.secondLine.setText(checkinMsgLine2);
        }else {
            if (!mIsSdk3) {
                holder.secondLine.setVisibility(View.GONE);
            } else {
                holder.secondLine.setVisibility(View.INVISIBLE);
            }
        }
        if (!TextUtils.isEmpty(checkinMsgLine3)) {
            holder.thirdLine.setVisibility(View.VISIBLE);
            holder.thirdLine.setText(checkinMsgLine3);            
        }else {
            holder.thirdLine.setVisibility(View.INVISIBLE);
        }
        holder.timeTextView.setText(checkinMsgLine4);
        if(recommend.getNumComments()!=0){
            holder.commentnumTextView.setText(Integer.toString(recommend.getNumComments()));
        } else {
            holder.commentnumTextView.setText("0");
        }
        return convertView;
    }

    @Override
    public void setGroup(Group<RecommendMsg> g) {
        super.setGroup(g);
        mCachedTimestamps.clear();

        EventTimestampSort timestamps = new EventTimestampSort();
        Date date;
        
        for (RecommendMsg it : g) {
            Uri photoUri;
            
            try {
                photoUri = Uri.parse(it.getPhoto().get(0));

            } catch (Exception e) {
                photoUri = Uri.parse(it.getFromUser().getPhoto());

            }
            if (!mRrm.exists(photoUri)) {
                mRrm.request(photoUri);
             }

            try {
                date = df.parse(it.getCreateDate());
                if (date.after(timestamps.getBoundaryRecent())) {
                    mCachedTimestamps.put(it.getFragmentId(), 
                        StringFormatters.getRelativeTimeSpanString(it.getCreateDate()).toString());
                } else if (date.after(timestamps.getBoundaryToday())) {
                    mCachedTimestamps.put(it.getFragmentId(), StringFormatters.getTodayTimeString(it.getCreateDate()));
                } else if (date.after(timestamps.getBoundaryYesterday())) {
                    mCachedTimestamps.put(it.getFragmentId(), StringFormatters.getYesterdayTimeString(it.getCreateDate()));
                } else {
                    mCachedTimestamps.put(it.getFragmentId(), StringFormatters.getOlderTimeString(it.getCreateDate()));
                }
            } catch (ParseException e) {
                
                e.printStackTrace();
            }

        }
    }

    private class RemoteResourceManagerObserver implements Observer {
        @Override
        public void update(Observable observable, Object data) {
            mHandler.post(mUpdatePhotos);
        }
    }
    
    private Runnable mUpdatePhotos = new Runnable() {
        @Override
        public void run() {
            notifyDataSetChanged();
        }
    };

    private static class ViewHolder {
        ImageView photo;
        TextView firstLine;
        TextView secondLine;
        TextView thirdLine;
        TextView timeTextView;
        TextView commentnumTextView;
    }
}
