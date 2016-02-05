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
import com.example.yonghaohu.sniff.RootTools.*;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.provider.Settings;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SniffPackets extends Thread {

    // Variable declarations for handling the view items in the layout.
    //private Button start_button;
    //private Button stop_button;
    //private Button read_button;
    //private EditText parameters;

    // Variable declarations for handling the TCPdump process.
    private TCPdump tcpdump = null;
    private TCPdumpHandler tcpDumpHandler = null;
    private SharedPreferences settings = null;
    private Context mycontext = null;
    private int lenOfPacketCapture = 68;

    // Variable declarations for handling the options and reader activities.
    private Intent optionsIntent = null;
    private Intent readerIntent = null;

    public SniffPackets(Context context) {
        // Extracting the TCPdump binary to the app folder.
        if (RootTools.installBinary(context, R.raw.tcpdump, "tcpdump") == false) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.extraction_error)
                    .setMessage(R.string.extraction_error_msg)
                    .setNeutralButton(R.string.ok, null).show();
        }else {
            Log.d("INstallBinary TCPDump", "SUCCESS");

            // Creating a new TCPdump object.
            tcpdump = new TCPdump();
            // Creating a TCPdump handler for the TCPdump object created after.
            tcpDumpHandler = new TCPdumpHandler(tcpdump, context, true);

            // Obtaining the command from the options that were saved last time
            // Shark was running.
            //tcpDumpHandler.generateCommand();
        }
    }

    public void setcontext(Context context) {
        this.mycontext = context;
    }

    public void SetLenOfPacketCapture(int LenOfPacketCapture) {
        if(LenOfPacketCapture != 0)
            lenOfPacketCapture = LenOfPacketCapture;
    }

    @Override
    public void run() {
        startTCPdump();
    }



    //setContentView(R.layout.main);

    // Associating the items in the view to the variables.
    //start_button = (Button) findViewById(R.id.start_button);
    //stop_button = (Button) findViewById(R.id.stop_button);
        //read_button = (Button) findViewById(R.id.read_button);
        //parameters = (EditText) findViewById(R.id.params_text);

        // Accessing the app's preferences.
        //settings = getSharedPreferences(GlobalConstants.prefsName, 0);




