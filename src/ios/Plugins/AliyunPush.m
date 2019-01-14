/********* AliyunPush.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import "AliyunNotificationLauncher.h"
#import <CloudPushSDK/CloudPushSDK.h>

@interface AliyunPush : CDVPlugin {

}

@property (nonatomic,strong) CDVInvokedUrlCommand * messageCommand;

@end

@implementation AliyunPush

- (void)pluginInitialize{
    
    [super pluginInitialize];
    
      NSLog(@"x-->pluginInitialize");
    
    // 推送通知 注册
    [[NSNotificationCenter defaultCenter] addObserver:self
                                            selector:@selector(onNotificationReceived:)
                                                 name:@"AliyunNotification"
                                               object:nil];
    
    // 推送消息 注册
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onMessageReceived:)
                                                 name:@"AliyunNotificationMessage"
                                               object:nil];
    
}


#pragma mark AliyunNotification通知
- (void)onNotificationReceived:(NSNotification *)notification {
    
    NSDictionary * info = notification.object;
    
    if(!info){
        return;
    }
    
    NSMutableDictionary *extra = [[NSMutableDictionary alloc] initWithDictionary:info];
    [extra removeObjectForKey:@"type"];
    [extra removeObjectForKey:@"body"];
    [extra removeObjectForKey:@"title"];
    
    NSMutableDictionary *message = [NSMutableDictionary dictionary];
    [message setObject:extra forKey:@"extra"];
    [message setObject:info[@"type"] forKey:@"type"];
    [message setObject:info[@"title"] forKey:@"title"];
    [message setObject:info[@"body"] forKey:@"content"];
    [message setObject:@"" forKey:@"url"];
    
    NSLog(@"x----数据来了");
    NSLog(@"%@",info[@"body"]);
    
    CDVPluginResult *result;
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:message];
    [result setKeepCallbackAsBool:true];
    [self.commandDelegate sendPluginResult:result callbackId:self.messageCommand.callbackId];
    
    
    NSString *requestData = [NSString stringWithFormat:@"sevenPushReceive(\"%@\")",info[@"body"]];
    
    [self.commandDelegate evalJs:requestData];
}

#pragma mark AliyunNotification消息

- (void)onMessageReceived:(NSNotification *)notification {
   
    NSDictionary * info = notification.object;
    if(!info){
        return;
    }
    NSMutableDictionary *message = [NSMutableDictionary dictionary];
    [message setObject:@"" forKey:@"extra"];
    [message setObject:info[@"type"] forKey:@"type"];
    [message setObject:info[@"title"] forKey:@"title"];
    [message setObject:info[@"body"] forKey:@"content"];
    [message setObject:@"" forKey:@"url"];
    
    CDVPluginResult *result;
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:message];
    [result setKeepCallbackAsBool:true];
    [self.commandDelegate sendPluginResult:result callbackId:self.messageCommand.callbackId];
    
}


-(NSString *)NSStringToJson:(NSString *)str
{
    NSMutableString *s = [NSMutableString stringWithString:str];
    
    [s replaceOccurrencesOfString:@"\\" withString:@"\\\\" options:NSCaseInsensitiveSearch range:NSMakeRange(0, [s length])];
    
    return [NSString stringWithString:s];
}

/**
 * 接收阿里云的消息
 */
- (void)onMessage:(CDVInvokedUrlCommand*)command{
    
    self.messageCommand = command;
}

/**
 *  主要检查这里  getDeviceId  是否为 75da75b0c3cc411ea4b1b0bca5df92ba
 * 获取设备唯一标识deviceId，deviceId为阿里云移动推送过程中对设备的唯一标识（并不是设备UUID/UDID）
 */
- (void)getRegisterId:(CDVInvokedUrlCommand*)command{

    NSString *deviceId =  [[AliyunNotificationLauncher sharedAliyunNotificationLauncher] getDeviceId];
    NSLog(@"deviceId----: %@",deviceId);
    NSLog(@"[CloudPushSDK getDeviceId]----: %@",[CloudPushSDK getDeviceId]);
    CDVPluginResult *result;
    
    if(deviceId.length != 0){
       result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:deviceId];
    }else{
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
    
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

/**
 * 阿里云推送绑定账号名
 */
- (void)bindAccount:(CDVInvokedUrlCommand*)command{
    
    NSString* account = [command.arguments objectAtIndex:0];
    
    if(account.length != 0){
     
        [[AliyunNotificationLauncher sharedAliyunNotificationLauncher] bindAccountWithAccount:account andCallback:^(BOOL result) {
           
            CDVPluginResult *cdvresult;
            
            if(result){
                cdvresult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            }else{
                cdvresult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
            }
            
            [self.commandDelegate sendPluginResult:cdvresult callbackId:command.callbackId];

        }];
    }

}


/**
 *绑定标签
 */
- (void)bindTags:(CDVInvokedUrlCommand*)command{
    NSArray *tags = [command.arguments objectAtIndex:0];
    
    [[AliyunNotificationLauncher sharedAliyunNotificationLauncher] bindTagsWithTags:tags andCallback:^(BOOL result) {
      
        CDVPluginResult *cdvresult;
        
        if(result){
            cdvresult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        }else{
            cdvresult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        }
        
        [self.commandDelegate sendPluginResult:cdvresult callbackId:command.callbackId];
        
    }];
}

/**
 *解绑定标签
 */
- (void)unbindTags:(CDVInvokedUrlCommand*)command{
    NSArray *tags = [command.arguments objectAtIndex:0];

    [[AliyunNotificationLauncher sharedAliyunNotificationLauncher] unbindTagsWithTags:tags andCallback:^(BOOL result) {
        
        CDVPluginResult *cdvresult;
        
        if(result){
            cdvresult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        }else{
            cdvresult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        }
        
        [self.commandDelegate sendPluginResult:cdvresult callbackId:command.callbackId];
        
    }];
    
}

/**
 *查询标签
 */
- (void)listTags:(CDVInvokedUrlCommand*)command{
    
    [[AliyunNotificationLauncher sharedAliyunNotificationLauncher] listTagsAndCallback:^(id result) {
       
        CDVPluginResult *cdvresult;
        
        if(result == [NSNull null] ){
            
            cdvresult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        }else{
            cdvresult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:(NSDictionary *)result];
        }
        
        [self.commandDelegate sendPluginResult:cdvresult callbackId:command.callbackId];
        
    }];
    
}




@end






