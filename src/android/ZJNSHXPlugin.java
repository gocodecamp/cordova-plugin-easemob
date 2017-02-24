package com.bjzjns.hxplugin;

import android.content.Context;
import android.text.TextUtils;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import com.bjzjns.hxplugin.manager.HXManager;
import com.bjzjns.hxplugin.model.ConversationItemModel;
import com.bjzjns.hxplugin.model.ConversationListModel;
import com.bjzjns.hxplugin.tools.GsonUtils;
import com.bjzjns.hxplugin.tools.LogUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.model.MessageExtModel;

import java.util.ArrayList;
import java.util.List;

/**
 * This class echoes a string called from JavaScript.
 */
public class ZJNSHXPlugin extends CordovaPlugin {
    // 初始化环信
    private static final String INIT_HX = "initEaseMobile";
    // 登录环信
    private static final String LOGIN_HX = "login";
    // 退出环信
    private static final String LOGOUT_HX = "logout";
    // 获取所有会话
    private static final String LOAD_ALL_CONVERSATION = "getAllConversations";
    // 删除会话
    private static final String DEL_CONVERSATION_ITEM = "delConversationItem";
    // 进入聊天
    private static final String GOTO_CHAT = "gotoChat";
    private static CordovaWebView mWebView;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        mWebView = webView;
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        LogUtils.d("ZJNSHXPlugin", "action =" + action + ",args =" + args.toString());
        if (action.equals(INIT_HX)) {
            try {
                initHX();
                callbackContext.success("initEaseMobile success");
            } catch (Exception e) {
                callbackContext.error("initEaseMobile error:" + e.toString());
            }

            return true;
        } else if (action.equals(LOGIN_HX)) {
            loginHX(args.getString(0), args.getString(1), callbackContext);
            return true;
        } else if (action.equals(LOGOUT_HX)) {
            logout(true, callbackContext);
            return true;
        } else if (action.equals(LOAD_ALL_CONVERSATION)) {
            loadAllConversation(callbackContext);
            return true;
        } else if (action.equals(DEL_CONVERSATION_ITEM)) {
            delConversationItem(args.getString(0), callbackContext);
            return true;
        } else if (action.equals(GOTO_CHAT)) {
            gotoChat(args.getString(0));
            return true;
        }
        return false;
    }

    private Context getContext() {
        return this.cordova.getActivity();
    }

    /**
     * 初始化环信
     */
    private void initHX() {
        LogUtils.d("ZJNSHXPlugin", "initHX");
        this.cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                HXManager.getInstance().init(cordova.getActivity().getApplicationContext());
            }
        });
    }

    /**
     * 进入聊天
     *
     * @param sendVal
     */
    private void gotoChat(final String sendVal) {
        LogUtils.d("ZJNSHXPlugin", "gotoChat");
        this.cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                HXManager.getInstance().startChatActivity(getContext(), sendVal);
            }
        });
    }

    /**
     * 登录环信
     *
     * @param userName
     * @param password
     */
    private void loginHX(String userName, String password, final CallbackContext callbackContext) {
        LogUtils.d("ZJNSHXPlugin", "loginHX");
        HXManager.getInstance().loginHX(userName, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                LogUtils.d("ZJNSHXPlugin", "login success");
                callbackContext.success("login success");
                cordova.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                LogUtils.d("ZJNSHXPlugin", "login error:" + i + ":" + s);
                callbackContext.error("login error:" + i + ":" + s);
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

    /**
     * 退出环信
     *
     * @param unbindDeviceToken
     * @param callbackContext
     */
    private void logout(boolean unbindDeviceToken, final CallbackContext callbackContext) {
        LogUtils.d("ZJNSHXPlugin", "logout");
        HXManager.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                LogUtils.d("ZJNSHXPlugin", "logout success");
                callbackContext.success("logout success");
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(int code, String error) {
                LogUtils.d("ZJNSHXPlugin", "logout error:" + code + ":" + error);
                callbackContext.error("logout error:" + code + ":" + error);
            }
        });
    }

    /**
     * 获取所有会话
     *
     * @param callbackContext
     */
    private void loadAllConversation(final CallbackContext callbackContext) {
        LogUtils.d("ZJNSHXPlugin", "loadAllConversation");
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ConversationListModel conversationListModel = new ConversationListModel();
                    List<ConversationItemModel> conversationItemList = new ArrayList<ConversationItemModel>();
                    ConversationItemModel conversationItemModel;
                    EMMessage message;
                    for (EMConversation emConversation : HXManager.getInstance().loadConversationList()) {
                        conversationItemModel = new ConversationItemModel();
                        message = emConversation.getLastMessage();
                        conversationItemModel.conversationId = emConversation.conversationId();
                        conversationItemModel.unreadMessagesCount = emConversation.getUnreadMsgCount() + "";
                        conversationItemModel.timestamp = message.getMsgTime() + "";
                        String content = "";
                        if (EMMessage.Type.TXT == message.getType()) {
                            content = ((EMTextMessageBody) message.getBody()).getMessage();
                        }
                        conversationItemModel.messageBodyContent = content;
                        conversationItemModel.messageBodyType = message.getType().ordinal() + 1 + "";
                        String extContent = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXT, "");
                        MessageExtModel extModel = GsonUtils.fromJson(extContent, MessageExtModel.class);
                        conversationItemModel.ext = extModel;
                        conversationItemList.add(conversationItemModel);
                    }
                    conversationListModel.conversationList = conversationItemList;
                    LogUtils.d("ZJNSHXPlugin", "AllConversation gson data:" + GsonUtils.toJson(conversationListModel));
                    callbackContext.success(GsonUtils.toJson(conversationListModel));
                } catch (Exception e) {
                    LogUtils.d("ZJNSHXPlugin", "loadAllConversation exception:" + e.toString());
                    callbackContext.error("loadAllConversation exception:" + e.toString());
                }
            }
        });
    }

    /**
     * 删除会话
     *
     * @param sendVal
     * @param callbackContext
     */
    private void delConversationItem(final String sendVal, final CallbackContext callbackContext) {
        LogUtils.d("ZJNSHXPlugin", "delConversationItem");
        if (!TextUtils.isEmpty(sendVal)) {
            try {
                this.cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 删除此会话
                        HXManager.getInstance().delConversation(sendVal);
                        LogUtils.d("ZJNSHXPlugin", "delConversationItem success");
                        callbackContext.success("delConversationItem success");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.d("ZJNSHXPlugin", "delConversationItem exception:" + e.toString());
                callbackContext.error("delConversationItem exception:" + e.toString());
            }
        } else {
            LogUtils.d("ZJNSHXPlugin", "delConversationItem you not send data");
            callbackContext.error("you not send data");
        }
    }

    /**
     * 通知会话列表变化
     */
    public static void renewConversationList() {
        if (null != mWebView) {
            LogUtils.d("ZJNSHXPlugin", "renewConversationList");
            mWebView.loadUrl("javascript:renewConversationList()");
        }
    }

    /**
     * 进入设计师详情
     *
     * @param sendVal
     */
    public static void gotoDesignerDeatil(String sendVal) {
        if (null != mWebView) {
            LogUtils.d("ZJNSHXPlugin", "gotoDesignerDeatil sendVal=" + sendVal);
            mWebView.loadUrl("javascript:goToDesignerDetial(" + sendVal + ")");
        }
    }

    /**
     * 进入用户详情
     *
     * @param sendVal
     */
    public static void gotoUserDetail(String sendVal) {
        if (null != mWebView) {
            LogUtils.d("ZJNSHXPlugin", "gotoUserDetail sendVal=" + sendVal);
            mWebView.loadUrl("javascript:goToUserDetail(" + sendVal + ")");
        }
    }

    /**
     * 进入商品详情
     *
     * @param sendVal
     */
    public static void gotoProductDetail(String sendVal) {
        if (null != mWebView) {
            LogUtils.d("ZJNSHXPlugin", "gotoProductDetail sendVal=" + sendVal);
            mWebView.loadUrl("javascript:goToProductDetail(" + sendVal + ")");
        }
    }
}
