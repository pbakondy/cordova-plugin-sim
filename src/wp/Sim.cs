// DeviceNetworkInformation
// https://msdn.microsoft.com/en-us/library/windows/apps/microsoft.phone.net.networkinformation.devicenetworkinformation(v=vs.105).aspx
//
// TODO http://stackoverflow.com/a/21879531

using System;
using Microsoft.Phone.Net.NetworkInformation;

namespace WPCordovaClassLib.Cordova.Commands
{
    public class Sim : BaseCommand
    {
        public void getSimInfo(string notused)
        {

            string res = String.Format("\"carrierName\":\"{0}\",\"countryCode\":\"\",\"mcc\":\"\",\"mnc\":\"\",\"isCellularDataEnabled\":\"{1}\",\"isCellularDataRoamingEnabled\":\"{2}\",\"isNetworkAvailable\":\"{3}\",\"isWiFiEnabled\":\"{4}\"",
                                        this.CellularMobileOperator,
                                        this.IsCellularDataEnabled,
                                        this.IsCellularDataRoamingEnabled,
                                        this.IsNetworkAvailable,
                                        this.IsWiFiEnabled);

            res = "{" + res + "}";

            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, res));
        }

        // Gets the name of the cellular mobile operator.
        public string CellularMobileOperator
        {
            get
            {
                return DeviceNetworkInformation.CellularMobileOperator;
            }
        }

        // Gets a value indicating whether the network is cellular data enabled.
        public bool IsCellularDataEnabled
        {
            get
            {
                return DeviceNetworkInformation.IsCellularDataEnabled;
            }
        }

        // Gets a value indicating whether the network allows data roaming.
        public bool IsCellularDataRoamingEnabled
        {
            get
            {
                return DeviceNetworkInformation.IsCellularDataRoamingEnabled;
            }
        }

        // Gets a value indicating whether the network is available.
        public bool IsNetworkAvailable
        {
            get
            {
                return DeviceNetworkInformation.IsNetworkAvailable;
            }
        }

        // Gets a value indicating whether the network is Wi-Fi enabled.
        public bool IsWiFiEnabled
        {
            get
            {
                return DeviceNetworkInformation.IsWiFiEnabled;
            }
        }

    }
}
