package com.bjzjns.hxplugin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.bjzjns.hxplugin.fragment.ChatFragment;
import com.bjzjns.hxplugin.manager.HXManager;
import com.bjzjns.hxplugin.permissions.PermissionsManager;
import com.bjzjns.hxplugin.tools.GsonUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.model.MessageExtModel;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.ui.EaseChatFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天页面
 */
public class ChatActivity extends EaseBaseActivity {
    public static final String TAG = ChatActivity.class.getSimpleName();
    public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    private String toChatUserId;
    private MessageExtModel extModel;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(getResources().getIdentifier("im_activity_chat", "layout", getPackageName()));
        activityInstance = this;
        toChatUserId = "-1";

        //聊天人或群id
        extModel = getIntent().getExtras().getParcelable(EaseConstant.EXTRA_EXT_MODEL);
        if (null != extModel && null != extModel.touser) {
            toChatUserId = extModel.touser.easemobile_id;
        }

        updateView();

        //可以直接new EaseChatFratFragment使用
        chatFragment = new ChatFragment();
        //传入参数
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(getResources().getIdentifier("container", "id", getPackageName()), chatFragment).commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String userId = "";
        extModel = getIntent().getExtras().getParcelable(EaseConstant.EXTRA_EXT_MODEL);
        if (null != extModel && null != extModel.touser) {
            userId = extModel.touser.easemobile_id;
        }
        if (toChatUserId.equals(userId)) {
            updateView();
            super.onNewIntent(intent);
        } else {
            finish();
            startActivity(intent);
        }
    }

    private void updateView() {
        updateProductView();
        updateCustomerService();
    }

    private void updateCustomerService() {
        SharedPreferences sp = getSharedPreferences(EaseConstant.APP_SP_NAME, Context.MODE_PRIVATE);
        long lastTime = sp.getLong(EaseConstant.SEND_CUSTOMER_SERVICE_MESSAGE_TIME + HXManager.getInstance().getUserHXId(), 0);
        if (System.currentTimeMillis() - lastTime > EaseConstant.DAY_MILLISECOND) {
            sendCSWelcomeMessage();
            sp.edit().putLong(EaseConstant.SEND_CUSTOMER_SERVICE_MESSAGE_TIME + HXManager.getInstance().getUserHXId(), System.currentTimeMillis()).commit();
        }
    }

    private void sendCSWelcomeMessage() {
        if (null != extModel && MessageExtModel.MESSAGE_SCENE_CUSTOMER_SERVICE == extModel.message_scene) {
            MessageExtModel model = new MessageExtModel();
            model.is_extend_message_content = extModel.is_extend_message_content;
            model.data = extModel.data;
            model.message_scene = extModel.message_scene;
            model.message_type = extModel.message_type;
            model.user = extModel.touser;
            model.touser = extModel.user;

            if (null != model.touser) {
                EMMessage message = EMMessage.createTxtSendMessage(getResources().getString(getResources().getIdentifier("str_customer_service_welcome_message", "string", getPackageName())), model.touser.easemobile_id);
                String extContent = GsonUtils.toJson(model);
                message.setAttribute(EaseConstant.MESSAGE_ATTR_EXT, extContent);
                EMClient.getInstance().chatManager().sendMessage(message);
            }
        }
    }

    private void updateProductView() {
        if (null != extModel && extModel.is_extend_message_content
                && null != extModel.touser && null != extModel.data
                && MessageExtModel.EXT_TYPE_SINGLE_PRODUCT.equals(extModel.message_type)) {
            EMMessage message = EMMessage.createTxtSendMessage(extModel.data.name, extModel.touser.easemobile_id);
            String extContent = GsonUtils.toJson(extModel);
            message.setAttribute(EaseConstant.MESSAGE_ATTR_EXT, extContent);
            message.setStatus(EMMessage.Status.SUCCESS);
            List<EMMessage> msgs = new ArrayList<EMMessage>();
            msgs.add(message);
            EMClient.getInstance().chatManager().importMessages(msgs);
            EMClient.getInstance().chatManager().saveMessage(message);
            chatFragment.refreshMessage();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // cancel the notification
        EaseUI.getInstance().getNotifier().reset();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
