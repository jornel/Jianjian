/**
 * 
 */
package com.liangshan.jianjian.types;


import java.util.ArrayList;

import com.liangshan.jianjian.util.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author ezhuche
 *
 */
public class RecommendMsg extends Fragment implements JianjianType,Parcelable{
    
    
    
    private String mPrice;
    private String mDescription;
    private Boolean mIsPrivate;
    private int mNumComments;
    private String mParentId;
    private String mUserId;    
    private Product mProduct;
    private ArrayList<String> mPhoto;
    
    public RecommendMsg(){
    }
    
    public RecommendMsg(Parcel in) {
        super(in);
        mPrice = ParcelUtils.readStringFromParcel(in);
        mDescription = ParcelUtils.readStringFromParcel(in);
        mIsPrivate = in.readInt() == 1;
        mNumComments = in.readInt();
        mParentId = ParcelUtils.readStringFromParcel(in);
        mUserId = ParcelUtils.readStringFromParcel(in);
        
        int photosize = in.readInt();
        if(photosize != 0){
            mPhoto = new ArrayList<String>();
            for (int i = 0; i < photosize; i++) {
                mPhoto.add(ParcelUtils.readStringFromParcel(in));
            }
        }
               
        if (in.readInt() == 1) {
            mProduct = in.readParcelable(Product.class.getClassLoader());
        }


            
    }
    
    public static final RecommendMsg.Creator<RecommendMsg> CREATOR = new Parcelable.Creator<RecommendMsg>() {
        public RecommendMsg createFromParcel(Parcel in) {
            return new RecommendMsg(in);
        }

        @Override
        public RecommendMsg[] newArray(int size) {
            return new RecommendMsg[size];
        }
    };

    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        ParcelUtils.writeStringToParcel(out, mPrice);
        ParcelUtils.writeStringToParcel(out, mDescription);
        if(mIsPrivate == true){
            out.writeInt(1);
        } else {
            out.writeInt(0);
        }
        out.writeInt(mNumComments);
        ParcelUtils.writeStringToParcel(out, mParentId);
        ParcelUtils.writeStringToParcel(out, mUserId);
        
        if ( mPhoto!= null && mPhoto.size() != 0) {
            out.writeInt(mPhoto.size());
            for (int i = 0; i < mPhoto.size(); i++) {
                ParcelUtils.writeStringToParcel(out,mPhoto.get(i));
            }
        } else {
            out.writeInt(0);
        }
        if(mProduct != null){
            out.writeInt(1);
            out.writeParcelable(mProduct, flags);
        } else {
            out.writeInt(0);
        }


    }
    
    /* (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }
    

    public Product getProduct() {
        return mProduct;
    }
    public void setProduct(Product pro) {
        mProduct = pro;
    }
    public String getPrice() {
        return mPrice;
    }
    public void setPrice(String price) {
        mPrice = price;
    }
    public String getDescription() {
        return mDescription;
    }
    public void setDescription(String description) {
        mDescription = description;
    }
    public ArrayList<String> getPhoto() {
        return mPhoto;
    }
    public void setPhoto(ArrayList<String> photo) {
        mPhoto = photo;
    }
    
    public Boolean getIsPrivate() {
        return mIsPrivate;
    }
    public void setIsPrivate(String isprivate) {
        if(isprivate.equalsIgnoreCase("true")){
            mIsPrivate = true;
        }else {
            mIsPrivate = false;
        }
    }
    public int getNumComments() {
        return mNumComments;
    }
    public void setNumComments(String num_comments) {
        mNumComments = Integer.parseInt(num_comments);
    }
    public String getParentId() {
        return mParentId;
    }
    public void setParentId(String id) {
        mParentId = id;
    }
    
    public String getUserId() {
        return mUserId;
    }
    public void setUserId(String id) {
        mUserId = id;
    }
    
    
   


}
