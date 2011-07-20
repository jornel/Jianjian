/**
 * 
 */
package com.liangshan.jianjian.parsers.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.liangshan.jianjian.types.JianjianType;
import com.liangshan.jianjian.types.Product;
import com.liangshan.jianjian.types.RecommendMsg;

/**
 * @author jornel
 *
 */
public class RecommendMsgParser extends AbstractParser<RecommendMsg> {
    
    private static final String From_Jianjian = "(from jianjian)";

    @Override
    public RecommendMsg parse(JSONObject json) throws JSONException {
        RecommendMsg message = new RecommendMsg();
        
        if (json.has("id")) {
            message.setFragmentId(json.getString("id"));
        } 
        
        if (json.has("user")) {
            message.setFromUser(new UserParser().parse(json.getJSONObject("user")));
        } 
        
        if (json.has("is_private")) {
            message.setIsPrivate(json.getString("is_private"));
        } 
        
        if (json.has("type")) {
            message.setType(json.getString("type"));
        } 
        
        if (json.has("num_comments")) {
            message.setNumComments(json.getString("num_comments"));
        }

        if (json.has("photo")&&!json.isNull("photo")) {
            String[] photoUri = new String[5];
            if(json.getJSONObject("photo").has("url")){
                photoUri[0] = json.getJSONObject("photo").getString("url");
                message.setPhoto(photoUri);
            }
        }
        if (json.has("body")) {
            String recommend_body = json.getString("body");
            if(recommend_body.contains(From_Jianjian)){
                
                recommend_body = recommend_body.substring(0, recommend_body.indexOf(From_Jianjian)).replace("?", " ");
                String[] entity = recommend_body.split("\\u002B\\u002B");
                
                Product pro = new Product();
                pro.setName(entity[0]);
                String price = entity [1];
                String description = entity[2];
                if (json.has("location")) {
                    pro.setVenue(new VenueParser().parse(json.getJSONObject("location")));
                }
                
                message.setProduct(pro);
                message.setPrice(price);
                message.setDescription(description);
                
                
            }             
        }
        
        
        return message;
        
    }

}
