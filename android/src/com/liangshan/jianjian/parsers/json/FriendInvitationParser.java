/**
 * Copyright 2011
 */
package com.liangshan.jianjian.parsers.json;


import org.json.JSONException;
import org.json.JSONObject;

import com.liangshan.jianjian.types.FriendInvitation;
import com.liangshan.jianjian.types.User;



/**
 * @date Dec 26, 2011
 * @author Joe Chen
 */
public class FriendInvitationParser extends AbstractParser<FriendInvitation> {

    @Override
    public FriendInvitation parse(JSONObject json) throws JSONException {
        
        FriendInvitation inv =new FriendInvitation();

        User user = new User();

        if (json.has("email")) {
            user.setEmail(json.getString("email"));
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
        
        inv.setFromUser(user);
              
        return inv;
    }
    
}