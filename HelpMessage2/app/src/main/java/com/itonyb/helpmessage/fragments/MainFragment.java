package com.itonyb.helpmessage.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.itonyb.helpmessage.R;
import com.itonyb.helpmessage.handlers.MainActivityHandler;
import com.itonyb.helpmessage.handlers.WiFlyMessageHandler;
import com.itonyb.helpmessage.model.WifiAdmin;
import com.itonyb.helpmessage.model.WifiModule;
import com.itonyb.helpmessage.threads.ConnectionThread;
import com.itonyb.helpmessage.threads.ReceiveMessageThread;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
/**
 * Main Fragment with the components of the first screen
 */
public class MainFragment extends Fragment {
    private RescueListFragment rescueListFragment;

    // TODO: Rename and change types of parameters
    private final String TAG = "MainActivity";

    private ConnectionThread connectionThread;
    private Context context;

    //GUI
    ProgressDialog progressDialog;
    Button btn_help, btn_exit, btn_retry, btn_list;
    TextView messageStatusView, ssidView, ipView, signalView;

    //Class that handles the wifi connection
    WifiAdmin mwifiAdmin;

    MainActivityHandler handlerMain;
    //Handles the messages returned from the RN-XV. It updates the textview with the response
    private Handler msgHandler;


    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(Context context) {
        MainFragment fragment = new MainFragment();
        if(context != null)
            fragment.setVariables(context);
        return fragment;
    }

    private void setVariables(Context context){
        // Initiate the variables Context and WifiAdmin
        this.context = context != null ? context : getContext();
        mwifiAdmin = new WifiAdmin(this.context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_main, container, false);
        ssidView = (TextView) ret.findViewById(R.id.txtSsidView);
        ipView = (TextView)ret.findViewById(R.id.txtIpView);
        signalView = (TextView)ret.findViewById(R.id.txtSignalView);
        messageStatusView =  (TextView)ret.findViewById(R.id.txtMessageStatusView);

        // Create listener for the buttons
        btn_help = (Button)ret.findViewById(R.id.btn_help);
        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnHelpClick();
            }
        });

        btn_exit = (Button)ret.findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnExitClick();
            }
        });

        btn_retry = (Button)ret.findViewById(R.id.btn_retry);
        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRetryClick();
            }
        });
        btn_list = (Button)ret.findViewById(R.id.btn_list);
        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnListClick();
            }
        });

        // Initiate the context and wifiAdmin
        if(context == null || mwifiAdmin == null)
            setVariables(getContext());
        // Start buttons Enable property
        // False for Help
        // True for Retry
        setButtonsConfig(false);

        // Starts Handlers to handler thread's responses
        handlerMain = new MainActivityHandler(context);
        msgHandler = new WiFlyMessageHandler(context);

        // Start the connection for the Wi-Fi
        startThread();

        return ret;
    }

    public void startThread(){
        // Start the thread to connect to the Wi-Fi
        String message = "Connecting to RescueNet...Please Wait";
        showProgressDialog(message);
        connectionThread = new ConnectionThread(mwifiAdmin, handlerMain);
        connectionThread.start();
    }

    public void setWifiModule(WifiModule wifiModule){
        // Set the Wi-Fi connected
        mwifiAdmin.setChoosenModule(wifiModule);
    }

    public void setButtonsConfig(boolean connected){
        // Enable or disable the buttons when connected
        btn_help.setEnabled(connected);
        btn_retry.setEnabled(!connected);
    }

    public void showProgressDialog(String message){
        // Show a Dialog with the received message
        if(progressDialog != null)
            progressDialog.dismiss();

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void dismissProgressDialog(){
        // Close a dialog, with there is one
        if(progressDialog != null)
            progressDialog.dismiss();
    }

    // Function tp handle Help button click
    public void btnHelpClick(){
        try {
            String Tag = TAG + "-BtnHelp";
            // Get number
            String number = getMyPhoneNO();
            ReceiveMessageThread receiveMessageThread;
            // Mount the message to be sent
            String msg = "*SOS*;Number:" + number + ";";
            Log.i(Tag, msg);
            // Show message to user
            showProgressDialog("Sending distress message...");
            // Disable Help Button
            btn_help.setEnabled(false);
            // Start thread to send message
            receiveMessageThread = new ReceiveMessageThread(msgHandler, msg);
            receiveMessageThread.start();
        }
        catch (Exception e){
            Log.i(TAG , "Caught this exception: " + e.getMessage());
        }
    }

    // Function to handle Retry button click
    public void btnRetryClick(){
        String Tag = TAG + "-BtnRetry";
        Log.d(Tag, "Retrying...");
        // Clear the chosen Wi-Fi and start connection thread again
        setWifiModule(null);
        startThread();
        //btn_retry.setVisibility(View.INVISIBLE);
    }

    // Function to handle List Wi-Fi click
    public void btnListClick(){
        // Scan the Wi-Fi to refresh list
        mwifiAdmin.scanForRescueNet();
        // Show the List screen
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if(rescueListFragment == null)
            rescueListFragment = RescueListFragment.newInstance(mwifiAdmin);
        fm.beginTransaction().replace(R.id.container_main, rescueListFragment).addToBackStack(null).commit();
    }

    // Function to handle Exit click
    public void btnExitClick(){
        // Finish the main activity
        onPause();
        this.getActivity().finish();
    }

    // Fill the Labels with the information for the connected Wi-Fi
    public void setTxtViews(String ssid, String ip, String signal){
        ssidView.setText(ssid);
        ipView.setText(ip);
        signalView.setText(signal);
    }

    // Includes message on the View to show the response from Communication Node
    public void setResponseView(String message){
        messageStatusView.append(message);
        messageStatusView.append("\n");
    }

    //Depending on the phone model, this function does not return the mobile number of the device
    //hence it is being hardcoded
    private String getMyPhoneNO() {
        String mPhoneNumber;
        TelephonyManager tMgr = (TelephonyManager) this.getActivity().getSystemService(Context
                .TELEPHONY_SERVICE);
        mPhoneNumber = tMgr.getLine1Number();
        if(mPhoneNumber == null)
            mPhoneNumber = "+NoNotFound";
        return mPhoneNumber;
    }



}
