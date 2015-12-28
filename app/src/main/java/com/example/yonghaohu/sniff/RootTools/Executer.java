package com.example.yonghaohu.sniff.RootTools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class Executer {

    //------------
    //# Executer #
    //------------

    /**
     * Sends several shell command as su (attempts to)
     *
     * @param commands  array of commands to send to the shell
     *
     * @param sleepTime time to sleep between each command, delay.
     *
     * @param result    injected result object that implements the Result class
     *
     * @return          a <code>LinkedList</code> containing each line that was returned
     *                  by the shell after executing or while trying to execute the given commands.
     *                  You must iterate over this list, it does not allow random access,
     *                  so no specifying an index of an item you want,
     *                  not like you're going to know that anyways.
     *
     * @throws InterruptedException
     *
     * @throws IOException
     */
    public List<String> sendShell(String[] commands, int sleepTime, IResult result)
            throws IOException, InterruptedException, RootToolsException {
        Log.i(InternalVariables.TAG, "Sending " + commands.length + " shell command" + (commands.length>1?"s":""));
        List<String> response = null;
        if(null == result) {
            response = new LinkedList<String>();
        }

        Process process = null;
        DataOutputStream os = null;
        InputStreamReader osRes = null;

        try {
            process = Runtime.getRuntime().exec("su");
            if(null != result) {
                result.setProcess(process);
            }
            os = new DataOutputStream(process.getOutputStream());
            osRes = new InputStreamReader(process.getInputStream());
            BufferedReader reader = new BufferedReader(osRes);
            // Doing Stuff ;)
            for (String single : commands) {
                os.writeBytes(single + "\n");
                os.flush();
                Thread.sleep(sleepTime);
            }

            os.writeBytes("exit \n");
            os.flush();

            String line = reader.readLine();

            while (line != null) {
                if(null == result) {
                    response.add(line);
                } else {
                    result.process(line);
                }
                line = reader.readLine();
            }
        }
        catch (Exception ex) {
            if(null != result) {
                result.onFailure(ex);
            }
        }
        finally {
            int diag = process.waitFor();
            if(null != result) {
                result.onComplete(diag);
            }

            try {
                if (os != null) {
                    os.close();
                }
                if (osRes != null) {
                    osRes.close();
                }
                process.destroy();
            } catch (Exception e) {
                //return what we have
                return response;
            }
            return response;
        }
    }
}