//		start_button.setOnClickListener(new OnClickListener() {
//			// Setting the action to perform when the start button is pressed.
//			@Override
//			public void onClick(View v) {
//				startTCPdump();
//			}
//		});
//
//		stop_button.setOnClickListener(new OnClickListener() {
//			// Setting the action to perform when the stop button is pressed.
//			@Override
//			public void onClick(View v) {
//				stopTCPdump();
//			}
//		});
//
//		read_button.setOnClickListener(new OnClickListener() {
//			// Setting the action to perform when the open in reader button is
//			// pressed.
//			@Override
//			public void onClick(View v) {
//				launchReader();
//			}
//		});
//

    /**
     * Calls TCPdumpHandler to try start the packet capture.
     */
    public void startTCPdump() {
        if (true) {//tcpDumpHandler.checkNetworkStatus()

            switch (tcpDumpHandler.start("-i any -p -s " + lenOfPacketCapture)) {
                case 0:
                    Toast.makeText(mycontext, (R.string.tcpdump_started),
                            Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(mycontext,
                            mycontext.getString(R.string.tcpdump_already_started),
                            Toast.LENGTH_SHORT).show();
                    break;
                case -2:
                    new AlertDialog.Builder(mycontext)
                            .setTitle(mycontext.getString(R.string.device_not_rooted_error))
                            .setMessage(
                                    mycontext.getString(R.string.device_not_rooted_error_msg))
                            .setNeutralButton(mycontext.getString(R.string.ok), null).show();
                    break;
                case -4:
                    new AlertDialog.Builder(mycontext).setTitle("Error")
                            .setMessage(mycontext.getString(R.string.command_error))
                            .setNeutralButton(mycontext.getString(R.string.ok), null).show();
                    break;
                case -5:
                    new AlertDialog.Builder(mycontext).setTitle("Error")
                            .setMessage(mycontext.getString(R.string.outputstream_error))
                            .setNeutralButton(mycontext.getString(R.string.ok), null).show();
                    break;
                default:
                    new AlertDialog.Builder(mycontext).setTitle("Error")
                            .setMessage(mycontext.getString(R.string.unknown_error))
                            .setNeutralButton(mycontext.getString(R.string.ok), null).show();
            }
        } else {
            new AlertDialog.Builder(mycontext)
                    .setTitle(mycontext.getString(R.string.network_connection_error))
                    .setMessage(
                            mycontext.getString(R.string.network_connection_error_msg))
                    .setPositiveButton(mycontext.getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    mycontext.startActivity(new Intent(
                                            Settings.ACTION_WIRELESS_SETTINGS));
                                }
                            }).setNegativeButton(mycontext.getString(R.string.no), null)
                    .show();
        }
    }

    /**
     * Calls TCPdumpHandler to try to stop the packet capture.
     */
    public void stopTCPdump(Context context) {
        switch (tcpDumpHandler.stop()) {
            case 0:
                Toast.makeText(context, context.getString(R.string.tcpdump_stoped),
                        Toast.LENGTH_SHORT).show();
                break;
            case -1:
                Toast.makeText(context,
                        context.getString(R.string.tcpdump_already_stoped),
                        Toast.LENGTH_SHORT).show();
                break;
            case -2:
                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.device_not_rooted_error))
                        .setMessage(context.getString(R.string.device_not_rooted_error_msg))
                        .setNeutralButton(context.getString(R.string.ok), null).show();
                break;
            case -4:
                new AlertDialog.Builder(context).setTitle("Error")
                        .setMessage(context.getString(R.string.command_error))
                        .setNeutralButton(context.getString(R.string.ok), null).show();
                break;
            case -5:
                new AlertDialog.Builder(context).setTitle("Error")
                        .setMessage(context.getString(R.string.outputstream_error))
                        .setNeutralButton(context.getString(R.string.ok), null).show();
                break;
            case -6:
                new AlertDialog.Builder(context).setTitle("Error")
                        .setMessage(context.getString(R.string.close_shell_error))
                        .setNeutralButton(context.getString(R.string.ok), null).show();
                break;
            case -7:
                new AlertDialog.Builder(context).setTitle("Error")
                        .setMessage(context.getString(R.string.process_finish_error))
                        .setNeutralButton(context.getString(R.string.ok), null).show();
            default:
                new AlertDialog.Builder(context).setTitle("Error")
                        .setMessage(context.getString(R.string.unknown_error))
                        .setNeutralButton(context.getString(R.string.ok), null).show();
        }

    }

    /**
     * Tries to launch the reader activity.
     */
//	public void launchReader() {
//		readerIntent = new Intent(SniffPackets.this, Reader.class);
//		if (FileManager.checkFile(GlobalConstants.dirName,
//				settings.context.getString("fileText", "shark_capture.pcap"))) {
//			if (tcpdump.getProcessStatus() == false) {
//				startActivity(readerIntent);
//			} else {
//				new AlertDialog.Builder(SniffPackets.this)
//						.setTitle(context.getString(R.string.capture_in_progress_error))
//						.setMessage(
//								context.getString(R.string.capture_in_progress_error_msg))
//						.setPositiveButton(context.getString(R.string.yes),
//								new DialogInterface.OnClickListener() {
//									@Override
//									public void onClick(DialogInterface arg0,
//											int arg1) {
//										stopTCPdump();
//										startActivity(readerIntent);
//									}
//								})
//						.setNegativeButton(context.getString(R.string.no), null).show();
//			}
//		} else {
//			new AlertDialog.Builder(SniffPackets.this)
//					.setTitle(context.getString(R.string.file_error))
//					.setMessage(context.getString(R.string.file_error_msg))
//					.setNeutralButton(context.getString(R.string.ok), null).show();
//		}
//	}
}
