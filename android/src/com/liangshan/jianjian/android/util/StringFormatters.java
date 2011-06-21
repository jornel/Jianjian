/**
 * Copyright 2012 joe chen
 */

package com.liangshan.jianjian.android.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.liangshan.jianjian.types.User;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.text.format.DateUtils;



/**
 * @author Joe LaPenna (joe@joelapenna.com)
 * @author Mark Wyszomierski (markww@gmail.com)
 *   -Added date formats for today/yesterday/older contexts.
 */
public class StringFormatters {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "EEE, dd MMM yy HH:mm:ss Z");
    
    /** Should look like "9:09 AM". */
    public static final SimpleDateFormat DATE_FORMAT_TODAY = new SimpleDateFormat(
            "h:mm a");

    /** Should look like "Sun 1:56 PM". */
    public static final SimpleDateFormat DATE_FORMAT_YESTERDAY = new SimpleDateFormat(
            "E h:mm a");

    /** Should look like "Sat Mar 20". */
    public static final SimpleDateFormat DATE_FORMAT_OLDER = new SimpleDateFormat(
            "E MMM d");
    
    /*
    public static String getVenueLocationFull(Venue venue) {
    	StringBuilder sb = new StringBuilder();
    	sb.append(venue.getAddress());
    	if (sb.length() > 0) {
    		sb.append(" ");
    	}
    	if (!TextUtils.isEmpty(venue.getCrossstreet())) {
            sb.append("(");
            sb.append(venue.getCrossstreet());
            sb.append(")");
    	}
    	return sb.toString();
    }

    public static String getVenueLocationCrossStreetOrCity(Venue venue) {
        if (!TextUtils.isEmpty(venue.getCrossstreet())) {
            return "(" + venue.getCrossstreet() + ")";
        } else if (!TextUtils.isEmpty(venue.getCity()) && !TextUtils.isEmpty(venue.getState())
                && !TextUtils.isEmpty(venue.getZip())) {
            return venue.getCity() + ", " + venue.getState() + " " + venue.getZip();
        } else {
            return null;
        }
    }

    public static String getCheckinMessageLine1(Checkin checkin, boolean displayAtVenue) {
        if (checkin.getDisplay() != null) {
            return checkin.getDisplay();

        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(getUserAbbreviatedName(checkin.getUser()));
            if (checkin.getVenue() != null && displayAtVenue) {
                sb.append(" @ " + checkin.getVenue().getName());
            }
            return sb.toString();
        }
    }
    
    public static String getCheckinMessageLine2(Checkin checkin) {
        if (TextUtils.isEmpty(checkin.getShout()) == false) {
            return checkin.getShout();
        } else {
            // No shout, show address instead.
            if (checkin.getVenue() != null && checkin.getVenue().getAddress() != null) {
                String address = checkin.getVenue().getAddress();
                if (checkin.getVenue().getCrossstreet() != null
                        && checkin.getVenue().getCrossstreet().length() > 0) {
                    address += " (" + checkin.getVenue().getCrossstreet() + ")";
                }
                return address;
            } else {
                return "";
            }
        }
    }
    
    public static String getCheckinMessageLine3(Checkin checkin) {
        if (!TextUtils.isEmpty(checkin.getCreated())) {
            try {
                return getTodayTimeString(checkin.getCreated());
            } catch (Exception ex) {
                return checkin.getCreated();
            }
        } else {
            return "";
        }
    }
    */

    public static String getUserFullName(User user) {
        StringBuffer sb = new StringBuffer();
        sb.append(user.getUsername());//username is the name of user
        return sb.toString();
    }    
    
    public static String getUserAbbreviatedName(User user) {
        StringBuffer sb = new StringBuffer();
        sb.append(user.getUserid());//userid is the name of user
        return sb.toString();
    }

    public static CharSequence getRelativeTimeSpanString(String created) {
        try {
            return DateUtils.getRelativeTimeSpanString(DATE_FORMAT.parse(created).getTime(),
                    new Date().getTime(), DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE);
        } catch (ParseException e) {
            return created;
        }
    }

    /**
     * Returns a format that will look like: "9:09 AM".
     */
    public static String getTodayTimeString(String created) {
        try {
            return DATE_FORMAT_TODAY.format(DATE_FORMAT.parse(created));
        } catch (ParseException e) {
            return created;
        }
    }
    
    /**
     * Returns a format that will look like: "Sun 1:56 PM".
     */
    public static String getYesterdayTimeString(String created) {
        try {
            return DATE_FORMAT_YESTERDAY.format(DATE_FORMAT.parse(created));
        } catch (ParseException e) {
            return created;
        }
    }
    
    /**
     * Returns a format that will look like: "Sat Mar 20".
     */
    public static String getOlderTimeString(String created) {
        try {
            return DATE_FORMAT_OLDER.format(DATE_FORMAT.parse(created));
        } catch (ParseException e) {
            return created;
        }
    }
    
    /**
     * Reads an inputstream into a string.
     */
    public static String inputStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    /*
    public static String getTipAge(Resources res, String created) {
        Calendar then = Calendar.getInstance();
        then.setTime(new Date(created));
        Calendar now = Calendar.getInstance();
        now.setTime(new Date(System.currentTimeMillis()));
        
        if (now.get(Calendar.YEAR) == then.get(Calendar.YEAR)) {
            if (now.get(Calendar.MONTH) == then.get(Calendar.MONTH)) {
                int diffDays = now.get(Calendar.DAY_OF_MONTH)- then.get(Calendar.DAY_OF_MONTH);
                if (diffDays == 0) {
                    return res.getString(R.string.tip_age_today);
                } else if (diffDays == 1) {
                    return res.getString(R.string.tip_age_days, "1", "");
                } else {
                    return res.getString(R.string.tip_age_days, String.valueOf(diffDays), "s");
                }
            } else {
                int diffMonths = now.get(Calendar.MONTH) - then.get(Calendar.MONTH);
                if (diffMonths == 1) {
                    return res.getString(R.string.tip_age_months, "1", "");
                } else {
                    return res.getString(R.string.tip_age_months, String.valueOf(diffMonths), "s");
                }
            }
        } else {
            int diffYears = now.get(Calendar.YEAR) - then.get(Calendar.YEAR);
            if (diffYears == 1) {
                return res.getString(R.string.tip_age_years, "1", "");
            } else {
                return res.getString(R.string.tip_age_years, String.valueOf(diffYears), "s");
            }
        }
    }
    */
    public static String createServerDateFormatV1() {
    	DateFormat df = new SimpleDateFormat("EEE, dd MMM yy HH:mm:ss Z");
    	return df.format(new Date());
    }
    
    public static byte[] getBitmapByte(Bitmap bitmap){   
        ByteArrayOutputStream out = new ByteArrayOutputStream();   
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);   
        try {   
            out.flush();   
            out.close();   
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
        return out.toByteArray();   
    }   
}
