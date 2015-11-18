# cordova-plugin-sim

[![Code Climate](https://codeclimate.com/github/pbakondy/cordova-plugin-sim/badges/gpa.svg)](https://codeclimate.com/github/pbakondy/cordova-plugin-sim)

This is a cordova plugin to get data from the SIM card like the carrier name, mcc, mnc and country code and other system dependent additional info.


## Installation

```
cordova plugin add cordova-plugin-sim
```

## Supported Platforms

- Android
- iOS
- Windows Phone 7 and 8


## Usage

```js
document.addEventListener("deviceready", onDeviceReady, false);

function onDeviceReady() {
  window.plugins.sim.getSimInfo(successCallback, errorCallback);
}

function successCallback(result) {
  console.log(result);
}

function errorCallback(error) {
  console.log(error);
}
```

The plugin returns a JSON object. Return values:

* `carrierName`: String - the Service Provider Name (SPN)
* `countryCode`: String - the ISO country code equivalent for the SIM provider's country code
* `mcc`: String - the MCC (mobile country code) of the provider of the SIM
* `mnc`: String - the MNC (mobile network code) of the provider of the SIM

Field carrierName may remain empty, dependent on the mobile provider.

On Windows Phone access to countryCode, MCC and MNC is not made provided (returns empty string).

You can extract country and carrier data from MCC and MNC codes, read further on [Wikipedia](http://en.wikipedia.org/wiki/Mobile_country_code) and [ITU-T](http://www.itu.int/pub/T-SP-E.212B-2014).


## Android Quirks

Additional return values:

* `phoneNumber`: String - phone number string for line 1, for example, the [MSISDN](http://en.wikipedia.org/wiki/MSISDN) for a GSM phone <sup>1</sup>
* `deviceId`: String - the unique device ID, for example, the IMEI for GSM and the MEID or ESN for CDMA phones
* `deviceSoftwareVersion`: String - the software version number for the device, for example, the IMEI/SV for GSM phones
* `simSerialNumber`: String - the serial number of the SIM, if applicable
* `subscriberId`: String - the unique subscriber ID, for example, the IMSI for a GSM phone
* `callState`: Number - the call state (cellular) on the device
* `dataActivity`: Number - the type of activity on a data connection (cellular)
* `networkType`: Number - the NETWORK_TYPE_xxxx for current data connection
* `phoneType`: Number - the device phone type. This indicates the type of radio used to transmit voice calls
* `simState`: Number - the state of the device SIM card
* `isNetworkRoaming`: Boolean - true if the device is considered roaming on the current network, for GSM purposes

<sup>1)</sup> Notice: the content of phoneNumber is unreliable (see [this](http://stackoverflow.com/questions/7922734/getting-reliable-msisdn-from-android-phone-voicemailnumber-line1number) and [this](http://stackoverflow.com/questions/25861064/retrieving-line1-number-from-telephonymanager-in-android) article).
Sometimes phoneNumber value is only an empty string.


### List of Call State Codes and Meanings

| Code | Constant                      | Meaning
|-----:|:------------------------------|--------
|    0 | `CALL_STATE_IDLE`             | No activity
|    1 | `CALL_STATE_RINGING`          | Ringing. A new call arrived and is ringing or waiting. In the latter case, another call is already active.
|    2 | `CALL_STATE_OFFHOOK`          | Off-hook. At least one call exists that is dialing, active, or on hold, and no calls are ringing or waiting.


### List of Data Activity Codes and Meanings

| Code | Constant                      | Meaning
|-----:|:------------------------------|--------
|    0 | `DATA_ACTIVITY_NONE`          | No traffic.
|    1 | `DATA_ACTIVITY_IN`            | Currently receiving IP PPP traffic.
|    2 | `DATA_ACTIVITY_OUT`           | Currently sending IP PPP traffic.
|    3 | `DATA_ACTIVITY_INOUT`         | Currently both sending and receiving IP PPP traffic.
|    4 | `DATA_ACTIVITY_DORMANT`       | Data connection is active, but physical link is down


### List of Network Type Codes and Meanings

| Code | Constant                      | Meaning
|-----:|:------------------------------|--------
|    0 | `NETWORK_TYPE_UNKNOWN`        | unknown
|    1 | `NETWORK_TYPE_GPRS`           | GPRS
|    2 | `NETWORK_TYPE_EDGE`           | EDGE
|    3 | `NETWORK_TYPE_UMTS`           | UMTS
|    4 | `NETWORK_TYPE_CDMA`           | CDMA: Either IS95A or IS95B
|    5 | `NETWORK_TYPE_EVDO_0`         | EVDO revision 0
|    6 | `NETWORK_TYPE_EVDO_A`         | EVDO revision A
|    7 | `NETWORK_TYPE_1xRTT`          | 1xRTT
|    8 | `NETWORK_TYPE_HSDPA`          | HSDPA
|    9 | `NETWORK_TYPE_HSUPA`          | HSUPA
|   10 | `NETWORK_TYPE_HSPA`           | HSPA
|   11 | `NETWORK_TYPE_IDEN`           | iDen
|   12 | `NETWORK_TYPE_EVDO_B`         | EVDO revision B
|   13 | `NETWORK_TYPE_LTE`            | LTE
|   14 | `NETWORK_TYPE_EHRPD`          | eHRPD
|   15 | `NETWORK_TYPE_HSPAP`          | HSPA+


### List of Phone Type Codes and Meanings

| Code | Constant                      | Meaning
|-----:|:------------------------------|--------
|    0 | `PHONE_TYPE_NONE`             | none
|    1 | `PHONE_TYPE_GSM`              | GSM
|    2 | `PHONE_TYPE_CDMA`             | CDMA
|    3 | `PHONE_TYPE_SIP`              | SIP


### List of SIM State Codes and Meanings

| Code | Constant                      | Meaning
|-----:|:------------------------------|--------
|    0 | `SIM_STATE_UNKNOWN`           | Unknown. Signifies that the SIM is in transition between states. For example, when the user inputs the SIM pin under PIN_REQUIRED state, a query for sim status returns this state before turning to SIM_STATE_READY.
|    1 | `SIM_STATE_ABSENT`            | No SIM card is available in the device
|    2 | `SIM_STATE_PIN_REQUIRED`      | Locked: requires the user's SIM PIN to unlock
|    3 | `SIM_STATE_PUK_REQUIRED`      | Locked: requires the user's SIM PUK to unlock
|    4 | `SIM_STATE_NETWORK_LOCKED`    | Locked: requires a network PIN to unlock
|    5 | `SIM_STATE_READY`             | Ready


## iOS Quirks

Additional return value:

* `allowsVOIP`: Boolean - indicates if the carrier allows VoIP calls to be made on its network


## Windows Phone Quirks

Additional return values:

* `isCellularDataEnabled`: Boolean - indicates whether the network is cellular data enabled
* `isCellularDataRoamingEnabled`: Boolean - indicates whether the network allows data roaming
* `IsNetworkAvailable`: Boolean - indicates whether the network is available
* `isWiFiEnabled`: Boolean - indicates whether the network is Wi-Fi enabled


## Author

#### Peter Bakondy

- https://github.com/pbakondy


## LICENSE

cordova-plugin-sim is licensed under the MIT Open Source license. For more information, see the LICENSE file in this repository.
