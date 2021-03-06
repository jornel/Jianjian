/**
 * 
 */
package com.liangshan.jianjian.parsers.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.liangshan.jianjian.types.Comment;

/**
 * @author jornel
 *
 */
public class CommentParser extends AbstractParser<Comment> {

    /* (non-Javadoc)
     * @see com.liangshan.jianjian.parsers.json.AbstractParser#parse(org.json.JSONObject)
     */
    @Override
    public Comment parse(JSONObject json) throws JSONException {
        Comment comments = new Comment();
        
        if (json.has("id")) {
            comments.setFragmentId(json.getString("id"));
        } 
        
        if (json.has("created_on")) {
            comments.setCreateDate(json.getString("created_on"));
        }
        if (json.has("user")) {
            comments.setFromUser(new UserParser().parse(json.getJSONObject("user")));
        }
        if (json.has("body")) {
            comments.setBody(json.getString("body"));
        }
        
        return comments;
    }

}
