package com.bjzjns.hxplugin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.bjzjns.hxplugin.fragment.ChatFragment;
import com.bjzjns.hxplugin.permissions.PermissionsManager;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.model.MessageData;
import com.hyphenate.easeui.model.MessageExtModel;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.ui.EaseChatFragment;

/**
 * 聊天页面
 */
public class ChatActivity extends EaseBaseActivity {
    public static final String TAG = ChatActivity.class.getSimpleName();
    public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    private String toChatUserId;
    private MessageExtModel<MessageData> extModel;

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
            super.onNewIntent(intent);
        } else {
            finish();
            startActivity(intent);
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
