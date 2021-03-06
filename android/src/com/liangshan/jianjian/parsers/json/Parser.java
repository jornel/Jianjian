/**
 * Copyright 2011
 */

package com.liangshan.jianjian.parsers.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.liangshan.jianjian.types.JianjianType;

/**
 * @date 
 * @author
 */
public interface Parser<T extends JianjianType> {

    public abstract T parse(JSONObject json) throws JSONException;

    /**
     * @param obj
     * @return
     */
    public T parse(JSONArray obj) throws JSONException;
}
