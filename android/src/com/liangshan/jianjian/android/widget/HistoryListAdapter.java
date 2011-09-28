/**
 * Copyright 2011
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
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import com.liangshan.jianjian.android.R;
import com.liangshan.jianjian.android.util.RemoteResourceManager;
import com.liangshan.jianjian.android.util.StringFormatters;
import com.liangshan.jianjian.general.Jianjian;
import com.liangshan.jianjian.types.RecommendMsg;
import com.liangshan.jianjian.types.User;

/**
 * @date 
 * @author 
 */
public class HistoryListAdapter extends BaseRecommendAdapter 
    implements ObservableAdapter {

    private LayoutInflater mInflater;
    private RemoteResourceManager mRrm;
    private Handler mHandler;
    private RemoteResourceManagerObserver mResourcesObserver;
    private Set<String> mLaunchedPhotoFetches;
    

    public HistoryListAdapter(Context context, RemoteResourceManager rrm) {
        super(context);
        mInflater = LayoutInflater.from(context);
        mHandler = new Handler();
        mRrm = rrm;
        mResourcesObserver = new RemoteResourceManagerObserver();
        mLaunchedPhotoFetches = new HashSet<String>();

        mRrm.addObserver(mResourcesObserver);
    }

    public void removeObserver() {
        mRrm.deleteObserver(mResourcesObserver);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

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
            
            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        RecommendMsg recommend = (RecommendMsg) getItem(position);
        
        Resources res = convertView.getContext().getResources();
        String venueSymbol = res.getString(R.string.venueSymbol);
        
        holder.firstLine.setText(recommend.getProduct().getName());//product name
        holder.secondLine.setText(StringFormatters.getRecommendMessageLine2(recommend, venueSymbol, true));//x Ôª£¬ÔÚÄ³µØ
        holder.thirdLine.setText(StringFormatters.getRecommendMessageLine3(recommend));
        holder.timeTextView.setText( 
                StringFormatters.getRelativeTimeSpanString(recommend.getCreateDate()));
        
        Uri photoUri = null;

        
        try {
            photoUri = Uri.parse(recommend.getPhoto().get(0));
            Bitmap bitmap = BitmapFactory.decodeStream(mRrm.getInputStream(photoUri));
            holder.photo.setImageBitmap(bitmap);
        } catch (Exception e) {
            holder.photo.setImageResource(R.drawable.category_none);
            if (photoUri != null && !mLaunchedPhotoFetches.contains(recommend.getFragmentId())) {
                mLaunchedPhotoFetches.add(recommend.getFragmentId());
                mRrm.request(photoUri);
            }
        }

        
        return convertView;
    }
    
    public void clear() {
        notifyDataSetInvalidated();
    }
    
    private class RemoteResourceManagerObserver implements Observer {
        @Override
        public void update(Observable observable, Object data) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    private static class ViewHolder {
        TextView thirdLine;
        TextView secondLine;
        ImageView photo;
        TextView firstLine;
        TextView timeTextView;
    }
}
