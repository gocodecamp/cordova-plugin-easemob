//
//  ZJNSEaseMobileManager.m
//  SSJ App
//
//  Created by xuyang on 2017/2/16.
//
//

#import "ZJNSEaseMobileManager.h"
#import "EMClient.h"
#import "ZJNSEaseMobileConversationModel.h"
@implementation ZJNSEaseMobileManager
+ (void)load{
    [ZJNSEaseMobileManager sharedInstance];
}

static ZJNSEaseMobileManager *_sharedInstance;

+ (ZJNSEaseMobileManager *)sharedInstance {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _sharedInstance = [ZJNSEaseMobileManager new];
        [[NSNotificationCenter defaultCenter] addObserver:_sharedInstance selector:@selector(goToDesignerDetial:) name:kGoToDesignerDetialNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:_sharedInstance selector:@selector(goToUserDetail:) name:kGoToUserDetailNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:_sharedInstance selector:@selector(goToProductDetail:) name:kGoToProductDetailNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:_sharedInstance selector:@selector(newmessageNotice:) name:kSetupUnreadMessageCount object:nil];
    });
    
    return _sharedInstance;
}


#pragma mark - JS -> Native
-(void)login:(CDVInvokedUrlCommand *)command{
    [ZJNSEaseMobileManager sharedInstance].commandDelegate = self.commandDelegate;
    if (command.arguments.count>1) {
        //customize argument
        
        // 1.将网址初始化成一个OC字符串对象
        NSString *urlStr = @"http://www.cocoachina.com/cms/uploads/allimg/140919/4673_140919134550_1.jpg";
        // 如果网址中存在中文,进行URLEncode
        NSString *newUrlStr = [urlStr stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        // 2.构建网络URL对象, NSURL
        NSURL *url = [NSURL URLWithString:newUrlStr];
        // 3.创建网络请求
        NSURLRequest *request = [NSURLRequest requestWithURL:url cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:10];
        // 创建同步链接
        NSURLResponse *response = nil;
        NSError *error = nil;
        NSData *data = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
        
        NSString* username = command.arguments[0];
        NSString *password = command.arguments[1];
        
        __weak __typeof(self)weakself = self;
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            EMError *error = [[EMClient sharedClient] loginWithUsername:username password:password];
            dispatch_async(dispatch_get_main_queue(), ^{
                
                if (!error) {
                    //设置是否自动登录
                    [[EMClient sharedClient].options setIsAutoLogin:YES];
                    
                    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                        [[EMClient sharedClient] migrateDatabaseToLatestSDK];
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [[ChatUIHelper shareHelper] asyncConversationFromDB];
                            //发送自动登陆状态通知
                            [[NSNotificationCenter defaultCenter] postNotificationName:KNOTIFICATION_LOGINCHANGE object:@([[EMClient sharedClient] isLoggedIn])];
                            
                            //保存最近一次登录用户名
                            [weakself saveLastLoginUsername];
                            
                            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[self resultStringWithMessage:@"success" success:YES]];
                            [weakself.commandDelegate runInBackground:^{
                                [weakself.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                            }];
                            
                        });
                    });
                } else {
                    NSString *errorStr = nil;
                    switch (error.code)
                    {
                            
                        case EMErrorNetworkUnavailable:
                            
                            errorStr = NSLocalizedString(@"error.connectNetworkFail", @"No network connection!");
                            
                            break;
                        case EMErrorServerNotReachable:
                            errorStr = NSLocalizedString(@"error.connectServerFail", @"Connect to the server failed!");
                            break;
                        case EMErrorUserAuthenticationFailed:
                            errorStr = error.errorDescription;
                            break;
                        case EMErrorServerTimeout:
                            errorStr = NSLocalizedString(@"error.connectServerTimeout", @"Connect to the server timed out!");
                            break;
                        case EMErrorServerServingForbidden:
                            errorStr = NSLocalizedString(@"servingIsBanned", @"Serving is banned");
                            break;
                        default:
                            errorStr = NSLocalizedString(@"login.fail", @"Login failure");
                            break;
                            
                    }
                    NSString *result = [self resultStringWithMessage:errorStr success:NO];
                    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:result];
                    [weakself.commandDelegate runInBackground:^{
                        [weakself.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                    }];
                }
            });
        });
        
    }else{
        //callback
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[self resultStringWithMessage:@"参数不足" success:NO]];
        [self.commandDelegate runInBackground:^{
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    }
}

- (void)logout:(CDVInvokedUrlCommand *)command{
    [ZJNSEaseMobileManager sharedInstance].commandDelegate = self.commandDelegate;
    __weak __typeof(self)weakSelf = self;
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        EMError *error = [[EMClient sharedClient] logout:YES];
        dispatch_async(dispatch_get_main_queue(), ^{
            
            if (error != nil) {
                NSString *result = [self resultStringWithMessage:error.errorDescription success:NO];
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:result];
                [weakSelf.commandDelegate runInBackground:^{
                    [weakSelf.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                }];
            }
            else{
                
                [[NSNotificationCenter defaultCenter] postNotificationName:KNOTIFICATION_LOGINCHANGE object:@NO];
                NSString *result = [self resultStringWithMessage:@"success" success:YES];
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:result];
                [weakSelf.commandDelegate runInBackground:^{
                    [weakSelf.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                }];
            }
        });
    });
}

