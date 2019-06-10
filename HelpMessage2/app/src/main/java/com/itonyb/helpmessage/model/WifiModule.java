package com.itonyb.helpmessage.model;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by HenriqueJunger on 14/06/17.
 * Class that represents a Wi-Fi
 */
public class WifiModule {
    private final String PREFIX_SSID = "RescueNet";
    private final String PASSWORD = "pass12345";
    private final int TOTAL_PERC = 100;
    private final String TAG = "WifiModule";


    private String ssid;
    private int signalStrength;
    private String ipAddress;
    private WifiConfiguration configuration;

    // If the Scanned Wi-Fi starts with RescueNet
    // Returns the WifiModule instance
    // if not returns null
    public static WifiModule getWifiModuleInstance(ScanResult scan){
        String scanSsid = scan.SSID;
        WifiModule module = new WifiModule(scan.SSID, scan.level);
        if(!module.checkSsid(scanSsid)){
            return null;
        }
        module.signalStrength = scan.level;
        return module;
    }

    private WifiModule(String ssid, int signalStrength) {
        this.ssid = ssid;
        this.signalStrength = signalStrength;
    }

    public String getSsid() {
        return ssid;
    }

    private boolean checkSsid(String ssid){
        return ssid.startsWith(PREFIX_SSID);
    }

    public int getSignalPercentage(){
        return TOTAL_PERC + signalStrength; // 100 + (-dBm);
    }

    public String getSignalPercLabel(){
        return getSignalPercentage() + "%";
    }

    public String getIpAddress(){
        return ipAddress;
    }

    public void setIpAddress(String value){
        this.ipAddress = value;
    }

    public String getPassword(){
        return PASSWORD;
    }

    public int getNetWorkId(){
        return configuration != null ? configuration.networkId : -1;
    }

    // Search the available Wi-Fi to return the configuration for the connected one
    private WifiConfiguration GetConfigNetwork(WifiManager wifiManager){
        if(configuration != null)
            return configuration;

        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if (existingConfigs.size() > 0){
            for (WifiConfiguration existingConfig : existingConfigs)
            {
                if (existingConfig.SSID.equals("\"" + getSsid() + "\""))
                {
                    return existingConfig;
                }
            }
        }
        return null;
    }

    // Run the configuration to connect to a Wi-Fi
    public boolean ConfigModule(WifiManager wifiManager){
        String Tag = TAG + "-ConfigModule";
        WifiConfiguration conf = GetConfigNetwork(wifiManager);
        if (conf == null){
            conf = new WifiConfiguration();
            conf.SSID = "\"" + getSsid() + "\"";
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

            conf.preSharedKey = "\""+ getPassword()+ "\"";
            wifiManager.addNetwork(conf);
        }

        configuration = conf;
        Log.d(Tag, "NetworkID:" + configuration.networkId);
        boolean disconnect = wifiManager.disconnect();
        Log.d(Tag, "Disconnect?" + disconnect);
        boolean enabledNetworkd = wifiManager.enableNetwork(configuration.networkId, true);
        Log.d(Tag, "Enabled?" + enabledNetworkd);
        boolean reconnect = wifiManager.reconnect();
        Log.d(Tag, "Reconnected?" + reconnect);

        Log.d(Tag, "Connect:" + getSsid());
        return disconnect &&
               enabledNetworkd &&
               reconnect;
    }

}
