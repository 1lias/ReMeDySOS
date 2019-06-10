package com.itonyb.helpmessage.threads;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.itonyb.helpmessage.activities.MainActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReceiveMessageThread extends Thread {
    // reference to mainHandler from the mainThread
    Handler parentHandler;
    // local Handler manages messages for MyThread
    // received from mainThread

    String smstext,response;

    final String gatewayIp = "10.0.0.1";
    final int socketPort = 2000;
    final String TAG = "ReceiveThread";

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK);

    public ReceiveMessageThread(Handler msgHandler, String smstext) {
        this.parentHandler=msgHandler;
        this.smstext=smstext;
    }

    public void run() {
        Socket socket = null;
        OutputStream sos = null;
        DataOutputStream dos = null;
        InputStream sis = null;
        BufferedReader mInputStream = null;

        try {
            socket = new Socket(gatewayIp, socketPort);
            socket.setKeepAlive(true);
            sos = socket.getOutputStream();
            dos = new DataOutputStream(sos);
            Date curDate = new Date(System.currentTimeMillis());
            String strDate = formatter.format(curDate);
            String msg = smstext + "Time:" +strDate + ";";
            Log.e("APid",msg);
            dos.write(msg.getBytes());
            dos.write('\n');

            //********************************
            sis = socket.getInputStream();
            Log.d(TAG, "Available: " + sis.available());
            mInputStream = new BufferedReader(new InputStreamReader(sis));
            Log.d(TAG, "mInputStream is ready: " + mInputStream.ready());

            String msgResponse = mInputStream.readLine();
            //Wifly module sends *HELLO* in its response to a successful connection
            if (msgResponse.contains("*HELLO*")) {
                Thread.sleep(7000);
                response = "Message received at " + strDate;
            } else {
                response = "Message failed at " + strDate;
            }
        } catch (IOException ex) {
            Log.e("EXCEPTION: ", ex.getMessage());
            response = "Message failed: IO Error, please try again.";
            ex.printStackTrace();
        }catch (Exception ex){
            response = "Message failed: Some error occurred.";
            ex.printStackTrace();
        } finally {
            try {
                if(mInputStream != null)
                    mInputStream.close();
                if(sis != null)
                    sis.close();
                if(dos != null)
                    dos.close();
                if(sos != null)
                    sos.close();
                if(socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        parentHandler.obtainMessage(1,response).sendToTarget();
    }
}
