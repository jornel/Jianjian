/**
 * Copyright 2010
 */
package com.liangshan.jianjian.parsers.json;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.liangshan.jianjian.types.JianjianType;


/**
 * @date 
 * @author Joe Chen
 *
 */
public abstract class AbstractParser<T extends JianjianType> implements Parser<T> {

    /** 
     * All derived parsers must implement parsing a JSONObject instance of themselves. 
     */
    public abstract T parse(JSONObject json) throws JSONException;
    
    /**
     * Only the GroupParser needs to implement this.
     */
    public T parse(JSONArray array) throws JSONException {
        throw new JSONException("Unexpected JSONArray parse type encountered.");
    }
    
}