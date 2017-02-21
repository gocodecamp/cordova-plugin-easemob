package com.bjzjns.hxplugin.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.hyphenate.easeui.model.MessageExtModel;

/**
 * Created by zhaolei_zjns on 2016/10/12.
 */
public class ConversationItemModel extends BaseModel implements Parcelable {

    // 会话ID
    public String conversationId;
    // 消息时间
    public String timestamp;
    // 消息未读数量
    public String unreadMessagesCount;
    // 消息内容
    public String messageBodyContent;
    // 消息类型 TXT IMAGE VIDEO LOCATION VOICE FILE CMD
    public String messageBodyType;
    // 消息内容信息
    public MessageExtModel ext;


    public ConversationItemModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.conversationId);
        dest.writeString(this.timestamp);
        dest.writeString(this.unreadMessagesCount);
        dest.writeString(this.messageBodyContent);
        dest.writeString(this.messageBodyType);
        dest.writeParcelable(this.ext, flags);
    }

    protected ConversationItemModel(Parcel in) {
        this.conversationId = in.readString();
        this.timestamp = in.readString();
        this.unreadMessagesCount = in.readString();
        this.messageBodyContent = in.readString();
        this.messageBodyType = in.readString();
        this.ext = in.readParcelable(MessageExtModel.class.getClassLoader());
    }

    public static final Creator<ConversationItemModel> CREATOR = new Creator<ConversationItemModel>() {
        @Override
        public ConversationItemModel createFromParcel(Parcel source) {
            return new ConversationItemModel(source);
        }

        @Override
        public ConversationItemModel[] newArray(int size) {
            return new ConversationItemModel[size];
        }
    };
}
