package com.chen.appupdater.updater;

import com.chen.appupdater.updater.net.INetManager;
import com.chen.appupdater.updater.net.OkHttpNetManager;

/**
 * Created by yy on 2019/10/15.
 */

public class AppUpdater {

    private static AppUpdater sInstance=new AppUpdater();

    //网络请求下载的能力
    //okhttp,volley,httpclient,httpurlconn
    private INetManager mNetManager=new OkHttpNetManager();

//    public void setmNetManager(INetManager manager){
//        mNetManager=manager;
//    }
    public INetManager getmNetManager(){
        return mNetManager;
    }
    public static AppUpdater getsInstance(){
        return sInstance;
    }
}
