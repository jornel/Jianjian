/**
 * 
 */
package com.liangshan.jianjian.parsers.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.liangshan.jianjian.types.Event;
import com.liangshan.jianjian.types.JianjianType;
import com.liangshan.jianjian.types.RecommendMsg;

/**
 * @author jornel
 *
 */
public class EventParser extends AbstractParser<Event> {
    
    @Override
    public Event parse(JSONObject json) throws JSONException {
        Event event = new Event();
        
        if (json.has("created_on")) {
            event.setCreateDate(json.getString("created_on"));
        } 
        if (json.has("type")) {
            event.setType(json.getString("type"));
        } 
        if (json.has("id")) {
            event.setEventId(json.getString("id"));
        } 
        if (event.getType() == "checkin"){
            event.setFragment(new RecommendMsgParser().parse(json.getJSONObject("status")));
        }
        
        return event;
        
    }

}
