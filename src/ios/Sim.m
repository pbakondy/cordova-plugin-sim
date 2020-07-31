// MCC and MNC codes on Wikipedia
// http://en.wikipedia.org/wiki/Mobile_country_code

// Mobile Network Codes (MNC) for the international identification plan for public networks and subscriptions
// http://www.itu.int/pub/T-SP-E.212B-2014

// class CTCarrier
// https://developer.apple.com/reference/coretelephony/ctcarrier?language=objc

#import "Sim.h"
#import <Cordova/CDV.h>
#import <Foundation/Foundation.h>
#import <CoreTelephony/CTCarrier.h>
#import <CoreTelephony/CTTelephonyNetworkInfo.h>

@implementation Sim

- (void)getSimInfo:(CDVInvokedUrlCommand*)command
{
  CTTelephonyNetworkInfo *netinfo = [[CTTelephonyNetworkInfo alloc] init];
  CTCarrier *carrier = [netinfo subscriberCellularProvider];

  NSMutableDictionary *carriers = [[NSMutableDictionary alloc] init];
  if (@available(iOS 12.0, *)) {
      NSDictionary *carrierObjects = [netinfo serviceSubscriberCellularProviders];
      for (NSString* key in carrierObjects) {
          carriers[key] = [NSDictionary dictionaryWithObjectsAndKeys:
          @([carrierObjects[key] allowsVOIP]), @"allowsVOIP",
          [carrierObjects[key] carrierName], @"carrierName",
          [carrierObjects[key] isoCountryCode], @"countryCode",
          [carrierObjects[key] mobileCountryCode], @"mcc",
          [carrierObjects[key] mobileNetworkCode], @"mnc",
          nil];
      }
  }

  BOOL allowsVOIPResult = [carrier allowsVOIP];
  NSString *carrierNameResult = [carrier carrierName];
  NSString *carrierCountryResult = [carrier isoCountryCode];
  NSString *carrierCodeResult = [carrier mobileCountryCode];
  NSString *carrierNetworkResult = [carrier mobileNetworkCode];

  if (!carrierNameResult) {
    carrierNameResult = @"";
  }
  if (!carrierCountryResult) {
    carrierCountryResult = @"";
  }
  if (!carrierCodeResult) {
    carrierCodeResult = @"";
  }
  if (!carrierNetworkResult) {
    carrierNetworkResult = @"";
  }

  NSDictionary *simData = [NSDictionary dictionaryWithObjectsAndKeys:
    @(allowsVOIPResult), @"allowsVOIP",
    carrierNameResult, @"carrierName",
    carrierCountryResult, @"countryCode",
    carrierCodeResult, @"mcc",
    carrierNetworkResult, @"mnc",               
    carriers, @"cards",
    nil];

  CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:simData];

  [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end
