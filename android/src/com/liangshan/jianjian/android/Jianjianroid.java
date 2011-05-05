/**
 * 
 */
package com.liangshan.jianjian.android;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.liangshan.jianjian.android.error.JianjianError;
import com.liangshan.jianjian.android.error.JianjianException;
import com.liangshan.jianjian.android.location.LocationUtils;
import com.liangshan.jianjian.android.preferences.JPreferences;
import com.liangshan.jianjian.android.util.JavaLoggingHandler;
import com.liangshan.jianjian.android.util.NullDiskCache;
import com.liangshan.jianjian.android.util.RemoteResourceManager;
import com.liangshan.jianjian.general.Jianjian;
import com.liangshan.jianjian.general.Jianjian.JLocation;
import com.liangshan.jianjian.types.User;
import com.liangshan.jianjian.util.IconUtils;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author jornel
 *
 */
public class Jianjianroid extends Application {
    
    private static final String TAG = "Jianjianroid";
    private static final boolean DEBUG = JianjianSettings.DEBUG;
    
    static {
        Logger.getLogger("com.liangshan.jianjian").addHandler(new JavaLoggingHandler());
        Logger.getLogger("com.liangshan.jianjian").setLevel(Level.ALL);
    }
    
    public static final String PACKAGE_NAME = "com.liangshan.jianjian.android";
    
    public static final String INTENT_ACTION_LOGGED_OUT = "com.liangshan.jianjianroid.intent.action.LOGGED_OUT";
    public static final String INTENT_ACTION_LOGGED_IN = "com.liangshan.jianjianroid.intent.action.LOGGED_IN";
    
    private SharedPreferences mPrefs;    
    private RemoteResourceManager mRemoteResourceManager;
    
    private String mVersion = null;
    private TaskHandler mTaskHandler;
    private HandlerThread mTaskThread;
    private boolean mIsFirstRun;
    
    
    private Jianjian mJianjian;
    
    @Override
    public void onCreate() {
        Log.i(TAG, "Using Debug Server:\t" + JianjianSettings.USE_DEBUG_SERVER);
        
        Log.i(TAG, "Using Debug Log:\t" + DEBUG);
        
        mVersion = getVersionString(this);
        
        // Check if this is a new install by seeing if our preference file exists on disk.
        mIsFirstRun = checkIfIsFirstRun();
        // Setup Prefs
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        // Setup some defaults in our preferences if not set yet.
        JPreferences.setupDefaults(mPrefs, getResources());
        
        // If we're on a high density device, request higher res images. This singleton
        // is picked up by the parsers to replace their icon urls with high res versions.
        float screenDensity = getApplicationContext().getResources().getDisplayMetrics().density;
        IconUtils.get().setRequestHighDensityIcons(screenDensity > 1.0f);
        
        // Sometimes we want the application to do some work on behalf of the
        // Activity. Lets do that
        // asynchronously.
        mTaskThread = new HandlerThread(TAG + "-AsyncThread");
        mTaskThread.start();
        mTaskHandler = new TaskHandler(mTaskThread.getLooper());
        
        // Set up storage cache.
        loadResourceManagers();
        
        // Log into Jianjian, if we can.
        loadJianjian();
        
    }


    
    /**
     * Provides static access to a Jianjian instance. This instance is
     * initiated without user credentials.
     * 
     * @param context the context to use when constructing the Jianjian
     *            instance
     * @return the Jianjian instace
     */
    public static Jianjian createJianjian(Context context) {
        String version = getVersionString(context);
        if (JianjianSettings.USE_DEBUG_SERVER) {
            return new Jianjian(Jianjian.createHttpApi("10.0.2.2:8080", version, false));
        } else {
            return new Jianjian(Jianjian.createHttpApi(version, false));
        }
    }
    
    /**
     * @return
     */
    public Jianjian getJianjian() {
        return mJianjian;
    }

    /**
     * @return
     */
    public Location getLastKnownLocation() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * @return
     */
    public boolean isReady() {
        // TODO Auto-generated method stub
        return getJianjian().hasLoginAndPassword() && !TextUtils.isEmpty(getUserId());
    }
    
    public RemoteResourceManager getRemoteResourceManager() {
        return mRemoteResourceManager;
    }
    
