package com.itonyb.helpmessage.handlers;

import android.content.Context;
import android.os.Handler;

import com.itonyb.helpmessage.R;
import com.itonyb.helpmessage.activities.MainActivity;
import com.itonyb.helpmessage.fragments.MainFragment;

/**
 * Created by HenriqueJunger on 04/08/17.
 * This class exists for functions used by all the Handlers
 */

public class MainFragmentHandler extends Handler {
    protected Context context;

    // Function to get the Fragment that is on the screen
    protected MainFragment getMainFragment(){
        MainActivity activity = (MainActivity)context;
        MainFragment fragment = (MainFragment) activity.getSupportFragmentManager().findFragmentById(R.id
                .container_main);
        return fragment;
    }
}
