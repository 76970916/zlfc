package com.zlfcapp.poster.mvp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.zlfcapp.poster.Constants;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.mvp.activity.ContentActivity;


/**
 * @Description: 用户及隐私协议dialog
 * @Author: lixh
 * @CreateDate: 2020/4/3 14:05
 * @Version: 1.0
 */
public class ProtocolDialog extends Dialog {
    TextView tvProtocol;

    TextView tvSecret;
    Button btnProtocolCancel;

    Button btnProtocolEnter;

    private Context mContext;

    public ProtocolDialog(Context context) {
        super(context);
        mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.protocol_dialogs);
        tvProtocol = findViewById(R.id.tv_protocol);
        tvSecret = findViewById(R.id.tv_secret);
        btnProtocolCancel = findViewById(R.id.btn_protocol_cancel);
        btnProtocolEnter = findViewById(R.id.btn_protocol_enter);

        tvProtocol.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ContentActivity.class);
            intent.putExtra(Constants.KEY_FRAGMENT, Constants.WEB_FRAGMENT);
            intent.putExtra(Constants.KEY_URL, Constants.YS_URL);
            mContext.startActivity(intent);
        });
        tvSecret.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ContentActivity.class);
            intent.putExtra(Constants.KEY_FRAGMENT, Constants.WEB_FRAGMENT);
            intent.putExtra(Constants.KEY_URL, Constants.USER_SERVE_URL);
            mContext.startActivity(intent);
        });
        btnProtocolCancel.setOnClickListener(v -> {
            System.exit(0);
        });
        btnProtocolEnter.setOnClickListener(v -> {
            SPUtils.getInstance().put("isShowProtocol", false);
            this.dismiss();
        });
    }
}
