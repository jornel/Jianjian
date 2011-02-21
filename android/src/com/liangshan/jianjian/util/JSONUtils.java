/**
 * Copyright 2010 Mark Wyszomierski
 */

package com.liangshan.jianjian.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.liangshan.jianjian.android.error.JianjianException;
import com.liangshan.jianjian.android.error.JianjianParseException;
import com.liangshan.jianjian.general.Jianjian;
import com.liangshan.jianjian.parsers.json.Parser;
import com.liangshan.jianjian.types.JianjianType;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;




public class JSONUtils {
    
    private static final boolean DEBUG = Jianjian.DEBUG;
    private static final Logger LOG = Logger.getLogger(JSONUtils.class.getCanonicalName());
    
    /**
     * Takes a parser, a json string, and returns a foursquare type.
     */
    public static JianjianType consume(Parser<? extends JianjianType> parser, String content)
        throws JianjianParseException, JianjianException, IOException {
        
        if (DEBUG) {
            LOG.log(Level.FINE, "http response: " + content);
        }
        
        try {
            // The v1 API returns the response raw with no wrapper. Depending on the
            // type of API call, the content might be a JSONObject or a JSONArray.
            // Since JSONArray does not derive from JSONObject, we need to check for
            // either of these cases to parse correctly.
            JSONObject json = new JSONObject(content);
            Iterator<String> it = (Iterator<String>)json.keys();
            if (it.hasNext()) {
                String key = (String)it.next();
                if (key.equals("error")) {
                    throw new JianjianException(json.getString(key));
                } else {
                    Object obj = json.get(key);
                    if (obj instanceof JSONArray) {
                        return parser.parse((JSONArray)obj);
                    } else {
                        return parser.parse((JSONObject)obj);
                    }
                }
            } else {
                throw new JianjianException("Error parsing JSON response, object had no single child key.");
            }
            
        } catch (JSONException ex) {
            throw new JianjianException("Error parsing JSON response: " + ex.getMessage());
        }
    }
}