package com.xinlan.imageeditlibrary.editimage.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.pixplicity.sharp.SharpDrawable;
import com.vector.update_app.HttpManager;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.bean.ConstantLogo;
import com.xinlan.imageeditlibrary.editimage.bean.EditData;
import com.xinlan.imageeditlibrary.editimage.bean.ImageBean;
import com.xinlan.imageeditlibrary.editimage.bean.TypeFace;
import com.xinlan.imageeditlibrary.editimage.dialog.TextInputDialog;
import com.xinlan.imageeditlibrary.editimage.view.StickerItem;
import com.xinlan.imageeditlibrary.editimage.view.StickerView;
import com.xinlan.imageeditlibrary.editimage.view.TextStickerView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lxy on 2020/9/28.
 */
public class CommUtils {
    static String oldText;
    // 获取 FloatingActionButton list颜色
    public static ColorStateList getColorStateListTest(Activity activity, int colorRes) {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed}  // pressed
        };
        int color = ContextCompat.getColor(activity, colorRes);
        int[] colors = new int[]{color, color, color, color};
        return new ColorStateList(states, colors);
    }

    /**
     * @Author lixh
     * @Date 2020/9/25 9:40
     * @Description: 在list中进行模糊查询
     */
    public static List<TypeFace> search(String name, List<TypeFace> list) {
        List<TypeFace> results = new ArrayList();
        for (TypeFace bean : list) {
            if (bean.getFontname().contains(name)) {
                results.add(bean);
            }
        }
        return results;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Bitmap getBitmapFromDrawable(Context context, Drawable drawable, StickerItem stickerItem) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat || drawable instanceof SharpDrawable) {
            Bitmap bitmap = null;
            if (ObjectUtils.isEmpty(stickerItem)) {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            } else {
                // SDK 大于19
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    bitmap = Bitmap.createBitmap(Math.round(stickerItem.dstRect.width()), Math.round(stickerItem.dstRect.height()), Bitmap.Config.ARGB_8888);
                }
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    public Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Drawable assets2Drawable(Context context, String fileName) {
        InputStream open = null;
        Drawable drawable = null;
        try {
            open = context.getAssets().open(fileName);
            drawable = Drawable.createFromStream(open, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (open != null) {
                    open.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return drawable;
    }

    /*
     * svg convert to bitmap
     * */
    public static Bitmap getSvgBitmap(Context context, int width, int height, int svgRawResourceId) {
        //創建一個空白图片，然后加载到一个画布上
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //绘制，如果传入了一个错误的id，就弹出Toast报错
        if (svgRawResourceId > 0) {
            //从资源文件里提取自己的svg样式图片
            SVG svg = SVGParser.getSVGFromInputStream(context.getResources().openRawResource(svgRawResourceId),
                    width, height);
            //将svg图片画在刚刚建立的画布上
            canvas.drawPicture(svg.getPicture());
        } else {
            Toast.makeText(context, "错误的图片样式", Toast.LENGTH_SHORT).show();
        }
        return bitmap;
    }

    /**
     * 判断是否为数字，包含负数情况
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Boolean flag = false;
        String tmp;
        if (ObjectUtils.isNotEmpty(str)) {
            if (str.startsWith("-")) {
                tmp = str.substring(1);
            } else {
                tmp = str;
            }
            flag = tmp.matches("^[0.0-9.0]+$");
        }
        return flag;
    }

    /**
     * @Author lixh
     * @Date 2020/11/6 11:43
     * @Description: 读取文件内容
     */
    public static String readTxtFile(File file) {
        String content = ""; //文件内容字符串
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }
        return content;
    }

    /**
     * @Author lixh
     * @Date 2020/11/6 14:34
     * @Description:调用系統方法分享文件
     */
    public static void shareFile(Context context, File file) {
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(context, "com.zlfcapp.photo.fileprovider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setType(getMimeType(file.getAbsolutePath()));//此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(share, "分享文件"));
        } else {
            ToastUtils.showShort("分享文件不存在");
        }
    }

    /**
     * @Author lixh
     * @Date 2020/11/6 14:34
     * @Description: 根据文件后缀名获得对应的MIME类型
     */
    public static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }

    /**
     * @Author lixh
     * @Date 2020/11/6 10:40
     * @Description: 修改svg 宽高
     */
    public static Bitmap getMyImg(Bitmap rootImg, int goalW, int goalH) {
        int rootW = rootImg.getWidth();
        int rootH = rootImg.getHeight();
        // graphics 包下的
        Matrix matrix = new Matrix();
        matrix.postScale(goalW * 1.0f / rootW, goalH * 1.0f / rootH);
        return Bitmap.createBitmap(rootImg, 0, 0, rootW, rootH, matrix, true);
    }


    private static Map<String, Activity> destoryMap = new HashMap<>();

    //将Activity添加到队列中
    public static void addActivityToMap(Activity activity, String activityName) {
        destoryMap.put(activityName, activity);
    }

    //根据名字销毁制定Activity
    public static void removeActivity(String activityName) {
        Set<String> keySet = destoryMap.keySet();
        if (keySet.size() > 0) {
            for (String key : keySet) {
                if (activityName.equals(key)) {
                    destoryMap.get(key).finish();
                }
            }
        }
    }

    /**
     * @Author lixh
     * @Date 2020/10/22 16:16
     * @Description: 下载
     */
    public static void download(@NonNull String url, @NonNull String path, @NonNull Map<String, String> params,
                                @NonNull String fileName, @NonNull final HttpManager.FileCallback callback) {
        OkHttpUtils.get()
                .url(url)
                .params(params)
                .build()
                .execute(new FileCallBack(path, fileName) {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        callback.onProgress(progress, total);
                    }

                    @Override
                    public void onError(okhttp3.Call call, Response response, Exception e, int id) {
                        callback.onError(validateError(e, response));
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        callback.onResponse(response);

                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        callback.onBefore();
                    }
                });
    }

    /**
     * @Author lixh
     * @Date 2020/11/10 16:11
     * @Description: 获取静态颜色
     */
    public static void getColors(Context context, List<Integer> colors) {
        colors.add(context.getResources().getColor(R.color.green_trans));
        colors.add(context.getResources().getColor(R.color.materialcolorpicker__green));
        colors.add(context.getResources().getColor(R.color.materialcolorpicker__dialogcolor));

        colors.add(context.getResources().getColor(R.color.app_color_blue_2));
        colors.add(context.getResources().getColor(R.color.blue));
        colors.add(context.getResources().getColor(R.color.clone_xunlei_color));

        colors.add(context.getResources().getColor(R.color.orange_backup));
        colors.add(context.getResources().getColor(R.color.orange_dark));
        colors.add(context.getResources().getColor(R.color.orange_trans));

        colors.add(context.getResources().getColor(R.color.red_btn_normal));
        colors.add(context.getResources().getColor(R.color.materialcolorpicker__dribble));
        colors.add(context.getResources().getColor(R.color.materialcolorpicker__red));

        colors.add(context.getResources().getColor(R.color.blanchedalmond));
        colors.add(context.getResources().getColor(R.color.gold));
        colors.add(context.getResources().getColor(R.color.darkorange));
        
        colors.add(context.getResources().getColor(R.color.gray_cc));
        colors.add(0);
    }

    public static String changeColor(int id, Context context) {
        StringBuffer stringBuffer = new StringBuffer();
        int color = context.getResources().getColor(id);
        stringBuffer.append("#");
        stringBuffer.append(Integer.toHexString(Color.alpha(color)));
        stringBuffer.append(Integer.toHexString(Color.red(color)));
        stringBuffer.append(Integer.toHexString(Color.green(color)));
        stringBuffer.append(Integer.toHexString(Color.blue(color)));
        return stringBuffer.toString();
    }

    /**
     * @Author lixh
     * @Date 2020/10/13 20:20
     * @Description: 弹出输入框
     */
    public static EditText mInputText;

    public static void showInputDialog(Activity activity, final TextStickerView mTextStickerView) {
        final TextInputDialog dialog = new TextInputDialog(activity);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        //获取dialog中的eittext
        mInputText = dialog.getEditInput();
        // 自动获取焦点
        mInputText.requestFocus();
        //文本贴图
        mTextStickerView.setEditText(mInputText);
        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString().trim();
                mTextStickerView.setText(text);

                //给一个特殊标识，防止textwatcher侦听不到
                String flagt = text + "$";

                //避免出现text为空或者删除掉该textstick点击空白处弹出输入框的尴尬
                //StringUtils.isEquals(flagt)说明text为空或者被删除
                if (!ObjectUtils.equals(flagt, "$")) {
                    mTextStickerView.setOnEditClickListener(v -> {
                        showInputDialog(activity, mTextStickerView);
                        mInputText.setText(mTextStickerView.getmText());
                    });
                } else {
                    mTextStickerView.setOnEditClickListener(v -> {
                        return;
                    });
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
        });

        //如果用户未输入任何字符，则textwatch监听不到，防止点击无反应
        if (mTextStickerView.getmText().equals(activity.getResources().getString(R.string.input_hint))) {
            mTextStickerView.setOnEditClickListener(v -> {
                //点中编辑框
                showInputDialog(activity, mTextStickerView);
            });
        }

        dialog.show();
        Window window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }


    /**
     * @Author lixh
     * @Date 2020/10/13 20:20
     * @Description: 弹出输入框
     */
    public static void showInputDialog(Activity activity, StickerItem stickerItem, StickerView stickerView) {
        final TextInputDialog dialog = new TextInputDialog(activity);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        //获取dialog中的eittext
        mInputText = dialog.getEditInput();
        oldText = stickerItem.getmText();
        //文本贴图
        mInputText.requestFocus();
        stickerItem.setEditText(mInputText);
        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString().trim();

                stickerView.setTextStickerContent(stickerItem,text);
               if(!text.equals(oldText)){
                   EditData data = new EditData();
                   data.setType(ConstantLogo.TEXT);
                   data.setText(text);
                   EventBus.getDefault().post(data);
               }

                //给一个特殊标识，防止textwatcher侦听不到
                String flagt = text + "$";
                //避免出现text为空或者删除掉该textstick点击空白处弹出输入框的尴尬
                //StringUtils.isEquals(flagt)说明text为空或者被删除
                if (!ObjectUtils.equals(flagt, "$")) {
                    stickerItem.setOnEditClickListener(v -> {
                        showInputDialog(activity,stickerItem,stickerView);
                        mInputText.setText(stickerItem.getmText());
                    });
                } else {
                    stickerItem.setOnEditClickListener(v -> {
                        return;
                    });
                }
                oldText = text;
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
        });

        //如果用户未输入任何字符，则textwatch监听不到，防止点击无反应
        if (stickerItem.getmText().equals(activity.getResources().getString(R.string.input_hint))) {
            stickerView.setOnEditClickListener(v -> {
                showInputDialog(activity, stickerItem,stickerView);
            });
        }
        dialog.show();
        Window window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }


}

