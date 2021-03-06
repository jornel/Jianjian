/**
 * 
 */
package com.liangshan.jianjian.android.preferences;

import java.io.IOException;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.util.Log;

import com.liangshan.jianjian.android.JianjianSettings;
import com.liangshan.jianjian.android.R;
import com.liangshan.jianjian.android.error.JianjianCredentialsException;
import com.liangshan.jianjian.android.error.JianjianException;
import com.liangshan.jianjian.general.Jianjian;
import com.liangshan.jianjian.general.Jianjian.JLocation;
import com.liangshan.jianjian.types.User;

/**
 * @author jornel
 *
 */
public class JPreferences {
    
    private static final String TAG = "Preferences";
    private static final boolean DEBUG = JianjianSettings.DEBUG;
    
    
    public static final String PREFERENCE_STARTUP_TAB = "startup_tab";
    public static final String PREFERENCE_CACHE_GEOLOCATION_FOR_SEARCHES 
        = "cache_geolocation_for_searches";
    public static final String PREFERENCE_SHOW_PRELAUNCH_ACTIVITY = "show_prelaunch_activity";
    public static final String PREFERENCE_PINGS_INTERVAL = "pings_refresh_interval_in_minutes";
    public static final String PREFERENCE_NATIVE_IMAGE_VIEWER = "native_full_size_image_viewer";
    private static final String PREFERENCE_ID = "userid";
    private static final String PREFERENCE_USER_NAME = "user_name";
    private static final String PREFERENCE_USER_EMAIL = "user_email";
    //private static final String PREFERENCE_USER_NICK = "user_nick";
    private static final String PREFERENCE_USER_CITY = "user_city";
    //private static final String PREFERENCE_USER_PIC = "user_pic";
    
    // Credentials related preferences
    public static final String PREFERENCE_LOGIN = "phone";
    public static final String PREFERENCE_PASSWORD = "password";
    /**
     * @param preferences
     * @param resources
     */
    public static void setupDefaults(SharedPreferences preferences, Resources resources) {
        
        Editor editor = preferences.edit();
        if (!preferences.contains(PREFERENCE_STARTUP_TAB)) {
            String[] startupTabValues = resources.getStringArray(R.array.startup_tabs_values);
            editor.putString(PREFERENCE_STARTUP_TAB, startupTabValues[0]);
        }
        if (!preferences.contains(PREFERENCE_CACHE_GEOLOCATION_FOR_SEARCHES)) {
            editor.putBoolean(PREFERENCE_CACHE_GEOLOCATION_FOR_SEARCHES, false);
        }
        if (!preferences.contains(PREFERENCE_SHOW_PRELAUNCH_ACTIVITY)) {
            editor.putBoolean(PREFERENCE_SHOW_PRELAUNCH_ACTIVITY, true);
        }
        if (!preferences.contains(PREFERENCE_PINGS_INTERVAL)) {
            editor.putString(PREFERENCE_PINGS_INTERVAL, "30");
        }
        if (!preferences.contains(PREFERENCE_NATIVE_IMAGE_VIEWER)) {
            editor.putBoolean(PREFERENCE_NATIVE_IMAGE_VIEWER, true);
        }
        editor.commit();
    }
    
    public static void storeUser(final Editor editor, User user) {
        if (user != null && user.getUserid() != null) {
            editor.putString(PREFERENCE_ID, user.getUserid());
            editor.putString(PREFERENCE_USER_NAME, user.getUsername());
            editor.putString(PREFERENCE_USER_EMAIL, user.getEmail());
            //editor.putString(PREFERENCE_USER_NICK, user.getNick());
            editor.putString(PREFERENCE_USER_CITY, user.getCity());
            
            if (DEBUG) Log.d(TAG, "Setting user info");
        } else {
            if (JPreferences.DEBUG) Log.d(JPreferences.TAG, "Unable to lookup user.");
        }
    }
    
    public static String getUserId(SharedPreferences prefs) {
        return prefs.getString(PREFERENCE_ID, null);
    }
    
    public static String getUserName(SharedPreferences prefs) {
        return prefs.getString(PREFERENCE_USER_EMAIL, null);
    }
    
    public static String getUserEmail(SharedPreferences prefs) {
        return prefs.getString(PREFERENCE_USER_EMAIL, null);
    }
    
    public static String getUserCity(SharedPreferences prefs) {
        return prefs.getString(PREFERENCE_USER_CITY, null);
    }

    /**
     * @param jianjian
     * @param editor
     */
    public static boolean logoutUser(Jianjian jianjian, Editor editor) {
        if (DEBUG) Log.d(JPreferences.TAG, "Trying to log out.");
        // TODO: If we re-implement oAuth, we'll have to call clearAllCrendentials here.
        jianjian.setCredentials(null, null);
        return editor.clear().commit();
    }
    
    public static boolean loginUser(Jianjian jianjian, String login, String password,
            JLocation location, Editor editor) throws JianjianCredentialsException,
            JianjianException, IOException {
        if (DEBUG) Log.d(JPreferences.TAG, "Trying to log in.");

        jianjian.setCredentials(login, password);
        storeLoginAndPassword(editor, login, password);
        if (!editor.commit()) {
            if (DEBUG) Log.d(TAG, "storeLoginAndPassword commit failed");
            return false;
        }
        
        User user = jianjian.user(null, false, false, false, location);
        storeUser(editor, user);
        if (!editor.commit()) {
            if (DEBUG) Log.d(TAG, "storeUser commit failed");
            return false;
        }

        return true;
    }
    
    public static void storeLoginAndPassword(final Editor editor, String login, String password) {
        editor.putString(PREFERENCE_LOGIN, login);
        editor.putString(PREFERENCE_PASSWORD, password);
    }

}
