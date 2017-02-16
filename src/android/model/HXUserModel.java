package com.bjzjns.hxplugin.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhaolei_zjns on 2017/2/15.
 */
public class HXUserModel extends BaseModel implements Parcelable {
    // 用户的环信聊天ID
    public String userHXId;
    // 用户在内部APP的ID
    public String userAppId;
    // 用户的密码
    public String password;
    // 用户的昵称
    public String nickName;
    // 用户的头像
    public String avatar;

    public HXUserModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userHXId);
        dest.writeString(this.userAppId);
        dest.writeString(this.password);
        dest.writeString(this.nickName);
        dest.writeString(this.avatar);
    }

    protected HXUserModel(Parcel in) {
        this.userHXId = in.readString();
        this.userAppId = in.readString();
        this.password = in.readString();
        this.nickName = in.readString();
        this.avatar = in.readString();
    }

    public static final Creator<HXUserModel> CREATOR = new Creator<HXUserModel>() {
        @Override
        public HXUserModel createFromParcel(Parcel source) {
            return new HXUserModel(source);
        }

        @Override
        public HXUserModel[] newArray(int size) {
            return new HXUserModel[size];
        }
    };
}
