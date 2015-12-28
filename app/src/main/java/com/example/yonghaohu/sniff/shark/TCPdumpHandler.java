/**
 * This file is part of Shark.
 *
 * Shark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shark.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author Sergio Jim閚ez Feij髈 (sergio.jf89@gmail.com)
 */


package com.example.yonghaohu.sniff.shark;

import com.example.yonghaohu.sniff.R;
import com.example.yonghaohu.sniff.SecondActivity.SecondActivity;
import com.example.yonghaohu.sniff.SecondActivity.SecondActivity.*;
import com.example.yonghaohu.sniff.shark.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Allows an Android app to interact with the standard output of a TCPdump
 * process and create a notification that warns about TCPdump is running.
 */
public class TCPdumpHandler {

    // Constants definition.
    private static final int defaultRefreshRate = 100;
    private static final int defaultBufferSize = 4096;

    // Your Main activity's ids for the View.
//	private static final int paramsId = R.id.params_text;
//	private static final int outputId = R.id.output_text;
//	private static final int scrollerId = R.id.scroller;
//	private static final int pbarId = R.id.running_progressbar;

    // TextView's refresh rate in ms.
    private int refreshRate = defaultRefreshRate;

    // Byte[] buffer's size.
    private int bufferSize = defaultBufferSize;

    private boolean notificationEnabled = false;
    private boolean refreshingActive = false;

    private TCPdump tcpdump = null;

    private Handler isHandler = null;

    private Context mContext = null;
    private SharedPreferences settings = null;
    private NotificationManager nManager = null;
    private Notification notification = null;

    private TextView outputText = null;
    private View scroller = null;
    private ProgressBar pbar = null;
    private EditText params = null;

    /**
     * This runnable is used for refreshing the TCPdump's process standard
     * output.
     */
    public String GetOutput() {
        try {
            if ((tcpdump.getInputStream().available() > 0) == true) {
                byte[] buffer = new byte[bufferSize];

                try {
                    tcpdump.getInputStream().read(buffer, 0, bufferSize);
                } catch (IOException e) {
                    stopRefreshing();
                    return "Nope";
                }

                // Clears the screen if it's full.
                if (outputText.length() + buffer.length >= bufferSize)
                    return buffer.toString();

                //outputText.append(new String(buffer));

                // Forces the scrollbar to be at the bottom.
                return buffer.toString();
            }
        } catch (IOException e) {
            stopRefreshing();
            return "Nope";
        }
        return "Nothing";
//            isHandler.postDelayed(updateOutputText, refreshRate);
    }

    public TCPdumpHandler(TCPdump tcpdump, Context mContext,
                          boolean notificationEnabled) {

        // Acessing the app's settings.
        //settings = mContext.getSharedPreferences(GlobalConstants.prefsName, -1);

        this.tcpdump = tcpdump;
        isHandler = new Handler();

        //this.params = (EditText) activity.findViewById(paramsId);
        //this.outputText = (TextView) activity.findViewById(outputId);
        //this.scroller = (View) activity.findViewById(scrollerId);
        //this.pbar = (ProgressBar) activity.findViewById(pbarId);

        this.mContext = mContext;
        this.notificationEnabled = notificationEnabled;

        if (notificationEnabled) {
            // Asociating the System's notification service with the
            // notification manager.
            nManager = (NotificationManager) mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            // Defining a notification that will be displayed when TCPdump
            // starts.
            notification = new Notification(R.drawable.ic_info_black_24dp,
                    mContext.getString(R.string.tcpdump_notification),
                    System.currentTimeMillis());
//            notification.setLatestEventInfo(mContext, "Shark", mContext
//                            .getString(R.string.tcpdump_notification_msg),
//                    PendingIntent.getActivity(mContext, 0, new Intent(mContext,
//                                    Main.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP),
//                            PendingIntent.FLAG_CANCEL_CURRENT));
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
        }
    }

    /**
     * Starts a TCPdump process, enables refreshing and posts a notification.
     *
     * @param params
     *            The parameters that TCPdump will use. For example: -i
     *            [interface name] -s [snaplen size] -w [filename]
     *
     * @return 0 Everything went OK.<br>
     *         -1 TCPdump is already running.<br>
     *         -2 The device isn't rooted.<br>
     *         -4 Error when running the TCPdump command.<br>
     *         -5 Error when flushing the DataOutputStream.
     */
    public int start(String params) {
        int TCPdumpReturn;
        if ((TCPdumpReturn = tcpdump.start(params)) == 0) {
            //if (settings.getBoolean("saveCheckbox", false) == true) {
            //    outputText.setText(mContext
            //            .getString(R.string.standard_output_disabled)
            //            + GlobalConstants.dirName
            //            + "/"
            //            + settings.getString("fileText", "shark_capture.pcap"));
            //} else {
              //  outputText.setText(mContext
              //          .getString(R.string.standard_output_enabled));
//                startRefreshing();

            Toast.makeText(mContext, ("pcptmp is "+ this.GetOutput()),
                    Toast.LENGTH_SHORT).show();

            SecondActivity secondActivity = new SecondActivity();

            if(secondActivity.RootCmd("/data/data/com.example.yonghaohu.sniff/files/tcpdump > /data/data/com.example.yonghaohu.sniff/files/tcpdump_res") == "false") {
                Toast.makeText(mContext, ("RootCmd false"),
                        Toast.LENGTH_SHORT).show();
            }else{
                //;
//                String returnstring = new String();
//                returnstring = secondActivity.RootCmd("cat /data/data/com.example.yonghaohu.sniff/files/tcpdump_res");
//                if(returnstring == "false") {
//                    Toast.makeText(mContext, ("Cat false"),
//                            Toast.LENGTH_SHORT).show();
//                }else {
//                    ;
//                }

            }
//            AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
//            builder1.setMessage("pcptmp is "+ this.GetOutput());
//            builder1.show();
            //}
            //setProgressbarVisible();
            //if (notificationEnabled)
            //    postNotification();
            return 0;
        } else
            return TCPdumpReturn;
    }

