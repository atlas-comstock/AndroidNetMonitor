package com.example.yonghaohu.sniff.RootTools;

import java.util.ArrayList;
import java.util.Set;

//no modifier, this is package-private which means that no one but the library can access it.
//If we need public variables just create the class for it.
class InternalVariables {

    //----------------------
    //# Internal Variables #
    //----------------------
	
    //Version numbers should be maintained here.
    protected static String TAG = "RootTools v0.9";
    protected static boolean accessGiven = false;
    protected static String[] space;
    protected static String getSpaceFor;
    protected static Set<String> path;
    protected static ArrayList<Mount> mounts;

}
