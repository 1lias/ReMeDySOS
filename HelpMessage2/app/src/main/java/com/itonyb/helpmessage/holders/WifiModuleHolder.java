package com.itonyb.helpmessage.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.itonyb.helpmessage.R;
import com.itonyb.helpmessage.listeners.WifiModuleClickListener;
import com.itonyb.helpmessage.model.WifiModule;

/**
 * Created by HenriqueJunger on 19/06/17.
 * Class used to encapsulate the items on the RecyclerView
 */

public class WifiModuleHolder extends RecyclerView.ViewHolder {
    private TextView ssidView;
    private TextView signalView;

    public WifiModuleHolder(View itemView) {
        super(itemView);
        // Initiate variables
        ssidView = (TextView) itemView.findViewById(R.id.ssidTxtCard);
        signalView = (TextView) itemView.findViewById(R.id.signalTxtCard);
    }

    public void bind(WifiModule module) {
        // Make the bind for the click event on each item of the list
        WifiModuleClickListener listener = new WifiModuleClickListener(module);
        ssidView.setText(module.getSsid());
        signalView.setText(module.getSignalPercLabel());
        itemView.setOnClickListener(listener);
    }

}
