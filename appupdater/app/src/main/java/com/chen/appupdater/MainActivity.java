package com.chen.appupdater;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chen.appupdater.updater.AppUpdater;
import com.chen.appupdater.updater.bean.DownloadBean;
import com.chen.appupdater.updater.net.INetCallBack;
import com.chen.appupdater.updater.ui.UpdateVersionShowDialog;
import com.chen.appupdater.updater.utils.AppUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button mBtnUpdater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnUpdater=(Button)findViewById(R.id.id_btn_updater);
        mBtnUpdater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUpdater.getsInstance().getmNetManager().get("http://59.110.162.30/app_updater_version.json", new INetCallBack() {
                    @Override
                    public void success(String response) {
                        Log.d("hyman","response = "+response);
                        //1.解析json
                        //2.做版本匹配
                        //如果需要更新
                        //3.弹框
                        //4.点击下载
                        DownloadBean bean=DownloadBean.parse(response);

                        if (bean==null){
                            Toast.makeText(MainActivity.this,"版本接口返回数据异常",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //检测，是否需要弹窗
                        try {
                            long versionCode=Long.parseLong(bean.versionCode);
                            if (versionCode<= AppUtils.getVersionCode(MainActivity.this)){
                                Toast.makeText(MainActivity.this,"已经是最新版本，无需更新",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this,"版本接口返回数据异常",Toast.LENGTH_SHORT).show();
                        }
                        //弹窗
                        UpdateVersionShowDialog.show(MainActivity.this,bean);



                    }

                    @Override
                    public void failed(Throwable throwable) {
                        Toast.makeText(MainActivity.this,"版本更新接口请求失败",Toast.LENGTH_SHORT).show();
                    }
                },MainActivity.this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUpdater.getsInstance().getmNetManager().cancel(this);
    }
}
