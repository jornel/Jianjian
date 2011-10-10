/**
 * Copyright 2011
 */

package com.liangshan.jianjian.android.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import com.liangshan.jianjian.android.R;
import com.liangshan.jianjian.android.util.RemoteResourceManager;
import com.liangshan.jianjian.android.util.StringFormatters;
import com.liangshan.jianjian.general.Jianjian;
import com.liangshan.jianjian.types.Comment;
import com.liangshan.jianjian.types.User;

/**
 * @date 
 * @author 
 */
public class CommentsListAdapter extends BaseGroupAdapter<Comment> 
    implements ObservableAdapter {

    private LayoutInflater mInflater;
    private RemoteResourceManager mRrm;
    private Handler mHandler;
    private RemoteResourceManagerObserver mResourcesObserver;
    private Set<String> mLaunchedPhotoFetches;
    

    public CommentsListAdapter(Context context, RemoteResourceManager rrm) {
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
            convertView = mInflater.inflate(R.layout.comment_list_item, null);

            // Creates a ViewHolder and store references to the two children
            // views we want to bind data to.
            holder = new ViewHolder();
            holder.photo = (ImageView) convertView.findViewById(R.id.c_photo);
            holder.nick = (TextView) convertView.findViewById(R.id.c_firstLine);
            holder.commentBody = (TextView) convertView.findViewById(R.id.c_secondLine);
            holder.timeLine = (TextView) convertView.findViewById(R.id.c_timeTextView);
            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        Comment comment = (Comment) getItem(position);
        User fromuser = comment.getFromUser();
        
        holder.nick.setText(fromuser.getNick());
        holder.commentBody.setText(comment.getBody());
        holder.timeLine.setText( 
                StringFormatters.getRelativeTimeSpanString(comment.getCreateDate()));
        Uri photoUri = Uri.parse(fromuser.getPhoto());
        
        try {
            
            Bitmap bitmap = BitmapFactory.decodeStream(mRrm.getInputStream(photoUri));
            holder.photo.setImageBitmap(bitmap);
        } catch (Exception e) {
            if (Jianjian.FEMALE.equals(fromuser.getGender())) {
                holder.photo.setImageResource(R.drawable.blank_girl);
                
            } else {
                holder.photo.setImageResource(R.drawable.blank_boy);
            }
            
            if (!mLaunchedPhotoFetches.contains(fromuser.getUserid())) {
                mLaunchedPhotoFetches.add(fromuser.getUserid());
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
        TextView nick;
        ImageView photo;
        TextView commentBody;
        TextView timeLine;

    }
}
