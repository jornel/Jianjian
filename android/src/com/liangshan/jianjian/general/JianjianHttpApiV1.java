/**
 * 
 */
package com.liangshan.jianjian.general;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;

import com.liangshan.jianjian.android.error.JianjianError;
import com.liangshan.jianjian.android.error.JianjianException;
import com.liangshan.jianjian.http.AbstractHttpApi;
import com.liangshan.jianjian.http.HttpApi;
import com.liangshan.jianjian.http.HttpApiWithBasicAuth;
import com.liangshan.jianjian.types.User;

/**
 * @author jornel
 *
 */
public class JianjianHttpApiV1 {
    
    private static final Logger LOG = Logger
    .getLogger(JianjianHttpApiV1.class.getCanonicalName());
    
    private static final boolean DEBUG = Jianjian.DEBUG;
    private HttpApi mHttpApi;
    
    private final String mApiBaseUrl;
    private final AuthScope mAuthScope;
    
    private final DefaultHttpClient mHttpClient = AbstractHttpApi.createHttpClient();
    
    public JianjianHttpApiV1(String domain, String clientVersion, boolean useOAuth) {
        mApiBaseUrl = "https://" + domain + "/v1";
        mAuthScope = new AuthScope(domain, 80);
        
        
        if (useOAuth) {
            mHttpApi = new HttpApiWithBasicAuth(mHttpClient, clientVersion);
        } else {
            mHttpApi = new HttpApiWithBasicAuth(mHttpClient, clientVersion);
        }
        
    }

    /**
     * /user?uid=9937
     * @param user
     * @param b
     * @param c
     * @param d
     * @param geolat
     * @param geolong
     * @param geohacc
     * @param geovacc
     * @param geoalt
     * @return
     */
    User user(String user, boolean b, boolean c, boolean d, String geolat, String geolong,
            String geohacc, String geovacc, String geoalt)  throws JianjianException,
            JianjianError, IOException{
        
        return null;
    }

    /**
     * @param phone
     * @param password
     */
    void setCredentials(String phone, String password) {
        if (phone == null || phone.length() == 0 || password == null || password.length() == 0) {
            if (DEBUG) LOG.log(Level.FINE, "Clearing Credentials");
            mHttpClient.getCredentialsProvider().clear();
        } else {
            if (DEBUG) LOG.log(Level.FINE, "Setting Phone/Password: " + phone + "/******");
            mHttpClient.getCredentialsProvider().setCredentials(mAuthScope,
                    new UsernamePasswordCredentials(phone, password));
        }
        
    }

}
