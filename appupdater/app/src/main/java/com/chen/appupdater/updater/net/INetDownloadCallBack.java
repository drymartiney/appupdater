package com.chen.appupdater.updater.net;

import java.io.File;

/**
 * Created by yy on 2019/10/15.
 */

public interface INetDownloadCallBack {

    void success(File apkFile);

    void progress(int progress);

    void failed(Throwable throwable);
}
