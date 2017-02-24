//
//  ZJNSEaseMobileConversationModel.h
//  SSJ App
//
//  Created by xuyang on 2017/2/16.
//
//

#import <Foundation/Foundation.h>

@interface ZJNSEaseMobileConversationModel : NSObject
@property (nonatomic, copy) NSString *ext;
@property (nonatomic, copy) NSString *conversationId;
@property (nonatomic, copy) NSString *timestamp;
@property (nonatomic, copy) NSString *unreadMessageCount;
@property (nonatomic, copy) NSString *messageBodyContent;
@property (nonatomic, copy) NSString *messageBodyType;

- (instancetype)initWithConversation:(EMConversation *)conversation;
@end
