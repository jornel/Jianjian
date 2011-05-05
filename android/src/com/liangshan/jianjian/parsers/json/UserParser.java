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
        if (json.has("friendcount")) {
            user.setFriendCount(json.getInt("friendcount"));
        }
        if (json.has("friendsincommon")) {
            //user.setFriendsInCommon(
            //    new GroupParser(
            //        new UserParser()).parse(json.getJSONArray("friendsincommon")));
        }  
        if (json.has("friendstatus")) {
            user.setFriendstatus(json.getString("friendstatus"));
        }
        if (json.has("gender")) {
            user.setGender(json.getString("gender"));
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
 
        return user;
    }
    
}