package com.chen.appupdater.updater.ui;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.appupdater.MainActivity;
import com.chen.appupdater.R;
import com.chen.appupdater.updater.AppUpdater;
import com.chen.appupdater.updater.bean.DownloadBean;
import com.chen.appupdater.updater.net.INetDownloadCallBack;
import com.chen.appupdater.updater.utils.AppUtils;

import java.io.File;

/**
 * Created by yy on 2019/10/18.
 */

public class UpdateVersionShowDialog extends DialogFragment {

    private static final String KEY_DOWNLOAD_BEAN="download_bean";

    private DownloadBean mDownloadBean;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments=getArguments();
        if (arguments!=null){
            mDownloadBean= (DownloadBean) arguments.getSerializable(KEY_DOWNLOAD_BEAN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dialog_updater,container,false);
        bindEvents(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(336699));

    }

    private void bindEvents(View view) {
        TextView tvTitle= (TextView) view.findViewById(R.id.tv_title);
        TextView tvContent= (TextView) view.findViewById(R.id.tv_content);
        final TextView tvUpdater= (TextView) view.findViewById(R.id.tv_update);

        tvTitle.setText(mDownloadBean.title);
        tvContent.setText(mDownloadBean.content);
        tvUpdater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                final File targetFile=new File(getActivity().getCacheDir(),"target.apk");
                AppUpdater.getsInstance().getmNetManager().download(mDownloadBean.url,targetFile, new INetDownloadCallBack() {

                    @Override
                    public void success(File apkFile) {
                        //安装的代码
                        v.setEnabled(true);
                        Log.d("hyman","success = "+apkFile.getAbsolutePath());
                        dismiss();

                        String fileMd5=AppUtils.getFileMd5(targetFile);
                        Log.d("hyman","md5 = ="+fileMd5);
                        if (fileMd5 != null && fileMd5.equals(mDownloadBean.md5)) {
                            AppUtils.installApk(getActivity(), apkFile);
                        }else{
                            Toast.makeText(getActivity(),"md5检测失败",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void progress(int progress) {
                        Log.d("hyman","progress = "+progress);
                        tvUpdater.setText(progress+"%");
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        v.setEnabled(true);
                        Toast.makeText(getActivity(),"文件下载失败",Toast.LENGTH_SHORT).show();
                    }
                },UpdateVersionShowDialog.this);
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d("hyman","onDismiss");
        AppUpdater.getsInstance().getmNetManager().cancel(this);
    }

    public static void show(FragmentActivity activity, DownloadBean bean){
        Bundle bundle=new Bundle();
        bundle.putSerializable(KEY_DOWNLOAD_BEAN,bean);
        UpdateVersionShowDialog dialog=new UpdateVersionShowDialog();
        dialog.setArguments(bundle);
        dialog.show(activity.getSupportFragmentManager(),"updateVersionShowDialog");
    }



}
