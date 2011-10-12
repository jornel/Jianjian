/**
 * 
 */
package com.liangshan.jianjian.general;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


import android.graphics.Bitmap;

import com.liangshan.jianjian.android.error.JianjianError;
import com.liangshan.jianjian.android.error.JianjianException;
import com.liangshan.jianjian.android.error.JianjianParseException;
import com.liangshan.jianjian.android.util.Base64;
import com.liangshan.jianjian.android.util.Base64Coder;
import com.liangshan.jianjian.http.AbstractHttpApi;
import com.liangshan.jianjian.http.HttpApi;
import com.liangshan.jianjian.http.HttpApiWithBasicAuth;
import com.liangshan.jianjian.parsers.json.CommentParser;
import com.liangshan.jianjian.parsers.json.EventParser;
import com.liangshan.jianjian.parsers.json.GroupParser;
import com.liangshan.jianjian.parsers.json.RecommendMsgParser;
import com.liangshan.jianjian.parsers.json.UserParser;
import com.liangshan.jianjian.parsers.json.VenueParser;
import com.liangshan.jianjian.types.Comment;
import com.liangshan.jianjian.types.Event;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.RecommendMsg;
import com.liangshan.jianjian.types.User;
import com.liangshan.jianjian.types.Venue;
import com.liangshan.jianjian.util.JSONUtils;

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

    private static final String URL_API_EVENT_LIST = "/events/list";
    
    private static final String URL_API_HISTORY_LIST = "/statuses/list";
    
    private static final String URL_API_FRIEND_LIST = "/friends/list";
    
    private static final String URL_API_COMMENT_LIST = "/comments/list";

    private static final String EVENT_LIST_PAGE_COUNT = "20";
    
    private static final String HISTORY_LIST_PAGE_COUNT = "10";

    private static final String FRIEND_LIST_PAGE_COUNT = "20";



    

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
     * @param geohacc
     * @param geovacc
     * @param geoalt
     * @return
     */
    @SuppressWarnings("unchecked")
    public Group<Event> recommends(int page, String geolat, String geolong, String geohacc,
            String geovacc, String geoalt) throws JianjianException,
            JianjianError, IOException{
        
        if(page == 0){ page = 1; }
        
        HttpGet httpGet = mHttpApi.createHttpGet(fullUrl(URL_API_EVENT_LIST), //
                new BasicNameValuePair("source", "jianjian"), //
                new BasicNameValuePair("lang", "CHS"), //
                new BasicNameValuePair("page", String.valueOf(page)), //
                new BasicNameValuePair("count", EVENT_LIST_PAGE_COUNT) //
                );
        
        return (Group<Event>) mHttpApi.doHttpRequest(httpGet,
                new GroupParser(new EventParser()));
    }
    
    /**
     * @param count
     * @param sinceid
     * @param sinceid 
     * @param page
     * @return
     */
    @SuppressWarnings("unchecked")
    public Group<RecommendMsg> history(String userid, String sinceid, int page) throws JianjianException,
            JianjianError, IOException{
        if(page == 0){ page = 1; }
        
        HttpGet httpGet = mHttpApi.createHttpGet(fullUrl(URL_API_HISTORY_LIST), //
                new BasicNameValuePair("source", "jianjian"), //
                new BasicNameValuePair("lang", "CHS"), //
                new BasicNameValuePair("id", userid),
                new BasicNameValuePair("type", "checkin"),
                new BasicNameValuePair("page", String.valueOf(page)), //
                new BasicNameValuePair("count", HISTORY_LIST_PAGE_COUNT) //
                );
        
        return (Group<RecommendMsg>) mHttpApi.doHttpRequest(httpGet,
                new GroupParser(new RecommendMsgParser()));
    }
    
    /**
     * @param userid
     * @param mPage
     * @return
     */
    @SuppressWarnings("unchecked")
    public Group<User> friendlist(String userid, int page) throws JianjianException,
    JianjianError, IOException{
        if(page == 0){ page = 1; }
        
        HttpGet httpGet = mHttpApi.createHttpGet(fullUrl(URL_API_FRIEND_LIST), //
                new BasicNameValuePair("source", "jianjian"), //
                new BasicNameValuePair("lang", "CHS"), //
                new BasicNameValuePair("id", userid),
                new BasicNameValuePair("page", String.valueOf(page)), //
                new BasicNameValuePair("count", FRIEND_LIST_PAGE_COUNT) //
                );
        
        return (Group<User>) mHttpApi.doHttpRequest(httpGet,
                new GroupParser(new UserParser()));
    }
    
    /**
     * @param messageId
     * @return
     */
    @SuppressWarnings("unchecked")
    public Group<Comment> commentlist(String messageId) throws JianjianException,
    JianjianError, IOException{
        HttpGet httpGet = mHttpApi.createHttpGet(fullUrl(URL_API_COMMENT_LIST), //
                new BasicNameValuePair("source", "jianjian"), //
                new BasicNameValuePair("lang", "CHS"), //
                new BasicNameValuePair("id", messageId) //
                );
        
        return (Group<Comment>) mHttpApi.doHttpRequest(httpGet,
                new GroupParser(new CommentParser()));
    }
    
    /**
     * @param id
     * @param body
     * @return
     */
    @SuppressWarnings("unchecked")
    public Comment sendComment(String id, String body) throws JianjianException,
    JianjianError, IOException{
        
        HttpGet httpGet = mHttpApi.createHttpGet(fullUrl(URL_API_COMMENT_LIST), //
                new BasicNameValuePair("source", "jianjian"), //
                new BasicNameValuePair("lang", "CHS"), //
                new BasicNameValuePair("id", id),
                new BasicNameValuePair("body", body)//
                );
        
        return (Comment) mHttpApi.doHttpRequest(httpGet,
                new CommentParser());
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
        String photoName;
        if(price == null||price == ""){
            price = " ";
        }
        if(recommendDes == null || recommendDes ==""){
            recommendDes = " ";
        }
        checkinBody = productName + "++" + price + "++" + recommendDes + "(from jianjian)";
        
        /*
        if(mPhotoFile != null){
            photoPath = mPhotoFile.getPath();
            photoName = mPhotoFile.getName();
            File file = new File(photoPath);
            FileInputStream fileInputStream = new FileInputStream(file);
        }else {
            photoPath = null;
            photoName = null;
        }*/
        
        String BOUNDARY = "******";
        String lineEnd = "\r\n"; 
        String twoHyphens = "--";
        int maxBufferSize = 8192;
        

        URL url = new URL(fullUrl(URL_API_CHECK_IN));
        HttpURLConnection conn = mHttpApi.createHttpURLConnectionPost(url, BOUNDARY);
        
        conn.setRequestProperty("Authorization", "Basic " +  Base64Coder.encodeString(username + ":" + password));
        
        // We are always saving the image to a jpg so we can use .jpg as the extension below.
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream()); 

        //write source
        dos.writeBytes(twoHyphens + BOUNDARY + lineEnd); 
        dos.writeBytes("Content-Disposition: form-data; name=\"source\"" + lineEnd);
        dos.writeBytes(lineEnd); 
        dos.writeBytes(URLEncoder.encode("jianjian"));
        dos.writeBytes(lineEnd);
        
        //write source
        dos.writeBytes(twoHyphens + BOUNDARY + lineEnd); 
        dos.writeBytes("Content-Disposition: form-data; name=\"lang\"" + lineEnd);
        dos.writeBytes(lineEnd); 
        dos.writeBytes(URLEncoder.encode("zh_CN"));
        dos.writeBytes(lineEnd);
        
        //write guid
        dos.writeBytes(twoHyphens + BOUNDARY + lineEnd); 
        dos.writeBytes("Content-Disposition: form-data; name=\"guid\"" + lineEnd);
        dos.writeBytes(lineEnd); 
        dos.writeBytes(URLEncoder.encode(venueId));
        dos.writeBytes(lineEnd);
        
        //write source
        dos.writeBytes(twoHyphens + BOUNDARY + lineEnd); 
        dos.writeBytes("Content-Disposition: form-data; name=\"lat\"" + lineEnd);
        dos.writeBytes(lineEnd); 
        dos.writeBytes(URLEncoder.encode(geolat));
        dos.writeBytes(lineEnd);
        
        //write source
        dos.writeBytes(twoHyphens + BOUNDARY + lineEnd); 
        dos.writeBytes("Content-Disposition: form-data; name=\"lon\"" + lineEnd);
        dos.writeBytes(lineEnd); 
        dos.writeBytes(URLEncoder.encode(geolong));
        dos.writeBytes(lineEnd);
        
        //write source
        dos.writeBytes(twoHyphens + BOUNDARY + lineEnd); 
        dos.writeBytes("Content-Disposition: form-data; name=\"body\"" + lineEnd);
        dos.writeBytes(lineEnd); 
        dos.writeBytes(toGBK(checkinBody));
        dos.writeBytes(lineEnd);
        
        //write source
        dos.writeBytes(twoHyphens + BOUNDARY + lineEnd); 
        dos.writeBytes("Content-Disposition: form-data; name=\"syncs\"" + lineEnd);
        dos.writeBytes(lineEnd); 
        dos.writeBytes("");
        dos.writeBytes(lineEnd);
        
        
        //write image
        if(mPhotoFile != null){
            photoPath = mPhotoFile.getPath();
            photoName = mPhotoFile.getName();
            File file = new File(photoPath);
            FileInputStream fileInputStream = new FileInputStream(file);
            dos.writeBytes(twoHyphens + BOUNDARY + lineEnd); 
            dos.writeBytes("Content-Disposition: form-data; name=\"attachment_photo\";filename=\"" + photoName +"\"" + lineEnd); 
            dos.writeBytes("Content-Type: " + "image/jpeg" + lineEnd);
            dos.writeBytes(lineEnd); 
            
            int bytesAvailable = fileInputStream.available(); 
            int bufferSize = Math.min(bytesAvailable, maxBufferSize); 
            byte[] buffer = new byte[bufferSize]; 
            
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize); 
            int totalBytesRead = bytesRead;
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize); 
                bytesAvailable = fileInputStream.available(); 
                bufferSize = Math.min(bytesAvailable, maxBufferSize); 
                bytesRead = fileInputStream.read(buffer, 0, bufferSize); 
                totalBytesRead = totalBytesRead  + bytesRead;
            }
            dos.writeBytes(lineEnd); 
            
            fileInputStream.close(); 
            
        }

        dos.writeBytes(twoHyphens + BOUNDARY + twoHyphens + lineEnd); 
        
        
        dos.flush(); 
        dos.close(); 
        
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),HTTP.UTF_8));
        StringBuilder response = new StringBuilder();
        String responseLine = "";
        while ((responseLine = in.readLine()) != null) {
            response.append(responseLine);
        }
        in.close();
        
        try {
            return (RecommendMsg)JSONUtils.consume(new RecommendMsgParser(), response.toString());
        } catch (Exception ex) {
            throw new JianjianParseException(
                    "Error parsing user photo upload response, invalid json.");
        }
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
    
    private String getResponseBodyAsString(HttpResponse response) throws IOException {
        String html = null;
        GZIPInputStream gzin;
        
        if (response.getEntity().getContentEncoding() != null && response.getEntity().getContentEncoding().getValue().toLowerCase().indexOf("gzip") > -1) {
            InputStream is = response.getEntity().getContent();
            gzin = new GZIPInputStream(is);

            InputStreamReader isr = new InputStreamReader(gzin, "iso-8859-1");
            java.io.BufferedReader br = new java.io.BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String tempbf;
            while (( tempbf = br.readLine() ) != null) {
                sb.append(tempbf);
                sb.append("\r\n");
            }
            isr.close();
            gzin.close();
            html = sb.toString();
            html = new String(html.getBytes("iso-8859-1"), "utf-8");
        } else {
            HttpEntity entity = response.getEntity();
            html = EntityUtils.toString(entity, "utf-8");
            if (entity != null) {
                entity.consumeContent();
            }
        }
        return html;
    }
    

    public String toGBK(String str) {
        String output = null;
      try {
          output = new String(str.getBytes(),"iso-8859-1");
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }
         return output;
    }









}
