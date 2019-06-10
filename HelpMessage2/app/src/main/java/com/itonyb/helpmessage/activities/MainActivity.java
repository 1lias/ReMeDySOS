package com.itonyb.helpmessage.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.itonyb.helpmessage.R;
import com.itonyb.helpmessage.fragments.MainFragment;

//Provides app with access to standard operating system services including messages, system services and inter-process communication.
//import resources to allow button/text views and toast notifications
//This imports allows the representation of Internet Ptotocol addresses
//This class represents a Network Interface made up of a name, and a list of IP addresses assigned to this interface.
// It is used to identify the local interface on which a device is joined.
//
//Used to generate a series of elements. Successive calls to the nextElement method return successive elements of the series.

/**
 * MAIN ACTIVITY
 * Main activity is the main screen, it has the title of the App and is a container
 * for the other screens
 */
public class MainActivity extends RuntimePermissionsActivity {
    private static final int REQUEST_PERMISSION = 10;


    /**
     * Method called on the creation of the instance
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Call the permission requests
        requestAppPermissions(new String[]{
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.READ_PHONE_STATE,// Needed to access the device's phone number
                        Manifest.permission.ACCESS_COARSE_LOCATION,// As of Android N, get scanresults() which return all
                        Manifest.permission.ACCESS_FINE_LOCATION},//nearby networks, requires Location permission
                R.string.txt_permissions_request,REQUEST_PERMISSION);
        // Gets the FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        // Get the MainFragment
        MainFragment fragment = (MainFragment) fm.findFragmentById(R.id.container_main);
        // If it is the first time, fragment is null
        if(fragment == null){
            fragment = MainFragment.newInstance(null);
            // Putting the fragment on the container
            fm.beginTransaction().add(R.id.container_main, fragment).commit();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        // Show Toast to the user when permission granted
       Toast.makeText(getApplicationContext(), "Permissions granted", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume(){
        // When Resumed
        super.onResume();
        // Set the MainFragment to show the main screen
        FragmentManager fm = getSupportFragmentManager();
        MainFragment fragment = (MainFragment)fm.findFragmentById(R.id
                .container_main);
        // Starts the connection to the Wi-Fi again
        fragment.setWifiModule(null);
        fragment.startThread();
        fm.beginTransaction().replace(R.id.container_main, fragment).addToBackStack(null).commit();
    }


}
