package com.bjzjns.hxplugin.im;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolei on 2016/6/24.
 */
public class HXContext {

    private String SP_KEY_NOTIFICATION = "sp_key_notification";
    private String SP_KEY_SOUND = "sp_key_sound";
    private String SP_KEY_VIBRATE = "sp_key_vibrate";
    private String SP_KEY_SPEAKER = "sp_key_speaker";
    private String SP_KEY_CURRENT_USER = "sp_key_current_user";

    private static String SP_KEY_CHATROOM_OWNER_LEAVE = "sp_key_chatroom_owner_leave";
    private static String SP_KEY_DELETE_MESSAGES_WHEN_EXIT_GROUP = "sp_key_delete_messages_when_exit_group";
    private static String SP_KEY_AUTO_ACCEPT_GROUP_INVITATION = "sp_key_auto_accept_group_invitation";
    private static String SP_KEY_ADAPTIVE_VIDEO_ENCODE = "sp_key_adaptive_video_encode";


    protected Context context = null;
    protected Map<Key, Object> valueCache = new HashMap<Key, Object>();
    private static HXContext sInstance;

    public HXContext(Context context) {
        this.context = context;
        HXPreferenceManager.init(context);
    }

    /**
     * 设置振动并响铃
     *
     * @param paramBoolean
     */
    public void setSettingMsgNotification(boolean paramBoolean) {
        HXPreferenceManager.getInstance().setSettingMsgNotification(paramBoolean);
        valueCache.put(Key.VibrateAndPlayToneOn, paramBoolean);
    }

    /**
     * 是否是振动并响铃
     *
     * @return
     */
    public boolean getSettingMsgNotification() {
        Object val = valueCache.get(Key.VibrateAndPlayToneOn);

        if (val == null) {
            val = HXPreferenceManager.getInstance().getSettingMsgNotification();
            valueCache.put(Key.VibrateAndPlayToneOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    /**
     * 设置响铃
     *
     * @param paramBoolean
     */
    public void setSettingMsgSound(boolean paramBoolean) {
        HXPreferenceManager.getInstance().setSettingMsgSound(paramBoolean);
        valueCache.put(Key.PlayToneOn, paramBoolean);
    }

    /**
     * 是否响铃
     *
     * @return
     */
    public boolean getSettingMsgSound() {
        Object val = valueCache.get(Key.PlayToneOn);

        if (val == null) {
            val = HXPreferenceManager.getInstance().getSettingMsgSound();
            valueCache.put(Key.PlayToneOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    /**
     * 设置振动
     *
     * @param paramBoolean
     */
    public void setSettingMsgVibrate(boolean paramBoolean) {
        HXPreferenceManager.getInstance().setSettingMsgVibrate(paramBoolean);
        valueCache.put(Key.VibrateOn, paramBoolean);
    }

    /**
     * 是否振动
     *
     * @return
     */
    public boolean getSettingMsgVibrate() {
        Object val = valueCache.get(Key.VibrateOn);

        if (val == null) {
            val = HXPreferenceManager.getInstance().getSettingMsgVibrate();
            valueCache.put(Key.VibrateOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    /**
     * 设置外音
     *
     * @param paramBoolean
     */
    public void setSettingMsgSpeaker(boolean paramBoolean) {
        HXPreferenceManager.getInstance().setSettingMsgSpeaker(paramBoolean);
        valueCache.put(Key.SpakerOn, paramBoolean);
    }

    /**
     * 是否外音
     *
     * @return
     */
    public boolean getSettingMsgSpeaker() {
        Object val = valueCache.get(Key.SpakerOn);

        if (val == null) {
            val = HXPreferenceManager.getInstance().getSettingMsgSpeaker();
            valueCache.put(Key.SpakerOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }


    public void setDisabledGroups(List<String> groups) {
        valueCache.put(Key.DisabledGroups, groups);
    }

    public List<String> getDisabledGroups() {
        Object val = valueCache.get(Key.DisabledGroups);
        return (List<String>) val;
    }

    public void setDisabledIds(List<String> ids) {
        valueCache.put(Key.DisabledIds, ids);
    }

    public List<String> getDisabledIds() {
        Object val = valueCache.get(Key.DisabledIds);
        return (List<String>) val;
    }

    /**
     * 设置聊天室是否允许创建者离开
     *
     * @param value
     */
    public void allowChatroomOwnerLeave(boolean value) {
        HXPreferenceManager.getInstance().setSettingAllowChatroomOwnerLeave(value);
    }

    /**
     * 聊天室是否允许创建者离开
     */
    public boolean isChatroomOwnerLeaveAllowed() {
        return HXPreferenceManager.getInstance().getSettingAllowChatroomOwnerLeave();
    }

    /**
     * 设置离开群组后是否删除消息记录
     *
     * @param value
     */
    public void setDeleteMessagesAsExitGroup(boolean value) {
        HXPreferenceManager.getInstance().setDeleteMessagesAsExitGroup(value);
    }

    /**
     * 离开群组后是否删除消息记录
     *
     * @return
     */
    public boolean isDeleteMessagesAsExitGroup() {
        return HXPreferenceManager.getInstance().isDeleteMessagesAsExitGroup();
    }

    /**
     * 设置是否自动接受群组邀请
     *
     * @param value
     */
    public void setAutoAcceptGroupInvitation(boolean value) {
        HXPreferenceManager.getInstance().setAutoAcceptGroupInvitation(value);
    }

    /**
     * 是否自动接受群组邀请
     *
     * @return
     */
    public boolean isAutoAcceptGroupInvitation() {
        return HXPreferenceManager.getInstance().isAutoAcceptGroupInvitation();
    }


    public void setAdaptiveVideoEncode(boolean value) {
        HXPreferenceManager.getInstance().setAdaptiveVideoEncode(value);
    }

    public boolean isAdaptiveVideoEncode() {
        return HXPreferenceManager.getInstance().isAdaptiveVideoEncode();
    }

    enum Key {
        VibrateAndPlayToneOn,
        VibrateOn,
        PlayToneOn,
        SpakerOn,
        DisabledGroups,
        DisabledIds
    }
}
