package com.example.yonghaohu.sniff.FirstActivity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Created by yonghaohu on 15/10/18.
 */
public class PackagesInfo {
    private List<ApplicationInfo> appList;

    public PackagesInfo(Context context){
        //通包管理器，检索所有的应用程序（甚至卸载的）与数据目录
        PackageManager pm = context.getApplicationContext().getPackageManager();
        appList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
    }


    /**
     * 通过一个程序名返回该程序的一个Application对象。
     * @param name	程序名
     * @return	ApplicationInfo
     */

    public ApplicationInfo getInfo(String name){
        if(name == null)
            return null;
        for(ApplicationInfo appinfo : appList){
            if(name.equals(appinfo.processName))
                return appinfo;
        }
        return null;
    }
}

