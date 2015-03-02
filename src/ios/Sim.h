#import <Cordova/CDV.h>
#import <CoreTelephony/CTCarrier.h>
#import <CoreTelephony/CTTelephonyNetworkInfo.h>

@interface Sim : CDVPlugin

- (void)getSimInfo:(CDVInvokedUrlCommand*)command;

@end