    /**
     * Stops the TCPdump process, disables refreshing and removes the
     * notification.
     *
     *
     * @return 0: Everything went OK.<br>
     *         -1: TCPdump wasn't running.<br>
     *         -2: The device isn't rooted.<br>
     *         -4: Error when running the killall command.<br>
     *         -5: Error when flushing the output stream.<br>
     *         -6: Error when closing the shell.<br>
     *         -7: Error when waiting for the process to finish.
     */
    public int stop() {
        int TCPdumpReturn;
        if ((TCPdumpReturn = tcpdump.stop()) == 0) {
//            stopRefreshing();
//            setProgressbarInvisible();
//            if (notificationEnabled)
//                removeNotification();
            return 0;
        } else
            return TCPdumpReturn;
    }

    /**
     * Starts refreshing the TextView.
     */
//    private void startRefreshing() {
//        if (!refreshingActive) {
//            isHandler.post(updateOutputText);
//            refreshingActive = true;
//        }
//    }

    /**
     * Stops refreshing the TextView.
     */
    private void stopRefreshing() {
//        if (refreshingActive) {
//            isHandler.removeCallbacks(updateOutputText);
//            refreshingActive = false;

    }

    private void postNotification() {
        nManager.notify(0, notification);
    }

    private void removeNotification() {
        nManager.cancel(0);
    }

    private void setProgressbarVisible() {
        pbar.setVisibility(ProgressBar.VISIBLE);
    }

    private void setProgressbarInvisible() {
        pbar.setVisibility(ProgressBar.INVISIBLE);
    }

    /**
     * Sets the refreshRate value. refreshRate must be > 0.
     *
     * @param refreshRate
     *            The TextView's refresh rate in ms.
     * @return true if the new value has been set.<br>
     *         false if refreshRate hasn't been modified.
     */
    public boolean setRefreshRate(int refreshRate) {
        if ((refreshRate > 0) && (tcpdump.getProcessStatus() == false)) {
            this.refreshRate = refreshRate;
            return true;
        } else
            return false;
    }

    /**
     * Sets the bufferSize value. bufferSize must be > 0.
     *
     * @param bufferSize
     *            The bufferSize must be > 0.
     * @return true if the new value has been set.<br>
     *         false if bufferSize hasn't been modified.
     */
    public boolean setBufferSize(int bufferSize) {
        if ((bufferSize > 0) && (tcpdump.getProcessStatus() == false)) {
            this.bufferSize = bufferSize;
            return true;
        } else
            return false;
    }

    /**
     * Checks if the device's interface that will be used for capturing is up.
     *
     * @return true if the selected interface is up.<br>
     *         false if the selected interface is down.
     */
    public boolean checkNetworkStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        }else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.d("Network",
                                    "NETWORKNAME: " + anInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
        }
        Toast.makeText(mContext, "please_connect_to_internet", Toast.LENGTH_SHORT).show();
        return false;
    }


//        // Variables used for checking the network state.
//        final ConnectivityManager connMgr = (ConnectivityManager) mContext
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        final NetworkInfo wifi = connMgr
//                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//        final NetworkInfo mobile = connMgr
//                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//
//        if ((wifi.isConnected() == true) || (mobile.isConnected() == true)) {
//            return true;
//        } else
//            return false;

    /**
     * Generates the parameters that TCPdump will use by reading the options and
     * copies it in the parameters EditText.
     *
     * @return A string with the parameters.
     */
    public void generateCommand() {
        // Defining a String which will contain the command.
        String command = new String();

        // Recognizing the chosen interface.
        command = command
                + "-i "
                + TCPdumpInterface.listInterfaces(true)
                .get(settings.getInt("selectedInterface", 0))
                .getIfname();

        // Recognizing the promiscuous mode.
        if (settings.getBoolean("promiscCheckbox", false) == false) {
            command = command + " -p";
        }

        // Recognizing the verbose level.
        if (settings.getBoolean("verboseCheckbox", false) == true) {
            switch (settings.getInt("verboseLevel", 0)) {
                case 0: {
                    command = command + " -v";
                    break;
                }
                case 1: {
                    command = command + " -vv";
                    break;
                }
                case 2: {
                    command = command + " -vvv";
                    break;
                }
            }
        }

        // Recognizing the snaplen size.
        if (settings.getBoolean("snaplenCheckbox", false) == true) {
            command = command + " -s "
                    + Integer.toString(settings.getInt("snaplenValue", 0));
        }

        // Recognising the output file.
        if (settings.getBoolean("saveCheckbox", false) == true) {
            // If the directory in the sdcard isn't created we are going to
            // create it now.
            FileManager.checkDirectory(GlobalConstants.dirName);

            command = command + " -w /mnt/sdcard/" + GlobalConstants.dirName
                    + "/"
                    + settings.getString("fileText", "shark_capture.pcap");
        }
        params.setText(command);
    }

}
