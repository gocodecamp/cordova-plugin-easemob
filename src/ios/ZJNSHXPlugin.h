//
//  ZJNSEaseMobileManager.h
//  SSJ App
//
//  Created by xuyang on 2017/2/16.
//
//

#import <Cordova/CDVPlugin.h>

@interface ZJNSHXPlugin : CDVPlugin
@property (nonatomic, strong)UIViewController *cordovaViewController;
+ (ZJNSHXPlugin *)sharedInstance;
-(void)login:(CDVInvokedUrlCommand *)command;
-(void)logout:(CDVInvokedUrlCommand *)command;
-(void)getAllConversations:(CDVInvokedUrlCommand *)command;
-(void)delConversationItem:(CDVInvokedUrlCommand *)command;
-(void)gotoChat:(CDVInvokedUrlCommand *)command;
@end
