package com.bjzjns.hxplugin.view.chatrow;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjzjns.hxplugin.ZJNSHXPlugin;
import com.bjzjns.hxplugin.tools.GsonUtils;
import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.model.MessageData;
import com.hyphenate.easeui.model.MessageExtModel;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.exceptions.HyphenateException;

public class EaseChatRowProduct extends EaseChatRow {

    private TextView titleTv;
    private TextView priceTv;
    private TextView hyperLinkTv;
    private ImageView pictureIv;
    private LinearLayout contentLl;
    private MessageData data;
    private Context context;

    public EaseChatRowProduct(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
        this.context = context;
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(getResources().getIdentifier("ease_row_sent_product", "layout", context.getPackageName()), this);
    }

    @Override
    protected void onFindViewById() {
        contentLl = (LinearLayout) findViewById(getResources().getIdentifier("content_ll", "id", context.getPackageName()));
        pictureIv = (ImageView) findViewById(getResources().getIdentifier("image", "id", context.getPackageName()));
        titleTv = (TextView) findViewById(getResources().getIdentifier("title_tv", "id", context.getPackageName()));
        priceTv = (TextView) findViewById(getResources().getIdentifier("price_tv", "id", context.getPackageName()));
        hyperLinkTv = (TextView) findViewById(getResources().getIdentifier("hyperlink_tv", "id", context.getPackageName()));
    }

    @Override
    protected void onSetUpView() {
        String extContext = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXT, "");
        if (!TextUtils.isEmpty(extContext)) {
            final MessageExtModel model = GsonUtils.fromJson(extContext, MessageExtModel.class);
            if (null != model && null != model.data) {
                data = model.data;
                titleTv.setText(data.name);
                priceTv.setText(context.getResources().getString(getResources().getIdentifier("str_product_price", "string", context.getPackageName()), data.price));
                if (!TextUtils.isEmpty(data.imgSrc)) {
                    Glide.with(context).load(data.imgSrc).into(pictureIv);
                }
                contentLl.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ZJNSHXPlugin.gotoProductDetail(model.data.product_id);
                    }
                });
                hyperLinkTv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendProductMessage(model);
                    }
                });
            }
        }
        handleTextMessage();
    }

    private void sendProductMessage(MessageExtModel model) {
        model.is_extend_message_content = false;
        MessageData messageData = model.data;
        if (null != model && null != model.touser && null != messageData) {
            EMMessage message = EMMessage.createTxtSendMessage(messageData.url, model.touser.easemobile_id);
            String extContent = GsonUtils.toJson(model);
            message.setAttribute(EaseConstant.MESSAGE_ATTR_EXT, extContent);
            EMClient.getInstance().chatManager().sendMessage(message);
        }
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
        } else {
            if (!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat) {
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

    }
}
