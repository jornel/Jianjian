/**
 * Copyright 2010 Mark Wyszomierski
 */

package com.liangshan.jianjian.parsers.json;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.liangshan.jianjian.types.Group;
import com.liangshan.jianjian.types.JianjianType;

import java.util.Iterator;
import java.util.logging.Level;

/**
 * Reference:
 * http://www.json.org/javadoc/org/json/JSONObject.html
 * http://www.json.org/javadoc/org/json/JSONArray.html
 * 
 * @author 
 * @param <T>
 */
public class GroupParser extends AbstractParser<Group> {

    private Parser<? extends JianjianType> mSubParser;

    public GroupParser(Parser<? extends JianjianType> subParser) {
        mSubParser = subParser;
    }
    
    /**
     * When we encounter a JSONObject in a GroupParser, we expect one attribute
     * named 'type', and then another JSONArray attribute.
     */
    public Group<JianjianType> parse(JSONObject json) throws JSONException {

        Group<JianjianType> group = new Group<JianjianType>();
        
        Iterator<String> it = (Iterator<String>)json.keys();
        while (it.hasNext()) {
            String key = it.next();
            if (key.equals("has_more")) {
                group.setHasMore(Boolean.valueOf(json.getString(key)));
            } else if (key.equals("province")
                    ||key.equals("num_items")
                    ||key.equals("post")
                    ||key.equals("likes")){
                continue;
            }else {
                Object obj = json.get(key);
                if (obj instanceof JSONArray) {  
                    parse(group, (JSONArray)obj);
                } else {
                    throw new JSONException("Could not parse data.");
                }
            }
        }
        
        return group;
    }
    
    /**
     * Here we are getting a straight JSONArray and do not expect the 'type' attribute.
     */
    @Override
    public Group parse(JSONArray array) throws JSONException {
  
        Group<JianjianType> group = new Group<JianjianType>();
        parse(group, array);
        return group;
    }
    
    private void parse(Group group, JSONArray array) throws JSONException {
        for (int i = 0, m = array.length(); i < m; i++) {
            Object element = array.get(i);
            JianjianType item = null;
            if (element instanceof JSONArray) {
                item = mSubParser.parse((JSONArray)element);
            } else {
                item = mSubParser.parse((JSONObject)element);
            }
            
            group.add(item);
        }
    }
}
