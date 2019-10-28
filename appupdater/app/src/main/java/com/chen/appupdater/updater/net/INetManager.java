package com.chen.appupdater.updater.net;

import java.io.File;

/**
 * Created by yy on 2019/10/15.
 */

public interface INetManager {

    void get(String url,INetCallBack callBack,Object tag);

    void download(String url, File targerFile, INetDownloadCallBack callBack,Object tag);

    void cancel(Object tag);
}
