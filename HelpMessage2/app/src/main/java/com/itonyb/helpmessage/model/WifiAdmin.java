package com.itonyb.helpmessage.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itonyb.helpmessage.R;
import com.itonyb.helpmessage.holders.WifiModuleHolder;

import java.util.ArrayList;
import java.util.List;
/**
 * This class is used to manage all the information for Wi-Fi
 */
public class WifiAdmin extends RecyclerView.Adapter<WifiModuleHolder> {
    private static final String TAG="WIFIADMIN-";
    private WifiManager mWifiManager;
    private Context context;

    // List of all RescueNet Wi-Fis
    private List<WifiModule> rescueNetModules;
    // Connected Wi-Fi
    private WifiModule choosenModule;

    //This is the name of the WIFLY module SSID being hard coded
    final private String SSID = "RescueNet";
    final private String PSK = "pass12345";

    //Constructor
    public WifiAdmin(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.context = context;
        this.rescueNetModules = new ArrayList();
    }

    // If the Wi-Fi is not enable, enables it on the user phone
    public boolean enableWifi() {
        String Tag = TAG + "EnableWifi";
        boolean bRes = true;
        if(!isWifiEnabled()){
            if (mWifiManager.getWifiState()!= WifiManager.WIFI_STATE_ENABLING){
                Log.d(Tag, "Setting enable wifi...");
                bRes = mWifiManager.setWifiEnabled(true);
            }
        }
        Log.d(Tag, "Result:" + bRes);
        return bRes;
    }

    // Checks if the Wi-Fi is enable on the phone
    public boolean isWifiEnabled(){
        return mWifiManager.isWifiEnabled();
    }

    // Connect to the Wi-Fi
    public boolean connectModule(){
        String Tag = TAG + "RunConfig";
        Log.d(Tag, "Creating RescueNet configuration");
        WifiModule module = choosenModule;
        // If it is null, it has not been chosen on the List
        // So it connects to the one with the highest signal
        if(module == null){
            Log.d(Tag, "Getting highest signal...");
            module = getHighestSignal();
        }
        if(module == null)
            return false;

        Log.d(Tag, "SSID:" + module.getSsid());
        // Runs the configuration for the Wi-Fi
        return module.ConfigModule(mWifiManager);
    }

    public void setChoosenModule(WifiModule wifiModule){
        choosenModule = wifiModule;
    }

    // Get the Wi-Fi with the highest signal
    private WifiModule getHighestSignal(){
        WifiModule module = null;
        int signal = Integer.MIN_VALUE;
        // Goes through the list checking the signal
        for(WifiModule wifiModule : rescueNetModules){
            int wifiSignal = wifiModule.getSignalPercentage();
            if(wifiSignal > signal) {
                module = wifiModule;
                signal = wifiSignal;
            }
        }
        return module;
    }

    // Searches all nearby AP for the RescueNet network and puts then
    // on the list of Wi-Fi
    public boolean scanForRescueNet(){
        String Tag = "ScanForRescueNet";
        // Scan the available Wi-Fi
        boolean scan = mWifiManager.startScan();
        // Clear the list
        rescueNetModules.clear();
        Log.d(Tag,"scan: "+scan);

        Log.d(Tag, "WifiState:" + mWifiManager.getWifiState());

        List<ScanResult> scanResultList = mWifiManager.getScanResults();
        Log.d(Tag,"all AP:" +String.valueOf(scanResultList.size()));

        // Look into the scan Wi-Fi and if starts with RescueNet,
        // put into the list
        for (ScanResult scanResult : scanResultList){
            WifiModule module = WifiModule.getWifiModuleInstance(scanResult);
            if(module != null)
                rescueNetModules.add(module);
        }
        return rescueNetModules.size() > 0;
    }

    // Checks to see if Wifi is connected to a network
    public boolean isWifiConnect() {
        String Tag = "IsWifiConnect";
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifiNetworkInfo = mConnectivityManager.getNetworkInfo(mConnectivityManager.getActiveNetwork());
        Log.d(Tag, "Info null?" + (mWifiNetworkInfo == null));
        return mWifiNetworkInfo != null && mWifiNetworkInfo.isConnected();
    }

    // Get the Wi-Fi which the phone is connected
    public WifiModule getWifiModuleConnected(){
        String Tag = TAG + "-GetConnAdm";
        WifiInfo info = mWifiManager.getConnectionInfo();
        if(info == null) {
            Log.d(Tag, "Info is null");
            return null;
        }
        String ssid = info.getSSID().replace("\"", "");
        Log.d(Tag, "SSID:" + ssid);
        Log.d(Tag, "RescueNetLen:" + rescueNetModules.size());
        for(WifiModule wifiModule : rescueNetModules){
            if(wifiModule.getSsid().equals(ssid)) {
                Log.d(Tag, "WifiModule found");
                wifiModule.setIpAddress(getLocalIpAddress());
                return wifiModule;
            }
        }
        Log.d(Tag, "WifiModule not found");
        return null;
    }

    // Calculates the IP address for the phone
    public String getLocalIpAddress(){
        WifiInfo info = mWifiManager.getConnectionInfo();
        if(info == null)
            return null;

        int ip = info.getIpAddress();
        int ip3 = ip/0x1000000;
        int ip3mod = ip%0x1000000;

        int ip2 = ip3mod/0x10000;
        int ip2mod = ip3mod%0x10000;

        int ip1 = ip2mod/0x100;
        int ip0 = ip2mod%0x100;
        return String.valueOf(ip0)
                + "." + String.valueOf(ip1)
                + "." + String.valueOf(ip2)
                + "." + String.valueOf(ip3);
    }

    @Override
    public WifiModuleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_wifi,
                parent, false);
        return new WifiModuleHolder(view);
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item, parent,
        //        false);
    }

    @Override
    public void onBindViewHolder(WifiModuleHolder holder, int position) {
        holder.bind(rescueNetModules.get(position));
    }

    @Override
    public int getItemCount() {
        return rescueNetModules.size();
    }
}
