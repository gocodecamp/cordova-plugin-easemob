package com.bjzjns.hxplugin.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by zhaolei on 2016/7/26.
 */
public class ConversationListModel extends BaseModel implements Parcelable {
    public List<ConversationItemModel> conversationList;

    public ConversationListModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.conversationList);
    }

    protected ConversationListModel(Parcel in) {
        this.conversationList = in.createTypedArrayList(ConversationItemModel.CREATOR);
    }

    public static final Creator<ConversationListModel> CREATOR = new Creator<ConversationListModel>() {
        @Override
        public ConversationListModel createFromParcel(Parcel source) {
            return new ConversationListModel(source);
        }

        @Override
        public ConversationListModel[] newArray(int size) {
            return new ConversationListModel[size];
        }
    };
}
