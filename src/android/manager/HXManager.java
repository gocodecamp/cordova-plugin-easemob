package com.bjzjns.hxplugin.manager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.util.Pair;
import android.util.Log;

import com.bjzjns.hxplugin.ZJNSHXPlugin;
import com.bjzjns.hxplugin.activity.ChatActivity;
import com.bjzjns.hxplugin.im.HXContext;
import com.bjzjns.hxplugin.im.HXPreferenceManager;
import com.bjzjns.hxplugin.model.HXUserModel;
import com.bjzjns.hxplugin.tools.GsonUtils;
import com.bjzjns.hxplugin.tools.LogUtils;
import com.bjzjns.hxplugin.tools.PackageUtils;
import com.bjzjns.hxplugin.tools.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.model.MessageExtModel;
import com.hyphenate.easeui.model.MessageUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolei on 2016/6/23.
 */
public class HXManager {
    private static final String TAG = "HXManager";
    private static HXManager sInstance;
    private Context mContext;
    private EaseUI easeUI;
    private EMConnectionListener connectionListener;
    private boolean isGroupAndContactListenerRegisted;
    private EMMessageListener messageListener;
    private HXContext mHXContext;

    private HXManager() {

    }

    public synchronized static HXManager getInstance() {
        if (sInstance == null) {
            sInstance = new HXManager();
        }
        return sInstance;
    }

    public void init(Context context) {
        mHXContext = new HXContext(context);
        EMOptions options = initChatOptions();
        //options传null则使用默认的
        if (EaseUI.getInstance().init(context, options)) {
            mContext = context;
            //设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
            EMClient.getInstance().setDebugMode(false);
            //get easeui instance
            easeUI = EaseUI.getInstance();
            //调用easeui的api设置providers
            setEaseUIProviders();

            //设置全局监听
            setGlobalListeners();
//            broadcastManager = LocalBroadcastManager.getInstance(mContext);
        }
    }

    private EMOptions initChatOptions() {
        // 获取到EMChatOptions对象
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 设置是否需要已读回执
        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(false);

        //使用gcm和mipush时，把里面的参数替换成自己app申请的
        //设置google推送，需要的GCM的app可以设置此参数
//        options.setGCMNumber("324169311137");
        //在小米手机上当app被kill时使用小米推送进行消息提示，同GCM一样不是必须的
//        options.setMipushConfig("2882303761517426801", "5381742660801");
        //集成华为推送时需要设置
//        options.setHuaweiPushAppId("10492024");

        options.allowChatroomOwnerLeave(mHXContext.isChatroomOwnerLeaveAllowed());
        options.setDeleteMessagesAsExitGroup(mHXContext.isDeleteMessagesAsExitGroup());
        options.setAutoAcceptGroupInvitation(mHXContext.isAutoAcceptGroupInvitation());

        return options;
    }

