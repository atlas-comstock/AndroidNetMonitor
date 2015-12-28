package com.example.yonghaohu.sniff.RootTools;

import android.content.Context;
import android.util.Log;

import java.io.*;

class Installer {

    //-------------
    //# Installer #
    //-------------

    private static final String LOG_TAG = "RootTools::Installer";

    private static final String BOGUS_FILE_NAME = "bogus";

    private Context context;
    private String  filesPath;

    public Installer(Context context)
            throws IOException {

        this.context   = context;
        this.filesPath = context.getFilesDir().getCanonicalPath();
    }

    /**
     * This method can be used to unpack a binary from the raw resources folder and store it in
     * /data/data/app.package/files/
     * This is typically useful if you provide your own C- or C++-based binary.
     * This binary can then be executed using sendShell() and its full path.
     *
     * @param sourceId  resource id; typically <code>R.raw.id</code>
     *
     * @param destName  destination file name; appended to /data/data/app.package/files/
     *
     * @param mode      chmod value for this file
     *
     * @return          a <code>boolean</code> which indicates whether or not we were
     *                  able to create the new file.
     */
    protected boolean installBinary(int sourceId, String destName, String mode) {
        File mf = new File(filesPath + File.separator + destName);
        if(!mf.exists()) {
            // First, does our files/ directory even exist?
            // We cannot wait for android to lazily create it as we will soon
            // need it.
            try {
                FileInputStream fis = context.openFileInput(BOGUS_FILE_NAME);
                fis.close();
            } catch (FileNotFoundException e) {
                FileOutputStream fos = null;
                try {
                    fos = context.openFileOutput("bogus", Context.MODE_PRIVATE);
                    fos.write("justcreatedfilesdirectory".getBytes());
                } catch (Exception ex) {
                    Log.e(LOG_TAG, ex.toString());
                    return false;
                }
                finally {
                    if(null != fos) {
                        try {
                            fos.close();
                            context.deleteFile(BOGUS_FILE_NAME);
                        } catch (IOException e1) {}
                    }
                }
            }
            catch(IOException ex) {
                Log.e(LOG_TAG, ex.toString());
                return false;
            }

            // Only now can we start creating our actual file
            InputStream iss = context.getResources().openRawResource(sourceId);
            FileOutputStream oss = null;
            try {
                oss = new FileOutputStream(mf);
                byte [] buffer = new byte[4096];
                int len;
                try {
                    while(-1 != (len = iss.read(buffer))) {
                        oss.write(buffer, 0, len);
                    }
                } catch (IOException ex) {
                    Log.e(LOG_TAG, ex.toString());
                    return false;
                }
            } catch (FileNotFoundException ex) {
                Log.e(LOG_TAG, ex.toString());
                return false;
            }
            finally {
                if(oss != null) {
                    try {
                        oss.close();
                    } catch (IOException e) {}
                }
            }
            try {
                iss.close();
            } catch (IOException ex) {
                Log.e(LOG_TAG, ex.toString());
                return false;
            }

            InternalMethods.instance().doExec(new String[] { "chmod " + mode + " " + filesPath + File.separator + destName });
        }
        return true;
    }
}
