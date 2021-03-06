/**
 * 
 */
package com.liangshan.jianjian.types;

import com.liangshan.jianjian.util.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author jornel
 *
 */
public class Comment extends Fragment implements JianjianType, Parcelable {
    private String mBody;
    
    public Comment(){
    }
    public Comment(Parcel in) {
        super(in);
        mBody = ParcelUtils.readStringFromParcel(in);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        ParcelUtils.writeStringToParcel(out, mBody);

    }
    
    public String getBody() {
        return mBody;
    }
    public void setBody(String body) {
        mBody = body;
    }

}
