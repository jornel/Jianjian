/**
 * 
 */
package com.liangshan.jianjian.general;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import com.liangshan.jianjian.android.error.JianjianError;
import com.liangshan.jianjian.android.error.JianjianException;
import com.liangshan.jianjian.http.AbstractHttpApi;
import com.liangshan.jianjian.http.HttpApi;
import com.liangshan.jianjian.http.HttpApiWithBasicAuth;
import com.liangshan.jianjian.parsers.json.UserParser;
import com.liangshan.jianjian.types.User;

/**
 * @author jornel
 *
 */
public class JianjianHttpApiV1 {
    
    private static final Logger LOG = Logger
    .getLogger(JianjianHttpApiV1.class.getCanonicalName());
    
    private static final boolean DEBUG = Jianjian.DEBUG;

    private static final String URL_API_USER = "/account/verify_credentials";
    private static final String URL_API_USER_DETAIL = "/users/show";
    
    
    private static final String URL_API_USER_SHOW_TMP = "http://api.jiepang.com/users/show.json";

    //private static final String DATATYPE = ".json";
    
    private HttpApi mHttpApi;
    
    private final String mApiBaseUrl;
    private final AuthScope mAuthScope;
    
    private final DefaultHttpClient mHttpClient = AbstractHttpApi.createHttpClient();
    
    public JianjianHttpApiV1(String domain, String clientVersion, boolean useOAuth) {
        mApiBaseUrl = "http://" + domain + "/v1";
        mAuthScope = new AuthScope(domain, 80);
        
        
        if (useOAuth) {
            mHttpApi = new HttpApiWithBasicAuth(mHttpClient, clientVersion);
        } else {
            mHttpApi = new HttpApiWithBasicAuth(mHttpClient, clientVersion);
        }
        
    }
    
    public boolean hasCredentials() {
        return mHttpClient.getCredentialsProvider().getCredentials(mAuthScope) != null;
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
    User user(String uid, boolean b, boolean c, boolean d, String geolat, String geolong,
            String geohacc, String geovacc, String geoalt)  throws JianjianException,
            JianjianError, IOException{
        
        HttpGet httpGet = mHttpApi.createHttpGet(fullUrl(URL_API_USER), //
                new BasicNameValuePair("source", "jianjian"), //
                new BasicNameValuePair("lang", "CHS"), //
                new BasicNameValuePair("uid", uid), //
                new BasicNameValuePair("b", (b) ? "1" : "0"), //
                new BasicNameValuePair("c", (c) ? "1" : "0"), //
                new BasicNameValuePair("d", (d) ? "1" : "0"), //
                new BasicNameValuePair("geolat", geolat), //
                new BasicNameValuePair("geolong", geolong), //
                new BasicNameValuePair("geohacc", geohacc), //
                new BasicNameValuePair("geovacc", geovacc), //
                new BasicNameValuePair("geoalt", geoalt) //
                );
        return (User) mHttpApi.doHttpRequest(httpGet, new UserParser());
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
    User showUser(String uid, String geolat, String geolong,
            String geohacc, String geovacc, String geoalt)  throws JianjianException,
            JianjianError, IOException{
        
        HttpGet httpGet = mHttpApi.createHttpGet(URL_API_USER_SHOW_TMP, //
                new BasicNameValuePair("source", "jianjian"), //
                new BasicNameValuePair("lang", "CHS"), //
                new BasicNameValuePair("uid", uid), //
                new BasicNameValuePair("geolat", geolat), //
                new BasicNameValuePair("geolong", geolong), //
                new BasicNameValuePair("geohacc", geohacc), //
                new BasicNameValuePair("geovacc", geovacc), //
                new BasicNameValuePair("geoalt", geoalt) //
                );
        return (User) mHttpApi.doHttpRequest(httpGet, new UserParser());
    }

    /**
     * @param urlApiUser
     * @return
     */
    private String fullUrl(String url) {
        return mApiBaseUrl + url;

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