package com.zlfcapp.poster.mvp.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.zlfcapp.poster.R;

import java.util.ArrayList;
import java.util.List;


/**
 * @Description: 授权提示
 * @Author: lixh
 * @CreateDate: 2020/4/3 15:50
 * @Version: 1.0
 */
public class GrantPerissionDialog extends Dialog {

    Button btn_grant;
    Context mContenxt;
    Activity mActivity;
    List<String> permissionList = null;

    public GrantPerissionDialog(@NonNull Context context, Activity activity, List<String> list) {
        super(context);
        permissionList = list;
        mContenxt = context;
        mActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_grant);
        List<String> lackedPermission = new ArrayList<String>();
        btn_grant = findViewById(R.id.btn_grant);
        // 去授权
        btn_grant.setOnClickListener(v -> {
            dismiss();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionList != null && permissionList.size() != 0) {
                    for (String singlePermission : permissionList) {
                        if (!(mContenxt.checkSelfPermission(singlePermission) == PackageManager.PERMISSION_GRANTED)) {
                            lackedPermission.add(singlePermission);
                        }
                    }
                }
                String[] requestPermissions = new String[lackedPermission.size()];
                String[] array = lackedPermission.toArray(requestPermissions);
                mActivity.requestPermissions(array, 1024);
            }
        });
    }
}
