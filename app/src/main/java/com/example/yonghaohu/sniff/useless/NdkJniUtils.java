package com.example.yonghaohu.sniff.useless;

/**
 * Created by yonghaohu on 15/10/20.
 */

public class NdkJniUtils {

    public native String getCLanguageString(String path);
    static {
        System.loadLibrary("hellojni");	//defaultConfig.ndk.moduleName
    }
}
