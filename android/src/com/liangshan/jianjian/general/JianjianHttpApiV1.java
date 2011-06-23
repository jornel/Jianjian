/**
 * 
 */
package com.liangshan.jianjian.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import android.graphics.Bitmap;

import com.liangshan.jianjian.android.error.JianjianError;
import com.liangshan.jianjian.android.error.JianjianException;
import com.liangshan.jianjian.android.error.JianjianParseException;
import com.liangshan.jianjian.android.util.Base64;
import com.liangshan.jianjian.android.util.Base64Coder;
import com.liangshan.jianjian.http.AbstractHttpApi;
import com.liangshan.jianjian.http.HttpApi;
import com.liangshan.jianjian.http.HttpApiWithBasicAuth;
import com.liangshan.jianjian.parsers.json.GroupParser;
import com.liangshan.jianjian.parsers.json.RecommendMsgParser;
import com.liangshan.jianjian.parsers.json.UserParser;
import com.liangshan.jianjian.parsers.json.VenueParser;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.RecommendMsg;
import com.liangshan.jianjian.types.User;
import com.liangshan.jianjian.types.Venue;

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
    private static final String URL_API_VENUE_LIST = "/locations/search";
    
    
    private static final String URL_API_USER_SHOW_TMP = "http://api.jiepang.com/users/show.json";

    private static final String URL_API_CHECK_IN = "/statuses/checkin";

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
     * @param geolat
     * @param geolong
     * @param page
     * @return
     */
    @SuppressWarnings("unchecked")
    public Group<Venue> getVenuesByLocation(String geolat, 
              String geolong, int page) throws JianjianException,
              JianjianError, IOException{
        if(page == 0){ page = 1; }
        
        HttpGet httpGet = mHttpApi.createHttpGet(fullUrl(URL_API_VENUE_LIST), //
                new BasicNameValuePair("source", "jianjian"), //
                new BasicNameValuePair("lang", "CHS"), //
                new BasicNameValuePair("page", String.valueOf(page)), //
                new BasicNameValuePair("lat", geolat), //
                new BasicNameValuePair("lon", geolong) //
                );
        return (Group<Venue>) mHttpApi.doHttpRequest(httpGet, new GroupParser(new VenueParser()));
    }
    
    /**
     * @param geolat
     * @param geolong
     * @param productName
     * @param price
     * @param recommendDes
     * @param venueId
     * @param mPhoto
     * @return
     * @throws IOException 
     * @throws JianjianException 
     * @throws JianjianParseException 
     */
    public RecommendMsg recommendItToAllFriends(String geolat, String geolong, String productName,
            String price, String recommendDes, String venueId, File mPhotoFile, String username, String password) 
            throws SocketTimeoutException, JianjianException, JianjianParseException, JianjianException, IOException {
        
        
        String checkinBody;
        String photoPath;
        if(price == null||price == ""){
            price = " ";
        }
        if(recommendDes == null || recommendDes ==""){
            recommendDes = " ";
        }
        checkinBody = productName + "++" + price + "++" + recommendDes + "(from jianjian)";
        if(mPhotoFile != null){
            photoPath = mPhotoFile.getPath();
        }else {
            photoPath = null;
        }
        
        String BOUNDARY = "******";
        String lineEnd = "\r\n"; 
        String twoHyphens = "--";
        int maxBufferSize = 8192;
        
        File file = new File(photoPath);
        FileInputStream fileInputStream = new FileInputStream(file);
        URL url = new URL(fullUrl(URL_API_CHECK_IN));
        HttpURLConnection conn = mHttpApi.createHttpURLConnectionPost(url, BOUNDARY);
        
        conn.setRequestProperty("Authorization", "Basic " +  Base64Coder.encodeString(username + ":" + password));
        
        
        RecommendMsg recMsg = (RecommendMsg) mHttpApi.doHttpRequest(httpPost, new RecommendMsgParser());
        return recMsg;
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
