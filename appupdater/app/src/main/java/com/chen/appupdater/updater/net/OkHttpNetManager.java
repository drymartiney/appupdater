package com.chen.appupdater.updater.net;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yy on 2019/10/15.
 */

public class OkHttpNetManager implements INetManager {
    private static OkHttpClient sOkhttpClient;

    private static Handler sHandler=new Handler(Looper.getMainLooper());
    static{
        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS);
        sOkhttpClient=builder.build();

        //http
        //https自签名的，Okhttp握手的错误
        //builder.sslSocketFactory()
    }
    @Override
    public void get(String url, final INetCallBack callBack,Object tag) {

        //requestbuilder ->Request ->Call->ex
        Request.Builder builder=new Request.Builder();
        Request request=builder.url(url).get().tag(tag).build();
        Call call=sOkhttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call,final IOException e) {
                //非Ui线程
                sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failed(e);
                    }
                });
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                try {
                    final String string=response.body().string();
                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.success(string);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                    callBack.failed(e);
                }
            }
        });
    }

    @Override
    public void download(String url, final File targerFile, final INetDownloadCallBack callBack,Object tag) {
        if (!targerFile.exists()){
            targerFile.getParentFile().mkdir();
        }

        Request.Builder builder=new Request.Builder();
        Request request=builder.url(url).tag(tag).build();
        Call call=sOkhttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failed(e);
                    }
                });
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                InputStream is=null;
                OutputStream os=null;
                try {
                    final long totalLen=response.body().contentLength();
                    is=response.body().byteStream();
                    os=new FileOutputStream(targerFile);
                    byte[] buffer=new byte[8*1024];
                    long curlen=0;
                    int bufferLen=0;

                    while (!call.isCanceled()&&(bufferLen=is.read(buffer))!=-1){
                        os.write(buffer,0,bufferLen);
                        os.flush();
                        curlen+=bufferLen;
                        final long finalCurLen=curlen;
                        sHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //callBack.
                                //Log.d("chen","finalCurLen:"+finalCurLen+"  totalLen:"+totalLen);
                                callBack.progress((int)((finalCurLen*1.0f/totalLen)*100));
                            }
                        });
                    }
                    if (call.isCanceled()){
                        return;
                    }
                    //调用可执行函数
                    try {
                        targerFile.setExecutable(true,false);
                        targerFile.setReadable(true,false);
                        targerFile.setWritable(true,false);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("success download File");
                            callBack.success(targerFile);
                        }
                    });
                }catch (final Exception e){
                    if (call.isCanceled()){
                        return;
                    }
                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.failed(e);
                        }
                    });
                }finally {
                    if (is!=null){
                        is.close();
                    }
                    if (os!=null){
                        os.close();
                    }
                }
            }
        });
    }

    @Override
    public void cancel(Object tag) {
        List<Call> queuedCalls=sOkhttpClient.dispatcher().queuedCalls();
        if (queuedCalls!=null){
            for (Call call:queuedCalls){
                if (tag.equals(call.request().tag())){
                    Log.d("hyman","find call = "+tag);
                    call.cancel();
                }
            }
        }
        List<Call> runningCalls=sOkhttpClient.dispatcher().runningCalls();
        if (runningCalls!=null){
            for (Call call:runningCalls){
                if (tag.equals(call.request().tag())){
                    call.cancel();
                }
            }
        }
    }
}
