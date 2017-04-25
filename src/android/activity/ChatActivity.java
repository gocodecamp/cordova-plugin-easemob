package com.bjzjns.hxplugin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjzjns.hxplugin.ZJNSHXPlugin;
import com.bjzjns.hxplugin.fragment.ChatFragment;
import com.bjzjns.hxplugin.manager.HXManager;
import com.bjzjns.hxplugin.tools.GsonUtils;
import com.bjzjns.hxplugin.tools.ToastUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.model.MessageExtModel;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.ui.EaseChatFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 聊天页面
 */
public class ChatActivity extends EaseBaseActivity {
    public static final String TAG = ChatActivity.class.getSimpleName();
    public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    private String toChatUserId;
    private MessageExtModel extModel;
    private TextView leftTv;
    private TextView title;
    private TextView rightTv;
    private ImageView leftBtn;
    private ImageView rightBtn;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(getResources().getIdentifier("im_activity_chat", "layout", getPackageName()));
        leftBtn = (ImageView) findViewById(getResources().getIdentifier("toolbar_leftbtn", "id", getPackageName()));
        leftTv = (TextView) findViewById(getResources().getIdentifier("toolbar_lefttxt", "id", getPackageName()));
        title = (TextView) findViewById(getResources().getIdentifier("title", "id", getPackageName()));
        rightTv = (TextView) findViewById(getResources().getIdentifier("toolbar_righttxt", "id", getPackageName()));
        rightBtn = (ImageView) findViewById(getResources().getIdentifier("toolbar_rightbtn", "id", getPackageName()));
        activityInstance = this;
        toChatUserId = "-1";
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //聊天人或群id
        extModel = GsonUtils.fromJson(getIntent().getExtras().getString(EaseConstant.EXTRA_EXT_MODEL, ""), MessageExtModel.class);
        if (null != extModel) {
            if (null != extModel.touser) {
                toChatUserId = extModel.touser.easemobile_id;
                if (!TextUtils.isEmpty(extModel.touser.nickname)) {
                    title.setText(extModel.touser.nickname);
                } else {
                    title.setText(extModel.touser.easemobile_id);
                }
            }
            if (EaseConstant.CHATTYPE_DESIGNER == extModel.message_scene) {
                if (!TextUtils.isEmpty(extModel.brand_name)) {
                    title.setText(extModel.brand_name);
                }
                rightTv.setText(getResources().getIdentifier("str_look_designer", "string", getPackageName()));
                rightTv.setVisibility(View.VISIBLE);
                rightTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != extModel.touser) {
                            ZJNSHXPlugin.gotoDesignerDetail(extModel.touser.username);
                            finish();
                        }
                    }
                });
            } else {
                rightTv.setVisibility(View.GONE);
            }
        }
        //可以直接new EaseChatFratFragment使用
        chatFragment = new ChatFragment();
        //传入参数
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(getResources().getIdentifier("container", "id", getPackageName()), chatFragment).commit();

        updateView();
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
        extModel = GsonUtils.fromJson(getIntent().getExtras().getString(EaseConstant.EXTRA_EXT_MODEL, ""), MessageExtModel.class);
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
        if (null != extModel && MessageExtModel.MESSAGE_SCENE_CUSTOMER_SERVICE == extModel.message_scene) {
            updateCustomerService();
        }
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
        MessageExtModel model = new MessageExtModel();
        model.is_extend_message_content = false;
        model.message_scene = extModel.message_scene;
        model.user = extModel.touser;
        model.touser = extModel.user;

        if (null != model.touser) {
            EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            message.setChatType(EMMessage.ChatType.Chat);
            message.setFrom(model.user.easemobile_id);
            message.setTo(model.touser.easemobile_id);
            message.setMsgId(UUID.randomUUID().toString());
            message.addBody(new EMTextMessageBody(getResources().getString(getResources().getIdentifier("str_customer_service_welcome_message", "string", getPackageName()))));
            message.setStatus(EMMessage.Status.SUCCESS);
            String extContent = GsonUtils.toJson(model);
            message.setAttribute(EaseConstant.MESSAGE_ATTR_EXT, extContent);
            // 保存同意消息
            EMClient.getInstance().chatManager().saveMessage(message);
            List<EMMessage> msgs = new ArrayList<EMMessage>();
            msgs.add(message);
            EMClient.getInstance().chatManager().importMessages(msgs);
        }
    }

    private void updateProductView() {
        if (null != extModel && MessageExtModel.MESSAGE_SCENE_DESIGNER == extModel.message_scene
                && extModel.is_extend_message_content
                && MessageExtModel.EXT_TYPE_SINGLE_PRODUCT.equals(extModel.message_type)
                && null != extModel.touser && null != extModel.data) {
            EMMessage message = EMMessage.createTxtSendMessage(extModel.data.name, extModel.touser.easemobile_id);
            String extContent = GsonUtils.toJson(extModel);
            message.setAttribute(EaseConstant.MESSAGE_ATTR_EXT, extContent);
            message.setStatus(EMMessage.Status.SUCCESS);
            List<EMMessage> msgs = new ArrayList<EMMessage>();
            msgs.add(message);
            EMClient.getInstance().chatManager().importMessages(msgs);
            EMClient.getInstance().chatManager().saveMessage(message);
            if (null != chatFragment) {
                chatFragment.refreshMessage();
            }
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
}
