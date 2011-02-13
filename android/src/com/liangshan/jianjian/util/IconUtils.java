/**
 * Copyright 2010 Mark Wyszomierski
 */

package com.liangshan.jianjian.util;


/**
 * This is not ideal.
 * 
 * @date July 1, 2010
 * @author 
 */
public class IconUtils {
    
    private static IconUtils mInstance;
    private boolean mRequestHighDensityIcons;
    
    private IconUtils() {
        mRequestHighDensityIcons = false;
    }
    
    public static IconUtils get() {
        if (mInstance == null) {
            mInstance = new IconUtils();
        }
        return mInstance;
    }

    public boolean getRequestHighDensityIcons() {
        return mRequestHighDensityIcons;
    }
    
    public void setRequestHighDensityIcons(boolean requestHighDensityIcons) {
        mRequestHighDensityIcons = requestHighDensityIcons;
    }
}