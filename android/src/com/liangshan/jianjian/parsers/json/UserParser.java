/**
 * Copyright 2011
 */
package com.liangshan.jianjian.parsers.json;


import org.json.JSONException;
import org.json.JSONObject;

import com.liangshan.jianjian.types.User;



/**
 * @date July 13, 2010
 * @author Mark Wyszomierski (markww@gmail.com)
 */
public class UserParser extends AbstractParser<User> {

    @Override
    public User parse(JSONObject json) throws JSONException {

        User user = new User();

        if (json.has("email")) {
            user.setEmail(json.getString("email"));
        }
        if (json.has("friends_num")) {
            user.setFriendCount(json.getInt("friends_num"));
        }
        if (json.has("friendsincommon")) {
            //user.setFriendsInCommon(
            //    new GroupParser(
            //        new UserParser()).parse(json.getJSONArray("friendsincommon")));
        }  
        if (json.has("friendstatus")) {
            user.setFriendstatus(json.getString("friendstatus"));
        }
        if (json.has("sex")) {
            user.setGender(json.getString("sex"));
        }
        if (json.has("id")) {
            user.setUserid(json.getString("id"));
        }
        if (json.has("msisdn")) {
            user.setMsisdn(json.getString("msisdn"));
        }
        if (json.has("avatar")) {
            user.setPhoto(json.getString("avatar"));
        }
        if (json.has("name")) {
            user.setUsername(json.getString("name"));
        }
        if (json.has("nick")) {
            user.setNick(json.getString("nick"));
        }
        if (json.has("city")) {
            user.setCity(json.getString("city"));
        }
        if (json.has("points_total")) {
            user.setPoints(json.getInt("points_total"));
        }
        if (json.has("badges_num")) {
            user.setBadgesCount(json.getInt("badges_num"));
        }
        
        
 
        return user;
    }
    
}