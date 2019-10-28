package com.chen.appupdater.updater.net;

/**
 * Created by yy on 2019/10/15.
 */

public interface INetCallBack {
    void success(String response);

    void failed(Throwable throwable);
}
