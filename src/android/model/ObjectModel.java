package com.bjzjns.hxplugin.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhaolei on 2016/7/26.
 */
public class ObjectModel extends BaseModel implements Parcelable {

    public String id;

    public ObjectModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
    }

    protected ObjectModel(Parcel in) {
        this.id = in.readString();
    }

    public static final Creator<ObjectModel> CREATOR = new Creator<ObjectModel>() {
        @Override
        public ObjectModel createFromParcel(Parcel source) {
            return new ObjectModel(source);
        }

        @Override
        public ObjectModel[] newArray(int size) {
            return new ObjectModel[size];
        }
    };
}
