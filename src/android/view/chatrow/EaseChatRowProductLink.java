package com.bjzjns.hxplugin.view.chatrow;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bjzjns.hxplugin.ZJNSHXPlugin;
import com.bjzjns.hxplugin.tools.GsonUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.model.MessageExtModel;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.exceptions.HyphenateException;

import java.util.HashMap;
import java.util.Map;

public class EaseChatRowProductLink extends EaseChatRow {

    private TextView contentView;

    public EaseChatRowProductLink(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            inflater.inflate(getResources().getIdentifier("ease_row_received_product_link", "layout", context.getPackageName()), this);
        } else {
            inflater.inflate(getResources().getIdentifier("ease_row_sent_product_link", "layout", context.getPackageName()), this);
        }
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(getResources().getIdentifier("tv_chatcontent", "id", context.getPackageName()));
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
        // 设置内容
        contentView.setText(span, TextView.BufferType.SPANNABLE);

        handleTextMessage();
    }

    protected void handleTextMessage() {
        if (message.direct() == EMMessage.Direct.SEND) {
            setMessageSendCallback();
            switch (message.status()) {
                case CREATE:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.GONE);
                    break;
                case FAIL:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                    progressBar.setVisibility(View.VISIBLE);
                    statusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }else{
            if(!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat){
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onUpdateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onBubbleClick() {
        String extContent = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXT, "");
        MessageExtModel model = GsonUtils.fromJson(extContent, MessageExtModel.class);
        if (null != model && MessageExtModel.MESSAGE_SCENE_DESIGNER == model.message_scene
                && null != model.data) {
            String messageContent = model.data.url;
            Map<String, String> contentMap = new HashMap<String, String>();
            if (!TextUtils.isEmpty(messageContent) && messageContent.contains("?")) {
                String content = messageContent.substring(messageContent.indexOf("?") + 1);
                if (!TextUtils.isEmpty(content)) {
                    String[] contentArray = content.split("&");
                    String subContent;
                    String subKey;
                    String subValue = "";
                    for (int i = 0; i < contentArray.length; i++) {
                        subContent = contentArray[i];
                        if (subContent.contains("=")) {
                            subKey = subContent.substring(0, subContent.indexOf("="));
                            subValue = subContent.substring(subContent.indexOf("=") + 1);
                            contentMap.put(subKey, subValue);
                        }
                    }
                    String productId = "";
                    if (contentMap.containsKey("id")) {
                        productId = contentMap.get("id");
                    }
                    ZJNSHXPlugin.gotoProductDetail(productId);
                    ((Activity) context).finish();
                }
            }
        }
    }
}
