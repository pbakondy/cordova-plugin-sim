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

import java.lang.reflect.Method;
import java.util.List;

public class Sim extends CordovaPlugin {
  private static final String LOG_TAG = "CordovaPluginSim";
  private static final String GET_SIM_INFO = "getSimInfo";
  private static final String HAS_READ_PERMISSION = "hasReadPermission";
  private static final String REQUEST_READ_PERMISSION = "requestReadPermission";
  private final String[] permissions = { Manifest.permission.READ_PHONE_STATE
      // ,Manifest.permission.READ_PHONE_NUMBERS
      // ,Manifest.permission.READ_SMS
  };

  private CallbackContext callback;

  private final int ANDROID_VERSION_Q = 29;

  @SuppressLint("HardwareIds")
  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    this.callback = callbackContext;

    switch (action) {
    case GET_SIM_INFO:
      return getSimInfo();

    case HAS_READ_PERMISSION:
      return hasReadPermission();

    case REQUEST_READ_PERMISSION:
      return requestReadPermission();

    default:
      return false;
    }

  }

  private boolean getSimInfo() throws JSONException {
    Context context = this.cordova.getActivity().getApplicationContext();
    TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

    JSONObject result = new JSONObject();

    setBasicSimInfo(result, manager);
    trySetPhoneCount(result, manager);
    trySetSimCards(result, manager, context);
    trySetSensitiveInfo(result, manager, context);
    this.callback.success(result);
    return true;
  }

  private boolean hasReadPermission() {
    this.callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, simAllPermissionsGranted()));
    return true;
  }

  private boolean requestReadPermission() {
    requestPermission();
    return true;
  }

  private void trySetPhoneCount(JSONObject result, TelephonyManager manager) throws JSONException {
    // TelephonyManager.getPhoneCount() requires API 23
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M)
      return;

    result.put("phoneCount", manager.getPhoneCount());
  }

  // anything that requires READ_PHONE_STATE, READ_PHONE_NUMBERS or other
  // permissions
  private void trySetSensitiveInfo(JSONObject result, TelephonyManager manager, Context context) throws JSONException {
    if (!simAllPermissionsGranted())
      return;

    result.put("phoneNumber", manager.getLine1Number());
    result.put("deviceSoftwareVersion", manager.getDeviceSoftwareVersion());
    result.put("networkType", manager.getNetworkType());

    if (android.os.Build.VERSION.SDK_INT >= ANDROID_VERSION_Q) {
      result.put("deviceId", Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
    } else {
      result.put("deviceId", manager.getDeviceId());
    }

    if (android.os.Build.VERSION.SDK_INT < ANDROID_VERSION_Q) {
      result.put("simSerialNumber", manager.getSimSerialNumber());
      result.put("subscriberId", manager.getSubscriberId());
    }
  }

  private void setBasicSimInfo(JSONObject result, TelephonyManager manager) throws JSONException {
    String simOperator = manager.getSimOperator();
    if (simOperator.length() >= 3) {
      result.put("mcc", simOperator.substring(0, 3));
      result.put("mnc", simOperator.substring(3));
    }
    result.put("carrierName", manager.getSimOperatorName());
    result.put("countryCode", manager.getSimCountryIso());
    result.put("callState", manager.getCallState());
    result.put("dataActivity", manager.getDataActivity());
    result.put("phoneType", manager.getPhoneType());
    result.put("simState", manager.getSimState());
    result.put("isNetworkRoaming", manager.isNetworkRoaming());
  }

  // dual SIM detection with SubscriptionManager API
  private void trySetSimCards(JSONObject result, TelephonyManager manager, Context context) {
    try {
      // requires API 22
      if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP_MR1)
        return;

      // requires permission READ_PHONE_STATE
      if (!simAllPermissionsGranted())
        return;

      JSONArray sims = new JSONArray();

      SubscriptionManager subscriptionManager = (SubscriptionManager) context
          .getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

      result.put("activeSubscriptionInfoCount", subscriptionManager.getActiveSubscriptionInfoCount());
      result.put("activeSubscriptionInfoCountMax", subscriptionManager.getActiveSubscriptionInfoCountMax());

      List<SubscriptionInfo> subscriptionInfos = subscriptionManager.getActiveSubscriptionInfoList();
      for (SubscriptionInfo subscriptionInfo : subscriptionInfos) {

        JSONObject simData = new JSONObject();

        simData.put("carrierName", subscriptionInfo.getCarrierName().toString());
        simData.put("displayName", subscriptionInfo.getDisplayName().toString());
        simData.put("countryCode", subscriptionInfo.getCountryIso());
        simData.put("mcc", subscriptionInfo.getMcc());
        simData.put("mnc", subscriptionInfo.getMnc());
        simData.put("isDataRoaming", (subscriptionInfo.getDataRoaming() == 1));// 1 is enabled ; 0 is disabled
        simData.put("phoneNumber", subscriptionInfo.getNumber());
        simData.put("subscriptionId", subscriptionInfo.getSubscriptionId());

        if (android.os.Build.VERSION.SDK_INT < ANDROID_VERSION_Q) {
          simData.put("simSerialNumber", subscriptionInfo.getIccId());
        }

        int simSlotIndex = subscriptionInfo.getSimSlotIndex();
        simData.put("simSlotIndex", simSlotIndex);
        simData.put("isNetworkRoaming", subscriptionManager.isNetworkRoaming(simSlotIndex));

        // TelephonyManager.getDeviceId(slotId) requires API 23
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M
            && android.os.Build.VERSION.SDK_INT < ANDROID_VERSION_Q) {
          simData.put("deviceId", manager.getDeviceId(simSlotIndex));
        }

        sims.put(simData);

      }
      if (sims == null || sims.length() == 0)
        return;

      trySetDefaultSubscriptionId(result);
      result.put("cards", sims);

    } catch (JSONException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void trySetDefaultSubscriptionId(JSONObject result) throws JSONException {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      // android.telephony.SubscriptionManager#getDefaultDataSubscriptionId requires
      // API 24
      result.put("defaultDataSubscriptionId", SubscriptionManager.getDefaultDataSubscriptionId());
    } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
      // android.telephony.SubscriptionManager#getDefaultDataSubId requires API 22,
      // must be accessed through reflection
      try {
        Method getDefaultDataSubId = SubscriptionManager.class.getDeclaredMethod("getDefaultDataSubId");
        getDefaultDataSubId.setAccessible(true);
        result.put("defaultDataSubscriptionId", getDefaultDataSubId.invoke(null));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private boolean simPermissionGranted(String type) {
    if (Build.VERSION.SDK_INT < 23) {
      return true;
    }
    return cordova.hasPermission(type);
  }

  private boolean simAllPermissionsGranted() {
    for (String permission : this.permissions)
      if (!simPermissionGranted(permission))
        return false;
    return true;
  }

  private void requestPermission() {
    LOG.i(LOG_TAG, "requestPermission");
    if (!simAllPermissionsGranted()) {
      cordova.requestPermissions(this, 12345, this.permissions);
    } else {
      this.callback.success();
    }
  }

  @Override
  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
      throws JSONException {
    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      this.callback.success();
    } else {
      this.callback.error("Permission denied");
    }
  }
}
