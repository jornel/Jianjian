/**
 * Copyright 2011
 */

package com.liangshan.jianjian.android.error;

/**
 * @author Joe Chen
 */
public class LocationException extends JianjianException {

    public LocationException() {
        super("Unable to determine your location.");
    }

    public LocationException(String message) {
        super(message);
    }

    private static final long serialVersionUID = 1L;

}
