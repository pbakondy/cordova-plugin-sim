// MCC and MNC codes on Wikipedia
// http://en.wikipedia.org/wiki/Mobile_country_code

// Mobile Network Codes (MNC) for the international identification plan for public networks and subscriptions
// http://www.itu.int/pub/T-SP-E.212B-2014

// class TelephonyManager
// http://developer.android.com/reference/android/telephony/TelephonyManager.html
// https://github.com/android/platform_frameworks_base/blob/master/telephony/java/android/telephony/TelephonyManager.java

// permissions
// http://developer.android.com/training/permissions/requesting.html

// Multiple SIM Card Support
// https://developer.android.com/about/versions/android-5.1.html

// class SubscriptionManager
// https://developer.android.com/reference/android/telephony/SubscriptionManager.html
// https://github.com/android/platform_frameworks_base/blob/master/telephony/java/android/telephony/SubscriptionManager.java

// class SubscriptionInfo
// https://developer.android.com/reference/android/telephony/SubscriptionInfo.html
// https://github.com/android/platform_frameworks_base/blob/master/telephony/java/android/telephony/SubscriptionInfo.java

// Cordova Permissions API
// https://cordova.apache.org/docs/en/latest/guide/platforms/android/plugin.html#android-permissions

package com.pbakondy;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings.Secure;
import android.os.Build;
import android.Manifest;

import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import java.util.List;

public class Sim extends CordovaPlugin {
  private static final String LOG_TAG = "CordovaPluginSim";


  private static final String GET_SIM_INFO = "getSimInfo";
  private static final String HAS_READ_PERMISSION = "hasReadPermission";
  private static final String REQUEST_READ_PERMISSION = "requestReadPermission";

  private CallbackContext callback;

  @SuppressLint("HardwareIds")
  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    callback = callbackContext;

    if (GET_SIM_INFO.equals(action)) {
      Context context = this.cordova.getActivity().getApplicationContext();

      TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

      // dual SIM detection with SubscriptionManager API
      // requires API 22
      // requires permission READ_PHONE_STATE
      JSONArray sims = null;
      Integer phoneCount = null;
      Integer activeSubscriptionInfoCount = null;
      Integer activeSubscriptionInfoCountMax = null;
   LOG.d(LOG_TAG, "rupendra-1");
           
      try {
        // TelephonyManager.getPhoneCount() requires API 23
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
          phoneCount = manager.getPhoneCount();
        }
 LOG.d(LOG_TAG, "rupendra-2");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
 LOG.d(LOG_TAG, "rupendra-3");
          if (simPermissionGranted(Manifest.permission.READ_PHONE_STATE)) {
 LOG.d(LOG_TAG, "rupendra-4");
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            activeSubscriptionInfoCount = subscriptionManager.getActiveSubscriptionInfoCount();
            activeSubscriptionInfoCountMax = subscriptionManager.getActiveSubscriptionInfoCountMax();

            sims = new JSONArray();
 LOG.d(LOG_TAG, "rupendra-5");
            List<SubscriptionInfo> subscriptionInfos = subscriptionManager.getActiveSubscriptionInfoList();
            for (SubscriptionInfo subscriptionInfo : subscriptionInfos) {

              CharSequence carrierName = subscriptionInfo.getCarrierName();
              String countryIso = subscriptionInfo.getCountryIso();
              int dataRoaming = subscriptionInfo.getDataRoaming();  // 1 is enabled ; 0 is disabled
              CharSequence displayName = subscriptionInfo.getDisplayName();
              String iccId = "";
              
              if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
                iccId = subscriptionInfo.getIccId();
              }
              
              int mcc = subscriptionInfo.getMcc();
              int mnc = subscriptionInfo.getMnc();
              String number = subscriptionInfo.getNumber();
              int simSlotIndex = subscriptionInfo.getSimSlotIndex();
              int subscriptionId = subscriptionInfo.getSubscriptionId();

              boolean networkRoaming = subscriptionManager.isNetworkRoaming(simSlotIndex);
 LOG.d(LOG_TAG, "rupendra-6");
              String deviceId = null;
              // TelephonyManager.getDeviceId(slotId) requires API 23
              if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
                deviceId = manager.getDeviceId(simSlotIndex);
              }

              JSONObject simData = new JSONObject();
 LOG.d(LOG_TAG, "rupendra-7);
              simData.put("carrierName", carrierName.toString());
              simData.put("displayName", displayName.toString());
              simData.put("countryCode", countryIso);
              simData.put("mcc", mcc);
              simData.put("mnc", mnc);
              simData.put("isNetworkRoaming", networkRoaming);
              simData.put("isDataRoaming", (dataRoaming == 1));
              simData.put("simSlotIndex", simSlotIndex);
              simData.put("phoneNumber", number);
              if (deviceId != null) {
                simData.put("deviceId", deviceId);
              }
              simData.put("simSerialNumber", iccId);
              simData.put("subscriptionId", subscriptionId);
 LOG.d(LOG_TAG, "rupendra-8");
              sims.put(simData);

            }
          }
        }
      } catch (JSONException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }

      String phoneNumber = null;
      String countryCode = manager.getSimCountryIso();
      String simOperator = manager.getSimOperator();
      String carrierName = manager.getSimOperatorName();

      String deviceId = null;
      String deviceSoftwareVersion = null;
      String simSerialNumber = null;
      String subscriberId = null;
 LOG.d(LOG_TAG, "rupendra-9");
      int callState = manager.getCallState();
       LOG.d(LOG_TAG, "rupendra-9-1");
      int dataActivity = manager.getDataActivity();
       LOG.d(LOG_TAG, "rupendra-9-2");
      int networkType = manager.getNetworkType();
       LOG.d(LOG_TAG, "rupendra-9-3");
      int phoneType = manager.getPhoneType();
       LOG.d(LOG_TAG, "rupendra-9-4");
      int simState = manager.getSimState();
       LOG.d(LOG_TAG, "rupendra-9-5");

      boolean isNetworkRoaming = manager.isNetworkRoaming();
