/**
 * 
 */
package com.liangshan.jianjian.android.util;

import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * @author ezhuche
 *
 */
public class TabsUtil4 {
    
    public static void setTabIndicator(TabSpec spec, View view) {
        spec.setIndicator(view);
    }

    public static int getTabCount(TabHost tabHost) {
        return tabHost.getTabWidget().getTabCount();
    }

}
