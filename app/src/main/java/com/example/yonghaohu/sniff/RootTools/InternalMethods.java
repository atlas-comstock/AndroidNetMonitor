package com.example.yonghaohu.sniff.RootTools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import android.util.Log;

//no modifier, this is package-private which means that no one but the library can access it.
class InternalMethods {
	
    //--------------------
    //# Internal methods #
    //--------------------

    static private InternalMethods instance_;

    static protected InternalMethods instance() {
        if(null == instance_) {
            instance_ = new InternalMethods();
        }
        return instance_;
    }

    private InternalMethods() {
        super();
    }

    protected void doExec(String[] commands) {
        Process process = null;
        DataOutputStream os = null;
        InputStreamReader osRes = null;

        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            osRes = new InputStreamReader(process.getInputStream());
            BufferedReader reader = new BufferedReader(osRes);

            // Doing Stuff ;)
            for (String single : commands) {
                os.writeBytes(single + "\n");
                os.flush();
            }


            os.writeBytes("exit \n");
            os.flush();

            String line = reader.readLine();

            while (line != null) {
                if (commands[0].equals("id")) {
                    Set<String> ID = new HashSet<String>(Arrays.asList(line.split(" ")));
                    for (String id : ID) {
                        if (id.toLowerCase().contains("uid=") && id.toLowerCase().contains("root")) {
                            InternalVariables.accessGiven = true;
                            Log.i(InternalVariables.TAG, "Access Given");
                            break;
                        }
                    }
                    if (!InternalVariables.accessGiven) {
                        Log.i(InternalVariables.TAG, "Access Denied?");
                    }
                }
				if (commands[0].equals("df") && line.contains(InternalVariables.getSpaceFor)) {
					InternalVariables.space = line.split(" ");
				}
                line = reader.readLine();
            }

            process.waitFor();

        } catch (Exception e) {
            Log.d(InternalVariables.TAG,
                    "Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (osRes != null) {
                    osRes.close();
                }
                process.destroy();
            } catch (Exception e) {
                Log.d(InternalVariables.TAG,
                        "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    protected boolean returnPath() {
        File tmpDir = new File("/data/local/tmp");
        if (!tmpDir.exists()) {
            doExec(new String[]{"mkdir /data/local/tmp"});
        }
        try {
	        InternalVariables.path = new HashSet<String>();
	        //Try to read from the file.
	        LineNumberReader lnr = null;
	        doExec(new String[]{"dd if=/init.rc of=/data/local/tmp/init.rc",
	                "chmod 0777 /data/local/tmp/init.rc"});
	        lnr = new LineNumberReader( new FileReader( "/data/local/tmp/init.rc" ) );
	        String line;
	        while( (line = lnr.readLine()) != null ){
	            if (line.contains("export PATH")) {
	                int tmp = line.indexOf("/");
	                InternalVariables.path = new HashSet<String>(Arrays.asList(line.substring(tmp).split(":")));
	                return true;
	            }
	        }
	        return false;
        } catch (Exception e) {
            Log.d(InternalVariables.TAG,
                    "Error: " + e.getMessage());
            e.printStackTrace();
        	return false;
        }
    }
    
    protected ArrayList<Mount> getMounts() throws FileNotFoundException, IOException {
        LineNumberReader lnr = null;
        try {
            lnr = new LineNumberReader( new FileReader( "/proc/mounts" ) );
            String line;
            ArrayList<Mount> mounts = new ArrayList<Mount>();
            while( (line = lnr.readLine()) != null ){
                String[] fields = line.split(" ");
                mounts.add( new Mount(
                        new File(fields[0]), // device
                        new File(fields[1]), // mountPoint
                        fields[2], // fstype
                        fields[3] // flags
                ) );
            }
            return mounts;
        }
        finally {
            //no need to do anything here.
        }
    }
}
