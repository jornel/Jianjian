/**
 * Copyright
 */

package com.liangshan.jianjian.android.location;

import com.liangshan.jianjian.general.Jianjian.JLocation;

/**
 * @author Joe Chen 
 */
public class LocationUtils {

    public static final JLocation createFoursquareLocation(android.location.Location location) {
        if (location == null) {
            return new JLocation(null, null, null, null, null);
        }
        String geolat = null;
        if (location.getLatitude() != 0.0) {
            geolat = String.valueOf(location.getLatitude());
        }

        String geolong = null;
        if (location.getLongitude() != 0.0) {
            geolong = String.valueOf(location.getLongitude());
        }

        String geohacc = null;
        if (location.hasAccuracy()) {
            geohacc = String.valueOf(location.getAccuracy());
        }

        String geoalt = null;
        if (location.hasAccuracy()) {
            geoalt = String.valueOf(location.hasAltitude());
        }

        return new JLocation(geolat, geolong, geohacc, null, geoalt);
    }

}
