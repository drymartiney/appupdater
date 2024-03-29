package com.chen.appupdater.updater.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;

import com.chen.appupdater.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yy on 2019/10/18.
 */

public class AppUtils {
    public static long getVersionCode(Context context) {
        PackageManager packageManager=context.getPackageManager();

        try {
            PackageInfo packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void installApk(Activity activity, File apkFile) {
        Intent intent=new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri=Uri.fromFile(apkFile);
        intent.setDataAndType(uri,"application/vnd.android.package-archive");
        activity.startActivity(intent);
        //TODO N FILEPROVIDER
    }

    public static String getFileMd5(File targetFile) {
        if (targetFile==null||!targetFile.isFile()){
            return null;
        }
        MessageDigest digest=null;
        FileInputStream in=null;
        byte[] buffer=new byte[1024];
        int len=0;
        try {
            digest=MessageDigest.getInstance("MD5");
            in=new FileInputStream(targetFile);
            while ((len=in.read(buffer))!=-1){
                digest.update(buffer,0,len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            if (in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        byte[] result=digest.digest();
        BigInteger bigInt=new BigInteger(1,result);
        return bigInt.toString(16);
    }
}
