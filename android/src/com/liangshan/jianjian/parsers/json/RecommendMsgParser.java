/**
 * 
 */
package com.liangshan.jianjian.parsers.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.liangshan.jianjian.types.JianjianType;
import com.liangshan.jianjian.types.RecommendMsg;

/**
 * @author jornel
 *
 */
public class RecommendMsgParser extends AbstractParser<RecommendMsg> {
    
    @Override
    public RecommendMsg parse(JSONObject json) throws JSONException {
        RecommendMsg message = new RecommendMsg();
        return message;
        
    }

}
