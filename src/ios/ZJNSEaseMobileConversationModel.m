//
//  ZJNSEaseMobileConversationModel.m
//  SSJ App
//
//  Created by xuyang on 2017/2/16.
//
//

#import "ZJNSEaseMobileConversationModel.h"

@implementation ZJNSEaseMobileConversationModel
- (instancetype)initWithConversation:(EMConversation *)conversation{
    if (self = [super init]) {
        _ext = conversation.latestMessage.ext[@"ext"];
        _conversationId = conversation.conversationId;
        _timestamp = [NSString stringWithFormat:@"%lld", conversation.latestMessage.timestamp];
        _unreadMessageCount = [NSString stringWithFormat:@"%d", conversation.unreadMessagesCount];
        _messageBodyContent = @"";
        if (conversation.latestMessage.body.type == EMMessageBodyTypeText) {
            _messageBodyContent = ((EMTextMessageBody *)conversation.latestMessage.body).text;
        }
        _messageBodyType = [NSString stringWithFormat:@"%d",conversation.latestMessage.body.type];
    }
    return self;
}
@end
