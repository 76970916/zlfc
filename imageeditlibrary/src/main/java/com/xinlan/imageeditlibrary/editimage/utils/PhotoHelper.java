package com.xinlan.imageeditlibrary.editimage.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.xinlan.imageeditlibrary.editimage.bean.AlbumInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoHelper {
    // 是否是Android 10以上手机
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;

    public static void  getPhotos(Context context, Handler mHandler) {
        List<AlbumInfo> list = new ArrayList<>();
        AlbumInfo albumInfo1 = new AlbumInfo();
        albumInfo1.title = "拍照";
        list.add(albumInfo1);

        String orderBy = MediaStore.Images.Media.DATE_MODIFIED + " desc";
        String[] projection = new String[]{MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID, // 直接包含该图片文件的文件夹ID，防止在不同下的文件夹重名
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 直接包含该图片文件的文件夹名
                MediaStore.Images.Media.DISPLAY_NAME, // 图片文件名
                MediaStore.Images.Media.DATA, // 图片绝对路径
                MediaStore.Images.Media.TITLE, //
        };
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null, null, orderBy);

        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    AlbumInfo albumInfo = new AlbumInfo();
                    albumInfo.path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File file = new File(albumInfo.path);
                    if (file.exists() && file.length() > 0) {
                        albumInfo.folderId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                        albumInfo.folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                        albumInfo.fileId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                        albumInfo.imageName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        albumInfo.title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                        albumInfo.count = cursor.getInt(5);//该文件夹下一共有多少张图片
                        list.add(albumInfo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        Message message = new Message();
        message.what = 1;
        message.obj = list;
        mHandler.sendMessage(message);
    }


}