-(void)getAllConversations:(CDVInvokedUrlCommand *)command{
    [ZJNSEaseMobileManager sharedInstance].commandDelegate = self.commandDelegate;
    __weak __typeof(self)weakSelf = self;
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        __strong __typeof(weakSelf)strongSlef = weakSelf;
        [[ChatUIHelper shareHelper] asyncConversationFromDB];
        NSArray *conversations = [[EMClient sharedClient].chatManager getAllConversations];
        NSMutableArray *resultConversation = [NSMutableArray arrayWithCapacity:conversations.count];
        for (int i = 0; i < conversations.count; i++) {
            ZJNSEaseMobileConversationModel *model = [[ZJNSEaseMobileConversationModel alloc] initWithConversation:conversations[i]];
            [resultConversation addObject:model];
        }
        
        NSString *resultJson = [[ZJNSEaseMobileConversationModel mj_keyValuesArrayWithObjectArray:resultConversation] mj_JSONString];
        
        NSString *tempStr = [NSString stringWithFormat:@"\"conversationList\":%@",resultJson];
        
        NSString *result = [self resultStringWithMessage:tempStr success:YES];
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:result];
        [strongSlef.commandDelegate runInBackground:^{
            [strongSlef.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    });
}

-(void)gotoChat:(CDVInvokedUrlCommand *)command{
    [ZJNSEaseMobileManager sharedInstance].commandDelegate = self.commandDelegate;
    if (command.arguments.count >0) {
        NSString *ext       = command.arguments[0];
        NSDictionary *dic   = [ChatUIHelper jsonStringToDictionary:ext];
        dic = dic[@"ext"];
        [ZJUserModel sharedInstance].userId         = dic[@"user"][@"easemobile_id"];
        [ZJUserModel sharedInstance].easemobile_id  = dic[@"user"][@"easemobile_id"];
        [ZJUserModel sharedInstance].userName   = dic[@"user"][@"username"];
        [ZJUserModel sharedInstance].realavatar = dic[@"user"][@"head_thumb"];
        [ZJUserModel sharedInstance].nickName   = dic[@"user"][@"nickname"];
        
        
        ChatViewController *chatVC = [[ChatViewController alloc] initWithChatter:dic[@"user"][@"easemobile_id"] ext:dic];
        
        UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:chatVC];
        [[[[UIApplication sharedApplication] keyWindow] rootViewController] presentViewController:nav animated:YES completion:^{
            
        }];
        NSString *result = [self resultStringWithMessage:@"success" success:YES];
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:result];
        __weak __typeof(self)weakSelf = self;
        [self.commandDelegate runInBackground:^{
            [weakSelf.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    }else{
        //callback
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[self resultStringWithMessage:@"参数不足" success:NO]];
        [self.commandDelegate runInBackground:^{
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    }
}

-(void)delConversationItem:(CDVInvokedUrlCommand *)command{
    [ZJNSEaseMobileManager sharedInstance].commandDelegate = self.commandDelegate;
    if (command.arguments.count > 0) {
        NSString *conversationId = command.arguments[0];
        __weak __typeof(self)weakSelf = self;
        [[EMClient sharedClient].chatManager deleteConversation:conversationId isDeleteMessages:YES completion:^(NSString *aConversationId, EMError *aError) {
            __strong __typeof(weakSelf)strongSelf = weakSelf;
            NSString *result;
            if (aError != nil) {
                result = [strongSelf resultStringWithMessage:aError.description success:NO];
            }else{
                result = [strongSelf resultStringWithMessage:@"success" success:YES];
            }
            
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:result];
            [strongSelf.commandDelegate runInBackground:^{
                [strongSelf.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }];
            
        }];
    }else{
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[self resultStringWithMessage:@"参数不足" success:NO]];
        __weak __typeof(self)weakSelf = self;
        [self.commandDelegate runInBackground:^{
            [weakSelf.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    }
    
}

#pragma mark - Native->JS
- (void)goToDesignerDetial:(NSNotification *)notification{
    NSDictionary *ext = [notification userInfo];
    NSString *jsStr = [NSString stringWithFormat:@"window.goToDesignerDetial(\"%@\")",ext[@"touser"][@"username"]];
    [self.commandDelegate evalJs:jsStr];
}

- (void)goToUserDetail:(NSNotification *)notification{
    NSDictionary *ext = [notification userInfo];
    NSString *jsStr = [NSString stringWithFormat:@"window.goToUserDetail(\"%@\")",ext[@"user"][@"easemobile_id"]];
    [self.commandDelegate evalJs:jsStr];
}

- (void)goToProductDetail:(NSNotification *)notification{
    NSDictionary *dic = [notification userInfo];
    NSString *jsStr = [NSString stringWithFormat:@"window.goToProductDetail(\"%@\")",dic[@"url"]];
    [self.commandDelegate evalJs:jsStr];
}

- (void)newmessageNotice:(NSNotification *)notification{
    NSString *jsStr = @"window.renewConversationList()";
    [self.commandDelegate evalJs:jsStr];
    
}
#pragma  mark - private



-(NSString *)resultStringWithMessage:(NSString *)msg success:(BOOL)success{
    return [NSString stringWithFormat:@"{%@}",msg];
}

- (void)saveLastLoginUsername
{
    NSString *username = [[EMClient sharedClient] currentUsername];
    if (username && username.length > 0) {
        NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
        [ud setObject:username forKey:[NSString stringWithFormat:@"em_lastLogin_username"]];
        [ud synchronize];
    }
}

- (NSString*)lastLoginUsername
{
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    NSString *username = [ud objectForKey:[NSString stringWithFormat:@"em_lastLogin_username"]];
    if (username && username.length > 0) {
        return username;
    }
    return nil;
}
@end
