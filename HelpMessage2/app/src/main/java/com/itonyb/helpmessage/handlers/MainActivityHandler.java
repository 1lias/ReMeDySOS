package com.itonyb.helpmessage.handlers;

import android.content.Context;
import android.os.Message;
import android.widget.Toast;

import com.itonyb.helpmessage.R;
import com.itonyb.helpmessage.fragments.MainFragment;
import com.itonyb.helpmessage.model.WifiModule;

/**
 * Created by HenriqueJunger on 14/06/17.
 *
 */

public class MainActivityHandler extends MainFragmentHandler {
    private WifiModule wifiConnected;

    public MainActivityHandler(Context context){
        this.context = context;
    }

    public void setWifiConnected(WifiModule wifiConnected){
        this.wifiConnected = wifiConnected;
    }

    // Function to Handle the return from the Connection Thread
    @Override
    public void handleMessage(Message msg) {
        // Get the Fragment that is being shown
        MainFragment fragment = getMainFragment();
        // Close Dialog and enable/disable buttons
        fragment.dismissProgressDialog();
        fragment.setButtonsConfig(msg.what == 0);
        // If connection was successfull
        if(wifiConnected != null) {
            // Set the Info on the Views
            fragment.setTxtViews(wifiConnected.getSsid(), wifiConnected.getIpAddress(),
                    wifiConnected.getSignalPercLabel());
        }else {
            // Else clean the Views
            fragment.setTxtViews("", "", "");
        }
        // Show the message to the user according to the result
        switch (msg.what) {
            case 0:
                Toast.makeText(context, R.string.txt_connectionSuccess, Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(context, R.string.txt_outOfRange, Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(context, R.string.txt_tryAgain, Toast.LENGTH_LONG).show();
            case 3:
                Toast.makeText(context, R.string.txt_couldNotEnableWifi, Toast.LENGTH_LONG).show();
            default:
                break;
        }
    }
}
