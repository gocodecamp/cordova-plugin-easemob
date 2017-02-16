package com.bjzjns.hxplugin.model;

import android.os.Parcel;
import android.os.Parcelable;


public class VideoEntityModel extends BaseModel implements Parcelable {
    public int ID;
    public String title;
    public String filePath;
    public int size;
    public int duration;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ID);
        dest.writeString(this.title);
        dest.writeString(this.filePath);
        dest.writeInt(this.size);
        dest.writeInt(this.duration);
    }

    public VideoEntityModel() {
    }

    protected VideoEntityModel(Parcel in) {
        this.ID = in.readInt();
        this.title = in.readString();
        this.filePath = in.readString();
        this.size = in.readInt();
        this.duration = in.readInt();
    }

    public static final Creator<VideoEntityModel> CREATOR = new Creator<VideoEntityModel>() {
        @Override
        public VideoEntityModel createFromParcel(Parcel source) {
            return new VideoEntityModel(source);
        }

        @Override
        public VideoEntityModel[] newArray(int size) {
            return new VideoEntityModel[size];
        }
    };
}