LOG.d(LOG_TAG, "rupendra-9-6");
      if (simPermissionGranted(Manifest.permission.READ_PHONE_STATE)) {
        LOG.d(LOG_TAG, "rupendra-9-7");
        phoneNumber = manager.getLine1Number();
          LOG.d(LOG_TAG, "rupendra-9-8");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
          LOG.d(LOG_TAG, "rupendra-9-9");
           deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        }else{
          LOG.d(LOG_TAG, "rupendra-9-10");
          deviceId = manager.getDeviceId();
        }
        LOG.d(LOG_TAG, "rupendra-9-11");
        deviceSoftwareVersion = manager.getDeviceSoftwareVersion();
        LOG.d(LOG_TAG, "rupendra-9-12");
        simSerialNumber = manager.getSimSerialNumber();
        LOG.d(LOG_TAG, "rupendra-9-13");
        subscriberId = manager.getSubscriberId();
        LOG.d(LOG_TAG, "rupendra-9-14");
      }
 LOG.d(LOG_TAG, "rupendra-10");
      String mcc = "";
      String mnc = "";

      if (simOperator.length() >= 3) {
        mcc = simOperator.substring(0, 3);
        mnc = simOperator.substring(3);
      }

      JSONObject result = new JSONObject();

      result.put("carrierName", carrierName);
      result.put("countryCode", countryCode);
      result.put("mcc", mcc);
      result.put("mnc", mnc);
 LOG.d(LOG_TAG, "rupendra-11");
      result.put("callState", callState);
      result.put("dataActivity", dataActivity);
      result.put("networkType", networkType);
      result.put("phoneType", phoneType);
      result.put("simState", simState);
 LOG.d(LOG_TAG, "rupendra-12");
      result.put("isNetworkRoaming", isNetworkRoaming);

      if (phoneCount != null) {
        result.put("phoneCount", (int)phoneCount);
      }
      if (activeSubscriptionInfoCount != null) {
        result.put("activeSubscriptionInfoCount", (int)activeSubscriptionInfoCount);
      }
      if (activeSubscriptionInfoCountMax != null) {
        result.put("activeSubscriptionInfoCountMax", (int)activeSubscriptionInfoCountMax);
      }
 LOG.d(LOG_TAG, "rupendra-13");
      if (simPermissionGranted(Manifest.permission.READ_PHONE_STATE)) {
        result.put("phoneNumber", phoneNumber);
        result.put("deviceId", deviceId);
        result.put("deviceSoftwareVersion", deviceSoftwareVersion);
        result.put("simSerialNumber", simSerialNumber);
        result.put("subscriberId", subscriberId);
      }
 LOG.d(LOG_TAG, "rupendra-14");
      if (sims != null && sims.length() != 0) {
        result.put("cards", sims);
      }
 LOG.d(LOG_TAG, "rupendra-15");
      callbackContext.success(result);
 LOG.d(LOG_TAG, "rupendra-16");
      return true;
    } else if (HAS_READ_PERMISSION.equals(action)) {
 LOG.d(LOG_TAG, "rupendra-17");
      hasReadPermission();
      return true;
    } else if (REQUEST_READ_PERMISSION.equals(action)) {
       LOG.d(LOG_TAG, "rupendra-18");
      requestReadPermission();
      return true;
    } else {
       LOG.d(LOG_TAG, "rupendra-19");
      return false;
    }
  }

  private void hasReadPermission() {
    this.callback.sendPluginResult(new PluginResult(PluginResult.Status.OK,
      simPermissionGranted(Manifest.permission.READ_PHONE_STATE)));
  }

  private void requestReadPermission() {
    requestPermission(Manifest.permission.READ_PHONE_STATE);
  }

  private boolean simPermissionGranted(String type) {
    if (Build.VERSION.SDK_INT < 23) {
      return true;
    }
    return cordova.hasPermission(type);
  }

  private void requestPermission(String type) {
    LOG.i(LOG_TAG, "requestPermission");
    if (!simPermissionGranted(type)) {
      cordova.requestPermission(this, 12345, type);
    } else {
      this.callback.success();
    }
  }

  @Override
  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException
  {
    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      this.callback.success();
    } else {
      this.callback.error("Permission denied");
    }
  }
}
