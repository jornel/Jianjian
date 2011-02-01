/**
 * 
 */
package com.liangshan.jianjian.types;

import com.joelapenna.foursquare.types.User;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author jornel
 *
 */
public class User implements Parcelable {
    public static final User.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

}
