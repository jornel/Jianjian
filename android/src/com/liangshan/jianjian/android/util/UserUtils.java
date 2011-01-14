/**
 * 
 */
package com.liangshan.jianjian.android.util;

import com.liangshan.jianjian.android.R;


/**
 * @author ezhuche
 *
 */
public class UserUtils {
    
    public static int getDrawableForMeTabByGender(String gender) {
        if (gender != null && gender.equals("female")) {
            return R.drawable.tab_main_nav_me_girl_selector;
        } else {
            return R.drawable.tab_main_nav_me_boy_selector;
        }
    }

}