    public void requestStartService() {
        mTaskHandler.sendMessage( //
                mTaskHandler.obtainMessage(TaskHandler.MESSAGE_START_SERVICE));
    }
    
    /**
     * @return
     */
    private String getUserId() {
        // TODO Auto-generated method stub
        return JPreferences.getUserId(mPrefs);
    }



    /**
     * 
     */
    private void loadJianjian() {
       
        // Try logging in and setting up foursquare oauth, then user
        // credentials.
        if (JianjianSettings.USE_DEBUG_SERVER) {
            mJianjian = new Jianjian(Jianjian.createHttpApi("api.jiepang.com", mVersion, false));
        } else {
            mJianjian = new Jianjian(Jianjian.createHttpApi(mVersion, false));
        }
        
        if (JianjianSettings.DEBUG) Log.d(TAG, "loadCredentials()");
        String phoneNumber = mPrefs.getString(JPreferences.PREFERENCE_LOGIN, null);
        String password = mPrefs.getString(JPreferences.PREFERENCE_PASSWORD, null);
        mJianjian.setCredentials(phoneNumber, password);
        
    }

    /**
     * @return
     */
    private boolean checkIfIsFirstRun() {
        File file = new File(
        "/data/data/com.liangshan.jianjian.android/shared_prefs/com.liangshan.jianjian.foursquared_preferences.xml");
        return !file.exists();
    }

    /**
     * Constructs the version string of the application.
     * 
     * @param context the context to use for getting package info
     * @return the versions string of the application
     */
    private static String getVersionString(Context context) {
        // Get a version string for the app.
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(PACKAGE_NAME, 0);
            return PACKAGE_NAME + ":" + String.valueOf(pi.versionCode);
        } catch (NameNotFoundException e) {
            if (DEBUG) Log.d(TAG, "Could not retrieve package info", e);
            throw new RuntimeException(e);
        }
    }


    
    private void loadResourceManagers() {
        // We probably don't have SD card access if we get an
        // IllegalStateException. If it did, lets
        // at least have some sort of disk cache so that things don't npe when
        // trying to access the
        // resource managers.
        try {
            if (DEBUG) Log.d(TAG, "Attempting to load RemoteResourceManager(cache)");
            mRemoteResourceManager = new RemoteResourceManager("cache");
        } catch (IllegalStateException e) {
            if (DEBUG) Log.d(TAG, "Falling back to NullDiskCache for RemoteResourceManager");
            mRemoteResourceManager = new RemoteResourceManager(new NullDiskCache());
        }
    }
    
    private class TaskHandler extends Handler {

        private static final int MESSAGE_UPDATE_USER = 1;
        private static final int MESSAGE_START_SERVICE = 2;

        public TaskHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (DEBUG) Log.d(TAG, "handleMessage: " + msg.what);

            switch (msg.what) {
                case MESSAGE_UPDATE_USER:
                    try {
                        // Update user info
                        Log.d(TAG, "Updating user.");
                        
                        // Use location when requesting user information, if we
                        // have it.
                        JLocation location = LocationUtils
                                .createFoursquareLocation(getLastKnownLocation());
                        User user = getJianjian().user(
                                null, false, false, false, location);

                        Editor editor = mPrefs.edit();
                        JPreferences.storeUser(editor, user);
                        editor.commit();

                        if (location == null) {
                            // Pump the location listener, we don't have a
                            // location in our listener yet.
                            Log.d(TAG, "Priming Location from user city.");
                            Location primeLocation = new Location("foursquare");
                            // Very inaccurate, right?
                            primeLocation.setTime(System.currentTimeMillis());
                            //mBestLocationListener.updateLocation(primeLocation);
                        }

                    } catch (JianjianError e) {
                        if (DEBUG) Log.d(TAG, "FoursquareError", e);
                        // TODO Auto-generated catch block
                    } catch (JianjianException e) {
                        if (DEBUG) Log.d(TAG, "FoursquareException", e);
                        // TODO Auto-generated catch block
                    } catch (IOException e) {
                        if (DEBUG) Log.d(TAG, "IOException", e);
                        // TODO Auto-generated catch block
                    }
                    return;

                case MESSAGE_START_SERVICE:
                    //Intent serviceIntent = new Intent(Jianjianroid.this, FoursquaredService.class);
                    //serviceIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    //startService(serviceIntent);
                    return;
            }
        }
    }



}
