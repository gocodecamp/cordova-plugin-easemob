## cordova-plugin-easemob ##

**cordova-plugin-easemob** is a plugin for integration easemob.

### Installation ###

```
cordova plugin add https://github.com/gocodecamp/cordova-plugin-easemob --variable EASEMOB_KEY=your easemobile key
```

### Plugin API ###

**initEaseMobile**

初始化环信

`initEaseMobile(successCallback, failCallback)`

**login**

登录环信

`login(userName, password, successCallback, failCallback)`

**logout**

退出环信登录

`logout(successCallback, failCallback)`

**getAllConversations**

获取所有会话列表

`getAllConversations(successCallback, failCallback)`


Success function returns an Object, Object like:

	{"conversationList":"[
		 {
        	"conversationId":"323422"，
        	"timestamp":"32324566554545"，
	    	"unreadMessagesCount":"2"，
        	"messageBodyContent":"hello！"，
        	"messageBodyType":"2"，（1：TXT 2：IMAGE 3：VIDEO 4：LOCATION 5：VOICE 6：FILE 7：CMD）
        	"ext":"{
                   "message_scene": 0，
                   "is_extend_message_content": true，
                   "message_type": "single_product"，
                   "brand_name": "shop name"，
                   "ip": "ip地址"，
                   "user": "{
								"easemobile_id": "23242423"，
                   				"username": "2324353423"，
                   				"nickname": "sender nickname"，
                   				"head_thumb": "http：//323.jpg"，
							}"，
                   "touser": "{
								"easemobile_id": "3424332432"，
                   				"username": "32343242342"，
                   				"nickname": "receiver nickname"，
                   				"head_thumb": "http：//323.jpg"，
							}"，
                   "data": "{
								"product_id": "122323"，
                   				"name": "product name"，
                   				"price": "12.32"，
                   				"imgSrc": "http：//323.jpg"，
								"url": "http://2332"，
                   				"display_text": "title"，
                   				"notSupportDisplayText": "can’t Parsing this message"，
							}"
               }"
		}
	]"
	}

Failure function returns an error String.

**delConversationItem**

删除某项会话

`delConversationItem(conversationId，successCallback, failCallback)`

**gotoChat**

进入聊天界面

`gotoChat(options，successCallback, failCallback)`

params options is Object like：

	{
    	"message_scene": 100，
        "is_extend_message_content": true，
        "message_type": "single_product"，
        "brand_name": "shop name"，
        "ip": "ip地址"，
        "user": "{
        	"easemobile_id": "23242423"，
        	"username": "2324353423"，
        	"nickname": "sender nickname"，
        	"head_thumb": "http：//323.jpg"，
        	}"，
        "touser": "{
        	"easemobile_id": "3424332432"，
        	"username": "32343242342"，
        	"nickname": "receiver nickname"，
        	"head_thumb": "http：//323.jpg"，
        	}"，
        "data": "{
        	"product_id": "122323"，
        	"name": "product name"，
        	"price": "12.32"，
        	"imgSrc": "http：//323.jpg"，
        	"url": "http://2332"，
        	"display_text": "title"，
        	"notSupportDisplayText": "can’t Parsing this message"，
        	}"
	}

##Native call JS method##

Entry product detail page（进入商品详情页）

`goToProductDetail(String productId)`

Entry user detail page（进入用户详情页）

`gotoUserDetail(String userId)`

Entry designer detail page（进入设计师详情页）

`gotoDesignerDeatil(String designerId)`

Refresh conversation list page（刷新会话列表页）

`renewConversationList()`

##Description##

	message_scene （必填）
	0 单聊 1 群聊 2 聊天室 100 设计师  101 客服

	is_extend_message_content（必填）
	是否为扩展消息

	message_type
    扩展消息类型：single_product 商品扩展消息 single_product_link 商品链接扩展消息，is_extend_message_content为true时必填

	brand_name（根据message_scene可选）
	店铺名称 message_scene为100时，必填

	ip
	用户ip地址，内部客服陌生人专用
	
	user
	消息发送者信息

	touser
	消息接收者信息

	data
	商品相关信息，is_extend_message_content为true时必填

	easemobile_id
	环信聊天用户id

	username
	用户app内部id

	nickname
	用户昵称

	head_thumb
	用户头像地址