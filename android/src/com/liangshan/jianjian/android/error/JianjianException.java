/**
 * Copyright 2011
 */

package com.liangshan.jianjian.android.error;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 */
public class JianjianException extends Exception {
    private static final long serialVersionUID = 1L;
    
    private String mExtra;

    public JianjianException(String message) {
        super(message);
    }

    public JianjianException(String message, String extra) {
        super(message);
        mExtra = extra;
    }
    
    public String getExtra() {
        return mExtra;
    }
}
