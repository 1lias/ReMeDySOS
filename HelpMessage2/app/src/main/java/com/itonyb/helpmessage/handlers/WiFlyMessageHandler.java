package com.itonyb.helpmessage.handlers;

import android.content.Context;
import android.os.Message;

import com.itonyb.helpmessage.fragments.MainFragment;


/**
 * Created by HenriqueJunger on 14/06/17.
 *
 */

public class WiFlyMessageHandler extends MainFragmentHandler {

    public WiFlyMessageHandler(Context context){
        this.context= context;
    }

    @Override
    public void handleMessage(Message msg) {
        // Get the fragment
        MainFragment fragment = getMainFragment();
        // Close the Dialogs, set the enable property of the buttons and the
        // message received from the Communication Node
        fragment.dismissProgressDialog();
        fragment.setButtonsConfig(true);
        fragment.setResponseView(msg.obj.toString());
    }
}
