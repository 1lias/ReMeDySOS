package com.itonyb.helpmessage.threads;

import android.util.Log;

import com.itonyb.helpmessage.handlers.MainActivityHandler;
import com.itonyb.helpmessage.model.WifiAdmin;
import com.itonyb.helpmessage.model.WifiModule;


/**
 * Created by HenriqueJunger on 15/06/17.
 *
 */

public class ConnectionThread extends Thread {
    private String TAG = "CONNECTION-";
    private MainActivityHandler handler;
    private WifiAdmin wifiAdmin;


    private final int NOT_ENABLE_WIFI = 3;
    private final int NOT_POSSIBLE_TO_CONNECT = 2;
    private final int OUT_OF_RANGE = 1;
    private final int SUCCESS = 0;

    private final int WAIT_TO_ENABLE_WIFI = 5; // seconds
    private final int FUNC_ENABLE_WIFI = 0;
    private final int WAIT_TO_CONNECT_RESCUE = 5; // seconds
    private final int FUNC_IS_CONNECTED = 1;
    private final int MILI_SECONDS_UNIT = 500;


    public ConnectionThread(WifiAdmin mwifiAdmin,MainActivityHandler handler){
        this.wifiAdmin = mwifiAdmin;
        this.handler = handler;
    }

    public void run() {
        try {
            String Tag = TAG + "Run";
            // Enable Wifi
            Log.d(Tag, "Enabling Wifi...");

            if(!enableWifi()){
                sendMessageHandler(NOT_ENABLE_WIFI, null);
                return;
            }


            // Scan Rescue
            Log.d(Tag, "Scanning...");
            if (!scanRescue()) {
                sendMessageHandler(OUT_OF_RANGE, null);
                return;
            }


            // Connect to Rescue
            Log.d(Tag, "Connecting to Rescue...");
            if(connectToRescue()){
                WifiModule module = wifiAdmin.getWifiModuleConnected();
                if(module != null)
                    Log.d(Tag, "Module:" + module.getSsid());
                else
                    Log.d(Tag, "Module: null");
                sendMessageHandler(SUCCESS, module);
            }else{
                sendMessageHandler(NOT_POSSIBLE_TO_CONNECT, null);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void sendMessageHandler(int message, WifiModule module){
        if(handler != null){
            if(module != null)
                handler.setWifiConnected(module);

            handler.sendEmptyMessage(message);
        }
    }

    private boolean enableWifi(){
        String Tag = TAG + "EnableWifi";
        boolean enabled = wifiAdmin.isWifiEnabled();
        Log.d(Tag, "Enabled:" + enabled);
        if(!enabled){
            enabled = wifiAdmin.enableWifi();
            Log.d(Tag, "Enable return:" + enabled);
            enabled = waitFor(WAIT_TO_ENABLE_WIFI, FUNC_ENABLE_WIFI);
        }
        return enabled;
    }

    private boolean scanRescue(){
        String Tag = TAG + "ScanRescue";
        boolean result = wifiAdmin.scanForRescueNet();
        Log.d(Tag, "Scan:" + result);
        return result;
    }

    private boolean connectToRescue(){
        String Tag = TAG + "ConnToRescue";
        boolean config = wifiAdmin.connectModule();
        boolean connected = false;
        Log.d(Tag, "Config?" + config);
        if(config){
            connected = waitFor(WAIT_TO_CONNECT_RESCUE, FUNC_IS_CONNECTED);
        }
        return connected;
    }

    private boolean checkWait(int func){
        switch (func){
            case FUNC_ENABLE_WIFI:
                return wifiAdmin.isWifiEnabled();
            case FUNC_IS_CONNECTED:
                return wifiAdmin.isWifiConnect();
            default:
                return false;
        }
    }

    private boolean waitFor(int timeout, int func){
        String Tag = TAG + "Wait";
        try {
            int idx = 1;
            while (idx <= timeout && !checkWait(func)) {
                Thread.sleep(idx * MILI_SECONDS_UNIT);
                idx++;
            }
        }catch (InterruptedException ex){
            Log.d(Tag, "Wait interrepted");
            ex.printStackTrace();
        }
        return checkWait(func);
    }

}
