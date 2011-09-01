/**
 * Copyright 2010 Mark Wyszomierski
 */

package com.liangshan.jianjian.android.widget;


import android.content.Context;
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
import java.util.Observable;
import java.util.Observer;

import com.liangshan.jianjian.android.R;
import com.liangshan.jianjian.android.util.RemoteResourceManager;
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
    

    public HistoryListAdapter(Context context, RemoteResourceManager rrm) {
        super(context);
        mInflater = LayoutInflater.from(context);
        mHandler = new Handler();
        mRrm = rrm;
        mResourcesObserver = new RemoteResourceManagerObserver();

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
        
        

        
        return convertView;
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