    protected void setEaseUIProviders() {

        //需要easeui库显示用户头像和昵称设置此provider
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });

        //不设置，则使用easeui默认的
        easeUI.setSettingsProvider(new EaseUI.EaseSettingsProvider() {

            @Override
            public boolean isSpeakerOpened() {
                return mHXContext.getSettingMsgSpeaker();
            }

            @Override
            public boolean isMsgVibrateAllowed(EMMessage message) {
                return mHXContext.getSettingMsgVibrate();
            }

            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
                return mHXContext.getSettingMsgSound();
            }

            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {
                if (message == null) {
                    return mHXContext.getSettingMsgNotification();
                }
                if (!mHXContext.getSettingMsgNotification()) {
                    return false;
                } else {
                    //如果允许新消息提示
                    //屏蔽的用户和群组不提示用户
                    String chatUsename = null;
                    List<String> notNotifyIds = null;
                    // 获取设置的不提示新消息的用户或者群组ids
                    if (message.getChatType() == EMMessage.ChatType.Chat) {
                        chatUsename = message.getFrom();
                        notNotifyIds = mHXContext.getDisabledIds();
                    } else {
                        chatUsename = message.getTo();
                        notNotifyIds = mHXContext.getDisabledGroups();
                    }

                    if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });
        //设置表情provider
//        easeUI.setEmojiconInfoProvider(new EaseUI.EaseEmojiconInfoProvider() {
//
//            @Override
//            public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
//                EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
//                for(EaseEmojicon emojicon : data.getEmojiconList()){
//                    if(emojicon.getIdentityCode().equals(emojiconIdentityCode)){
//                        return emojicon;
//                    }
//                }
//                return null;
//            }
//
//            @Override
//            public Map<String, Object> getTextEmojiconMapping() {
//                //返回文字表情emoji文本和图片(resource id或者本地路径)的映射map
//                return null;
//            }
//        });

        //不设置，则使用easeui默认的
        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                //修改标题,这里使用默认
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //设置小图标，这里为默认
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = EaseCommonUtils.getMessageDigest(message, mContext);
                if (message.getType() == EMMessage.Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                String extContent = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXT, "");
                MessageExtModel model = GsonUtils.fromJson(extContent, MessageExtModel.class);

                MessageUser toUser = null;
                if (null != model) {
                    toUser = model.touser;
                }
                String name = message.getFrom();
                if (null != toUser) {
                    name = toUser.nickname;
                }
                return name + ":" + ticker;
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                return fromUsersNum + "个朋友，发来了" + messageNum + "条消息";
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                //设置点击通知栏跳转事件
                Intent intent = null;
                if (PackageUtils.isAppOnForeground(mContext)) {
                    intent = getIMMessageIntent(message);
                } else {
                    intent = HXManager.getInstance().getIMMessageIntent(message);
                }
                return intent;
            }
        });
    }

    public Intent getIMMessageIntent(EMMessage message) {
        ApplicationInfo appInfo = null;
        String hxMetaData = "bjzjnssinacom#styleme";
        try {
            appInfo = mContext.getPackageManager()
                    .getApplicationInfo(mContext.getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (null != appInfo) {
            hxMetaData = appInfo.metaData.getString("EASEMOB_APPKEY");
        }
        Intent intent = null;
        String extContent = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXT, "");
        MessageExtModel extModel = GsonUtils.fromJson(extContent, MessageExtModel.class);
        MessageUser user;
        EMMessage.ChatType chatType = message.getChatType();
        if (null != extModel) {
            if (chatType == EMMessage.ChatType.Chat) {
                user = extModel.user;
                extModel.user = extModel.touser;
                extModel.touser = user;
                intent = new Intent(mContext, ChatActivity.class);
            } else {
                extModel.user.easemobile_id = getUserHXId();
                extModel.user.nickname = getUserNickName();
                extModel.user.username = getUserId();
                extModel.user.head_thumb = getUserAvatar();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(EaseConstant.EXTRA_EXT_MODEL, GsonUtils.toJson(extModel));
            intent.putExtra(EaseConstant.EXTRA_HXMETADATA, hxMetaData);
            intent.putExtra(EaseConstant.EXTRA_CURRENTUSERID, getUserHXId());
        }
        return intent;
    }

    private EaseUser getUserInfo(String username) {
        if (username.equalsIgnoreCase(getUserHXId())) {
            EaseUser user = new EaseUser(username);
            user.setNick(getUserNickName());
            user.setAvatar(getUserAvatar());
            return user;
        }
        return null;
    }

    /**
     * 设置全局事件监听
     */
    protected void setGlobalListeners() {
        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                if (error == EMError.USER_REMOVED) {
                    LogUtils.d("HX", "环信账号被移除");
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    LogUtils.d("HX", "环信账号在别的设备登录");

                }
            }

            @Override
            public void onConnected() {
            }
        };

        //注册连接监听
        EMClient.getInstance().addConnectionListener(connectionListener);
        //注册群组和联系人监听
        registerGroupAndContactListener();
        //注册消息事件监听
        registerEventListener();

    }

    /**
     * 注册群组和联系人监听，由于logout的时候会被sdk清除掉，再次登录的时候需要再注册一下
     */
    public void registerGroupAndContactListener() {
        if (!isGroupAndContactListenerRegisted) {
            //注册群组变动监听
            EMClient.getInstance().groupManager().addGroupChangeListener(new MyGroupChangeListener());
            //注册联系人变动监听
            EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
            isGroupAndContactListenerRegisted = true;
        }

    }

    /**
     * 群组变动监听
     */
    class MyGroupChangeListener implements EMGroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            // 用户申请加入群聊
        }

        @Override
        public void onRequestToJoinReceived(String s, String s1, String s2, String s3) {
            // 用户申请加入群聊
        }

        @Override
        public void onRequestToJoinAccepted(String s, String s1, String s2) {

        }

        @Override
        public void onRequestToJoinDeclined(String s, String s1, String s2, String s3) {
            // 加群申请被拒绝，demo未实现
        }

        @Override
        public void onInvitationAccepted(String s, String s1, String s2) {

//            String st4 = mContext.getString(com.hyphenate.easeui.R.string.Agreed_to_your_group_chat_application);
//            // 加群申请被同意
//            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
//            msg.setChatType(EMMessage.ChatType.GroupChat);
//            msg.setFrom(accepter);
//            msg.setTo(groupId);
//            msg.setMsgId(UUID.randomUUID().toString());
//            msg.addBody(new EMTextMessageBody(accepter + " " + st4));
//            msg.setStatus(EMMessage.Status.SUCCESS);
//            // 保存同意消息
//            EMClient.getInstance().chatManager().saveMessage(msg);
//            // 提醒新消息
//            getNotifier().vibrateAndPlayTone(msg);
        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {
            // 对方同意加群邀请
        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            //TODO 提示用户被T了，demo省略此步骤
        }

        @Override
        public void onGroupDestroyed(String s, String s1) {
            // 群被解散
            //TODO 提示用户群被解散,demo省略
        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            // 被邀请
//            String st3 = mContext.getString(com.hyphenate.easeui.R.string.Invite_you_to_join_a_group_chat);
//            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
//            msg.setChatType(EMMessage.ChatType.GroupChat);
//            msg.setFrom(inviter);
//            msg.setTo(groupId);
//            msg.setMsgId(UUID.randomUUID().toString());
//            msg.addBody(new EMTextMessageBody(inviter + " " + st3));
//            msg.setStatus(EMMessage.Status.SUCCESS);
//            // 保存邀请消息
//            EMClient.getInstance().chatManager().saveMessage(msg);
//            // 提醒新消息
//            getNotifier().vibrateAndPlayTone(msg);
//            EMLog.d(TAG, "onAutoAcceptInvitationFromGroup groupId:" + groupId);
        }
    }

    /***
     * 好友变化listener
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(String username) {
            // 保存增加的联系人
        }

        @Override
        public void onContactDeleted(String username) {
            // 被删除
        }

        @Override
        public void onContactInvited(String username, String reason) {
            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
        }

        @Override
        public void onFriendRequestAccepted(String s) {

        }

        @Override
        public void onFriendRequestDeclined(String s) {

        }
    }

    /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void registerEventListener() {
        messageListener = new EMMessageListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
                    //应用在后台，不需要刷新UI,通知栏提示新消息
                    if (!easeUI.hasForegroundActivies()) {
                        getNotifier().onNewMsg(message);
                    }
                }
                ZJNSHXPlugin.renewConversationList();
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "收到透传消息");
                    //获取消息body
                    EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action();//获取自定义action

                    //获取扩展属性 此处省略
                    //message.getStringAttribute("");
                    EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action, message.toString()));
                    final String str = mContext.getString(com.hyphenate.easeui.R.string.receive_the_passthrough);

                    final String CMD_TOAST_BROADCAST = "hyphenate.demo.cmd.toast";
                    IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);

                    if (broadCastReceiver == null) {
                        broadCastReceiver = new BroadcastReceiver() {

                            @Override
                            public void onReceive(Context context, Intent intent) {
                                // TODO Auto-generated method stub
                                ToastUtils.showShort(mContext, intent.getStringExtra("cmd_value"));
                            }
                        };

                        //注册广播接收者
                        mContext.registerReceiver(broadCastReceiver, cmdFilter);
                    }

                    Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
                    broadcastIntent.putExtra("cmd_value", str + action);
                    mContext.sendBroadcast(broadcastIntent, null);
                }
            }

            @Override
            public void onMessageRead(List<EMMessage> list) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {

            }
        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    /**
     * 是否登录成功过
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    /**
     * 退出登录
     *
     * @param unbindDeviceToken 是否解绑设备token(使用GCM才有)
     * @param callback          callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        HXPreferenceManager.getInstance().setUserInfo("");
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "logout: onSuccess");
                HXPreferenceManager.getInstance().setUserInfo("");
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.d(TAG, "logout: onSuccess");
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }

    /**
     * 获取消息通知类
     *
     * @return
     */
    public EaseNotifier getNotifier() {
        return easeUI.getNotifier();
    }

    public void loginHX(final HXUserModel userModel, final EMCallBack callBack) {
        EMClient.getInstance().login(userModel.userHXId, userModel.password, new EMCallBack() {
            @Override
            public void onSuccess() {
                HXPreferenceManager.getInstance().setUserInfo(GsonUtils.toJson(userModel));
                callBack.onSuccess();
            }

            @Override
            public void onError(int i, String s) {
                callBack.onError(i, s);
            }

            @Override
            public void onProgress(int i, String s) {
                callBack.onProgress(i, s);
            }
        });
    }

    public void pushActivity(Activity activity) {
        easeUI.pushActivity(activity);
    }

    public void popActivity(Activity activity) {
        easeUI.popActivity(activity);
    }

    public int getUnreadMsgsCount() {
        return EMClient.getInstance().chatManager().getUnreadMsgsCount();
    }

    public HXUserModel getUserInfo() {
        HXUserModel userModel;
        userModel = GsonUtils.fromJson(HXPreferenceManager.getInstance().getUserInfo(), HXUserModel.class);
        return userModel;
    }

    public String getUserId() {
        return null != getUserInfo() ? getUserInfo().userAppId : "";
    }

    public String getUserHXId() {
        return null != getUserInfo() ? getUserInfo().userHXId : "";
    }

    public String getUserNickName() {
        return null != getUserInfo() ? getUserInfo().nickName : "";
    }

    public String getUserAvatar() {
        return null != getUserInfo() ? getUserInfo().avatar : "";
    }

    public List<EMConversation> loadConversationList() {
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        // Internal is TimSort algorithm, has bug
        sortConversationByLastChatTime(sortList);
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    public void delConversation(String conversationId) {
        // 删除此会话
        EMClient.getInstance().chatManager().deleteConversation(conversationId, false);
    }

    public void startChatActivity(Context context, String sendVal) {
        String currentUserId = HXManager.getInstance().getUserHXId();
        ApplicationInfo appInfo = null;
        String hxMetaData = "";
        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (null != appInfo) {
            hxMetaData = appInfo.metaData.getString("EASEMOB_APPKEY");
        }

        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EaseConstant.EXTRA_EXT_MODEL, sendVal);
        intent.putExtra(EaseConstant.EXTRA_HXMETADATA, hxMetaData);
        intent.putExtra(EaseConstant.EXTRA_CURRENTUSERID, currentUserId);
        context.startActivity(intent);
    }
}
