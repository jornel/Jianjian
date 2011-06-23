/**
 * Copyright 2011
 */

package com.liangshan.jianjian.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import com.liangshan.jianjian.android.error.JianjianException;
import com.liangshan.jianjian.android.error.JianjianParseException;
import com.liangshan.jianjian.parsers.json.Parser;
import com.liangshan.jianjian.types.JianjianType;



/**
 * @author Joe Chen
 */
public interface HttpApi {
    
    abstract public JianjianType doHttpRequest(HttpRequestBase httpRequest,
            Parser<? extends JianjianType> parser) throws 
            JianjianParseException, JianjianException, IOException;
    
    abstract public HttpGet createHttpGet(String url, NameValuePair... nameValuePairs);
    
    abstract public HttpPost createHttpPost(String url, NameValuePair... nameValuePairs);
    
    abstract public HttpURLConnection createHttpURLConnectionPost(URL url, String boundary)
    throws IOException; 

}
