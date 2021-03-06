/**
 * Copyright 2010
 */

package com.liangshan.jianjian.http;


import com.liangshan.jianjian.android.error.JianjianException;
import com.liangshan.jianjian.android.error.JianjianParseException;
import com.liangshan.jianjian.general.Jianjian;
import com.liangshan.jianjian.parsers.json.Parser;
import com.liangshan.jianjian.types.JianjianType;
import com.liangshan.jianjian.util.JSONUtils;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;


/**
 * @author Joe Chen
 */
abstract public class AbstractHttpApi implements HttpApi {
    protected static final Logger LOG = Logger.getLogger(AbstractHttpApi.class.getCanonicalName());
    protected static final boolean DEBUG = Jianjian.DEBUG;

    private static final String DEFAULT_CLIENT_VERSION = "com.liangshan.jianjian";
    private static final String CLIENT_VERSION_HEADER = "User-Agent";
    private static final int TIMEOUT = 60;

    private final DefaultHttpClient mHttpClient;
    private final String mClientVersion;

    public AbstractHttpApi(DefaultHttpClient httpClient, String clientVersion) {
        mHttpClient = httpClient;
        if (clientVersion != null) {
            mClientVersion = clientVersion;
        } else {
            mClientVersion = DEFAULT_CLIENT_VERSION;
        }
    }
    
    /**
     * Create a thread-safe client. This client does not do redirecting, to allow us to capture
     * correct "error" codes.
     *
     * @return HttpClient
     */
    public static final DefaultHttpClient createHttpClient() {
        // Sets up the http part of the service.
        final SchemeRegistry supportedSchemes = new SchemeRegistry();

        // Register the "http" protocol scheme, it is required
        // by the default operator to look up socket factories.
        final SocketFactory sf = PlainSocketFactory.getSocketFactory();
        supportedSchemes.register(new Scheme("http", sf, 80));
        supportedSchemes.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        
        // Set some client http client parameter defaults.
        final HttpParams httpParams = createHttpParams();
        HttpClientParams.setRedirecting(httpParams, false);

        final ClientConnectionManager ccm = new ThreadSafeClientConnManager(httpParams,
                supportedSchemes);
        return new DefaultHttpClient(ccm, httpParams);
    }
    
    public HttpGet createHttpGet(String url, NameValuePair... nameValuePairs) {
        if (DEBUG) LOG.log(Level.FINE, "creating HttpGet for: " + url);
        String query = URLEncodedUtils.format(stripNulls(nameValuePairs), HTTP.UTF_8);
        HttpGet httpGet = new HttpGet(url + "?" + query);
        httpGet.addHeader(CLIENT_VERSION_HEADER, mClientVersion);
        if (DEBUG) LOG.log(Level.FINE, "Created: " + httpGet.getURI());
        return httpGet;
    }
    
