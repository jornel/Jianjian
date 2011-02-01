/**
 * 
 */
package com.liangshan.jianjian.android.util;

import com.liangshan.jianjian.android.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;


/**
 * @author ezhuche
 *
 */
public abstract class TabsUtil {
    
    private static void setTabIndicator(TabSpec spec, String title, Drawable drawable, View view) {
        int sdk = new Integer(Build.VERSION.SDK).intValue();
        if (sdk < 4) {
            TabsUtil3.setTabIndicator(spec, title, drawable);
        } else {
            TabsUtil4.setTabIndicator(spec, view);
        }
    }
    
    public static void addTab(TabHost host, String title, int drawable, int index, int layout) {
        TabHost.TabSpec spec = host.newTabSpec("tab" + index);
        spec.setContent(layout);
        View view = prepareTabView(host.getContext(), title, drawable);
        TabsUtil.setTabIndicator(spec, title, host.getContext().getResources().getDrawable(drawable), view);
        host.addTab(spec);
    }
    
    public static void addTab(TabHost host, String title, int drawable, int index, Intent intent) {
        TabHost.TabSpec spec = host.newTabSpec("tab" + index);
        spec.setContent(intent);
        View view = prepareTabView(host.getContext(), title, drawable);
        TabsUtil.setTabIndicator(spec, title, host.getContext().getResources().getDrawable(drawable), view);
        host.addTab(spec);
    }
    
    private static View prepareTabView(Context context, String text, int drawable) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_main_nav, null);
        TextView tv = (TextView) view.findViewById(R.id.tvTitle);
        tv.setText(text);
        ImageView iv = (ImageView) view.findViewById(R.id.ivIcon);
        iv.setImageResource(drawable);
        return view;
    }

}
