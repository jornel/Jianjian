/**
 * 
 */
package com.liangshan.jianjian.general;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.liangshan.jianjian.android.error.JianjianError;
import com.liangshan.jianjian.android.error.JianjianException;
import com.liangshan.jianjian.types.User;

/**
 * @author jornel
 *
 */
public class Jianjian {
    
    private static final Logger LOG = Logger.getLogger("com.liangshan.jianjian");
    public static final boolean DEBUG = false;

    private static final String JIANJIAN_API_DOMAIN = "api.jiepang.com";
    public static final String JIANJIAN_MOBILE_SIGNUP = "http://jiepang.com/m/signup";
    
    private JianjianHttpApiV1 mJianjianV1;
    //private String mPhone;
    //private String mPassword;
    
    @V1
    public Jianjian(JianjianHttpApiV1 httpApi) {
        mJianjianV1 = httpApi;
    }
    
    @V1
    public boolean hasLoginAndPassword() {
        return mJianjianV1.hasCredentials();
    }
    
    /**
     * @param phone
     * @param password
     */
    public void setCredentials(String phone, String password) {
        //mPhone = phone;
        //mPassword = password;
        mJianjianV1.setCredentials(phone, password);
        
    }
    
    /**
     * @param string
     * @param b
     * @param c
     * @param d
     * @param createFoursquareLocation
     * @return
     */
    @V1
    public User user(String user, boolean b, boolean c, boolean d,
            JLocation location) 
            throws JianjianException, JianjianError, IOException{
        // TODO Auto-generated method stub
        if (location != null) {
            return mJianjianV1.user(user, b, c, d, location.geolat, location.geolong,
                    location.geohacc, location.geovacc, location.geoalt);
        } else {
            return mJianjianV1.user(user, b, c, d, null, null, null, null, null);
        }
    }
    
    /**
     * @param string
     * @param b
     * @param c
     * @param d
     * @param createFoursquareLocation
     * @return
     */
    @V1
    public User showUser(String user, JLocation location) 
            throws JianjianException, JianjianError, IOException{
        // TODO Auto-generated method stub
        if (location != null) {
            return mJianjianV1.showUser(user, location.geolat, location.geolong,
                    location.geohacc, location.geovacc, location.geoalt);
        } else {
            return mJianjianV1.showUser(user, null, null, null, null, null);
        }
    }
    
    /**
     * @param mUserId
     * @return
     */
    @V1
    public User friendApprove(String mUserId) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param mUserId
     * @return
     */
    @V1
    public User friendSendrequest(String mUserId) {
        // TODO Auto-generated method stub
        return null;
    }

    
    
    /**
     * @param string
     * @param mVersion
     * @param b
     * @return
     */
    public static JianjianHttpApiV1 createHttpApi(String domain, String clientVersion, 
            boolean useOAuth) {
        LOG.log(Level.INFO, "Using Jianjian for requests.");
        return new JianjianHttpApiV1(domain, clientVersion, useOAuth);
    }
    
    /**
     * @param mVersion
     * @param b
     * @return
     */
    public static JianjianHttpApiV1 createHttpApi(String clientVersion, boolean useOAuth) {
        return createHttpApi(JIANJIAN_API_DOMAIN, clientVersion, useOAuth);
    }
    

    
    
    public static class JLocation {
        String geolat = null;
        String geolong = null;
        String geohacc = null;
        String geovacc = null;
        String geoalt = null;

        public JLocation() {
        }

        public JLocation(final String geolat, final String geolong, final String geohacc,
                final String geovacc, final String geoalt) {
            this.geolat = geolat;
            this.geolong = geolong;
            this.geohacc = geohacc;
            this.geovacc = geovacc;
            this.geoalt = geovacc;
        }

        public JLocation(final String geolat, final String geolong) {
            this(geolat, geolong, null, null, null);
        }
    }
    
    public @interface V1 {

    }



}
