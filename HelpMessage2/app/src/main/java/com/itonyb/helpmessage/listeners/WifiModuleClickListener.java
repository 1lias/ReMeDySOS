package com.itonyb.helpmessage.listeners;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.itonyb.helpmessage.R;
import com.itonyb.helpmessage.activities.MainActivity;
import com.itonyb.helpmessage.fragments.MainFragment;
import com.itonyb.helpmessage.model.WifiModule;

/**
 * Created by HenriqueJunger on 19/06/17.
 * Function used to create the Click event on the holder
 */

public class WifiModuleClickListener implements View.OnClickListener {
    private WifiModule module;


    public WifiModuleClickListener(WifiModule wifiModule){
        this.module = wifiModule;
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        MainActivity activity = (MainActivity) context;
        FragmentManager fm = activity.getSupportFragmentManager();
        MainFragment fragment = MainFragment.newInstance(context);
        fragment.setWifiModule(module);
        fm.beginTransaction().replace(R.id.container_main, fragment).addToBackStack(null).commit();
        fragment.startThread();
    }
}
