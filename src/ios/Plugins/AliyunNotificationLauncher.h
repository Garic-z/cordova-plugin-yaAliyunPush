//
//  SevenAppNotificationLauncher.h
//  SevenPush
//


#import <Foundation/Foundation.h>
#import <UserNotifications/UserNotifications.h>

@interface AliyunNotificationLauncher : NSObject<UNUserNotificationCenterDelegate>

+ (id)sharedAliyunNotificationLauncher;

- (void)didFinishLaunchingWithOptions:(NSDictionary *)launchOptions andApplication:(UIApplication *)application;

#pragma mark - application notification delegate

- (void)didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken;

- (void)didReceiveRemoteNotification:(NSDictionary *)userInfo andApplication:(UIApplication *)application;

#pragma mark - 绑定信息

- (NSString *)getDeviceId;

- (void)bindAccountWithAccount:(NSString *)account andCallback:(void (^)(BOOL result))callback;

- (void)bindTagsWithTags:(NSArray *)tags andCallback:(void (^)(BOOL result))callback;

- (void)unbindTagsWithTags:(NSArray *)tags andCallback:(void (^)(BOOL result))callback;

- (void)listTagsAndCallback:(void (^)(id result))callback;


@end
