/**
 * Copyright 2009 Joe LaPenna
 */

package com.liangshan.jianjian.types;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Joe Chen
 */
public class Group<T extends JianjianType> extends ArrayList<T> implements JianjianType {

    private static final long serialVersionUID = 1L;

    private String mType;
    private Boolean hasMore;
    
    public Group() {
        super();
        hasMore = false;
        
    }
    
    public Group(Collection<T> collection) {
        super(collection);
    }

    public void setType(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }
    
    public void setHasMore(Boolean has_more) {
        hasMore = has_more;
    }

    public Boolean isHasMore() {
        return hasMore;
    }
    
}