    public HttpPost createHttpPost(String url, NameValuePair... nameValuePairs) {
        if (DEBUG) LOG.log(Level.FINE, "creating HttpPost for: " + url);
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(CLIENT_VERSION_HEADER, mClientVersion);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(stripNulls(nameValuePairs), HTTP.UTF_8));
        } catch (UnsupportedEncodingException e1) {
            throw new IllegalArgumentException("Unable to encode http parameters.");
        }
        if (DEBUG) LOG.log(Level.FINE, "Created: " + httpPost);
        return httpPost;
    }
    
    public HttpURLConnection createHttpURLConnectionPost(URL url, 
            String boundary) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);        
        conn.setDoOutput(true); 
        conn.setUseCaches(false); 
        conn.setConnectTimeout(TIMEOUT * 1000);
        conn.setRequestMethod("POST");

        conn.setRequestProperty(CLIENT_VERSION_HEADER, mClientVersion);
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("pragma", "no-cache");
        conn.setRequestProperty("accept-charset", "UTF-8");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
    
        return conn;
     }
    
    /**
     * @param httpRequest
     * @param parser
     * @return
     */
    public JianjianType executeHttpRequest(HttpRequestBase httpRequest,
            Parser<? extends JianjianType> parser) throws 
            JianjianParseException, JianjianException, IOException{
        if (DEBUG) LOG.log(Level.FINE, "doHttpRequest: " + httpRequest.getURI());
        
        httpRequest.addHeader("Accept-Encoding", "gzip");
        HttpResponse response = executeHttpRequest(httpRequest);
        if (DEBUG) LOG.log(Level.FINE, "executed HttpRequest for: "
                + httpRequest.getURI().toString());
        
        
        int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
            case 200:
                String content = getResponseBodyAsString(response);
                return JSONUtils.consume(parser, content);
                
            case 400:
                if (DEBUG) LOG.log(Level.FINE, "HTTP Code: 400");
                throw new JianjianException(
                        response.getStatusLine().toString(),
                        EntityUtils.toString(response.getEntity()));

            case 401:
                response.getEntity().consumeContent();
                if (DEBUG) LOG.log(Level.FINE, "HTTP Code: 401");
                throw new JianjianException(response.getStatusLine().toString());

            case 404:
                response.getEntity().consumeContent();
                if (DEBUG) LOG.log(Level.FINE, "HTTP Code: 404");
                throw new JianjianException(response.getStatusLine().toString());

            case 500:
                response.getEntity().consumeContent();
                if (DEBUG) LOG.log(Level.FINE, "HTTP Code: 500");
                throw new JianjianException("Foursquare is down. Try again later.");

            default:
                if (DEBUG) LOG.log(Level.FINE, "Default case for status code reached: "
                        + response.getStatusLine().toString());
                response.getEntity().consumeContent();
                throw new JianjianException("Error connecting to Foursquare: " + statusCode + ". Try again later.");
        }
         
    }
    
    
    
    /**
     * execute() an httpRequest catching exceptions and returning null instead.
     *
     * @param httpRequest
     * @return
     * @throws IOException
     */
    public HttpResponse executeHttpRequest(HttpRequestBase httpRequest) throws IOException {
        if (DEBUG) LOG.log(Level.FINE, "executing HttpRequest for: "
                + httpRequest.getURI().toString());
        try {
            mHttpClient.getConnectionManager().closeExpiredConnections();
            return mHttpClient.execute(httpRequest);
        } catch (IOException e) {
            httpRequest.abort();
            throw e;
        }
    }
    
    private List<NameValuePair> stripNulls(NameValuePair... nameValuePairs) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (int i = 0; i < nameValuePairs.length; i++) {
            NameValuePair param = nameValuePairs[i];
            if (param.getValue() != null) {
                if (DEBUG) LOG.log(Level.FINE, "Param: " + param);
                params.add(param);
            }
        }
        return params;
    }
    
    /**
     * Create the default HTTP protocol parameters.
     */
    private static final HttpParams createHttpParams() {
        final HttpParams params = new BasicHttpParams();

        // Turn off stale checking. Our connections break all the time anyway,
        // and it's not worth it to pay the penalty of checking every time.
        HttpConnectionParams.setStaleCheckingEnabled(params, false);

        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT * 1000);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        return params;
    }
    
    private String getResponseBodyAsString(HttpResponse response) throws IOException {
        String html = null;
        GZIPInputStream gzin;
        
        if (response.getEntity().getContentEncoding() != null && response.getEntity().getContentEncoding().getValue().toLowerCase().indexOf("gzip") > -1) {
            InputStream is = response.getEntity().getContent();
            gzin = new GZIPInputStream(is);

            InputStreamReader isr = new InputStreamReader(gzin, "iso-8859-1");
            java.io.BufferedReader br = new java.io.BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String tempbf;
            while (( tempbf = br.readLine() ) != null) {
                sb.append(tempbf);
                sb.append("\r\n");
            }
            isr.close();
            gzin.close();
            html = sb.toString();
            html = new String(html.getBytes("iso-8859-1"), "utf-8");
        } else {
            HttpEntity entity = response.getEntity();
            html = EntityUtils.toString(entity, "utf-8");
            if (entity != null) {
                entity.consumeContent();
            }
        }
        return html;
    }

}
