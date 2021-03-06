/**
 * 
 */
package com.liangshan.jianjian.general;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.graphics.Bitmap;

import com.liangshan.jianjian.android.R;
import com.liangshan.jianjian.android.error.JianjianError;
import com.liangshan.jianjian.android.error.JianjianException;
import com.liangshan.jianjian.general.Jianjian.JLocation;
import com.liangshan.jianjian.types.Comment;
import com.liangshan.jianjian.types.Event;
import com.liangshan.jianjian.types.FriendInvitation;
import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.RecommendMsg;
import com.liangshan.jianjian.types.User;
import com.liangshan.jianjian.types.Venue;

/**
 * @author jornel
 *
 */
public class Jianjian {
    
    private static final Logger LOG = Logger.getLogger("com.liangshan.jianjian");
    public static final boolean DEBUG = false;

    private static final String JIANJIAN_API_DOMAIN = "api.jiepang.com";
    public static final String JIANJIAN_MOBILE_SIGNUP = "http://jiepang.com/m/signup";
    public static final String MALE = "male";
    public static final String FEMALE = "female";
    
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
        
        if (location != null) {
            return mJianjianV1.showUser(user, location.geolat, location.geolong,
                    location.geohacc, location.geovacc, location.geoalt);
        } else {
            return mJianjianV1.showUser(user, null, null, null, null, null);
        }
    }
    
    /**
     * @param location 
     * @param page 
     * @return
     */
    @V1
    public Group<Venue> getVenuesByLocation(JLocation location, int page) 
            throws JianjianException, JianjianError, IOException{
        
        if(location != null){
            return mJianjianV1.getVenuesByLocation(location.geolat,location.geolong,page);
        }else {
            throw new JianjianException("can't get the Venues due to null location");
            
        }
        
    }
    
    /**
     * @param string
     * @param string2
     * @param string3
     * @param string4
     * @param mPhoto
     * @param createJianjianLocation
     * @return
     */
    @V1
    public RecommendMsg recommendItToAllFriends(String productName, String price, String recommendDes,
            String VenueId, File mPhotoFile, String username, String password, JLocation location) 
            throws JianjianException, JianjianError, IOException{
        
        if(location != null){
            return mJianjianV1.recommendItToAllFriends(location.geolat,location.geolong,productName,price,recommendDes,VenueId,mPhotoFile,username,password);
        }else {
            throw new JianjianException("failed to recommend the product...");
            
        }
    }
    
    /**
     * @param createJianjianLocation
     * @return
     */
    @V1
    public Group<Event> getEvents(int page, JLocation location) 
            throws JianjianException, JianjianError, IOException{
        
        return mJianjianV1.recommends(page, location.geolat, location.geolong, location.geohacc,
                location.geovacc, location.geoalt);
    }
    
    /**
     * @param object 
     * @param string
     * @param object
     * @return
     */
    @V1
    public Group<RecommendMsg> history(String userid, String sinceid, int page) 
            throws JianjianException, JianjianError, IOException{
        return mJianjianV1.history(userid, sinceid, page);

    }
    
    /**
     * @param userid
     * @param object
     * @param mPage
     * @return
     */
    @V1
    public Group<User> friendlist(String userid, int mPage) 
            throws JianjianException, JianjianError, IOException{
        return mJianjianV1.friendlist(userid, mPage);
    }
    
    /**
     * @param fragmentId
     * @return
     */
    @V1
    public Group<Comment> commentlist(String messageId) 
            throws JianjianException, JianjianError, IOException{
        return mJianjianV1.commentlist(messageId);
    }
    
    /**
     * @param fragmentId
     * @param mBody
     * @return
     */
    @V1
    public Comment sendComment(String id, String body) 
            throws JianjianException, JianjianError, IOException{
        
        return mJianjianV1.sendComment(id,body);
    }
    
    /**
     * @param mUserId
     * @return
     */
    @V1
    public User friendApprove(String mUserId) 
            throws JianjianException, JianjianError, IOException{
         
        return mJianjianV1.approveFriend(mUserId);
    }
    
    /**
     * @param mUserId
     * @return
     */
    @V1
    public User friendIgnore(String mUserId) 
            throws JianjianException, JianjianError, IOException{
         
        return mJianjianV1.ignoreFriend(mUserId);
    }

    /**
     * @param mUserId
     * @return
     */
    @V1
    public User friendSendrequest(String mUserId) 
            throws JianjianException, JianjianError, IOException{
        
        return mJianjianV1.sendAddFriendRequest(mUserId);
    }
    
    /**
     * @param mUserId
     * @return
     */
    @V1
    public Group<FriendInvitation> getFriendInvitations(String mUserId) 
            throws JianjianException, JianjianError, IOException{
        
        return mJianjianV1.getFriendInvitations(mUserId);
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
