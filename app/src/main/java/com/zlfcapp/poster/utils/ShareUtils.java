package com.zlfcapp.poster.utils;

import android.content.Context;
import android.content.Intent;

/**
 * @Description: 分享工具类
 * @Author: lixh
 * @CreateDate: 2020/4/3 17:22
 * @Version: 1.0
 */
public class ShareUtils {
    // 调用系統方法分享文件
    public static void shareFile(Context context, String info) {
        /* if (null != file && file.exists()) {*/
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, /*Uri.fromFile(file)*/info);
        share.setType("text/plain"/*getMimeType(file.getAbsolutePath())*/);
        //此处可发送多种文件
        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(share, "分享文件"));
    }

}
