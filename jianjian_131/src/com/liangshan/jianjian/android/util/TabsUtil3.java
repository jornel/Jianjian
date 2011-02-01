/**
 * 
 */
package com.liangshan.jianjian.android.util;

import android.graphics.drawable.Drawable;
import android.widget.TabHost.TabSpec;

/**
 * @author ezhuche
 *
 */
public class TabsUtil3 {
    
    public static void setTabIndicator(TabSpec spec, String title, Drawable drawable) {
            spec.setIndicator(title);
    }

}
