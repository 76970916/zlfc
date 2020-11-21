package com.zlfcapp.poster.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.SPUtils;
import com.king.base.util.StringUtils;
import com.maning.imagebrowserlibrary.ImageEngine;
import com.maning.imagebrowserlibrary.MNImageBrowser;
import com.maning.imagebrowserlibrary.listeners.OnClickListener;
import com.maning.imagebrowserlibrary.listeners.OnLongClickListener;
import com.maning.imagebrowserlibrary.listeners.OnPageChangeListener;
import com.maning.imagebrowserlibrary.model.ImageBrowserConfig;
import com.xinlan.imageeditlibrary.editimage.bean.TypeFace;
import com.zlfcapp.poster.App;
import com.zlfcapp.poster.Constants;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.constantsview.GlideImageEngine;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author: lixh
 * @CreateDate: 2019/4/3 10:30
 * @Version: 1.0
 */
public class CommonUtils {

    public static boolean checkEmaile(String emaile) {
        /**
         *   [^abc]取非 除abc以外的任意字符
         *   |  将两个匹配条件进行逻辑“或”（Or）运算
         *   [1-9] 1到9 省略123456789
         *    邮箱匹配 eg: ^[a-zA-Z_]{1,}[0-9]{0,}@(([a-zA-z0-9]-*){1,}\.){1,3}[a-zA-z\-]{1,}$
         *
         */
        String RULE_EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
        //正则表达式的模式 编译正则表达式
        Pattern p = Pattern.compile(RULE_EMAIL);
        //正则表达式的匹配器
        Matcher m = p.matcher(emaile);
        //进行正则匹配\
        return m.matches();
    }

    public static boolean isEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return false;
        }
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(regEx1);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }


    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        return min + "";
    }

    public static @ColorRes
    int getThemeColorId(Context context, String theme) {
        return context.getResources().getIdentifier(theme + "_backup", "color", context.getPackageName());
    }

    public static String getTheme(Context context) {
        if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_STORM) {
            return "blue";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_HOPE) {
            return "purple";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_WOOD) {
            return "green";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_LIGHT) {
            return "green_light";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_THUNDER) {
            return "yellow";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_SAND) {
            return "orange";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_FIREY) {
            return "red";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_SAKURA) {
            return "pink";
        }
        return null;
    }

    public static int getCId(Context context) {
        if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_STORM) {
            return R.color.blue;
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_HOPE) {
            return R.color.purple;
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_WOOD) {
            return R.color.green;
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_LIGHT) {
            return R.color.green_light;
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_THUNDER) {
            return R.color.yellow;
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_SAND) {
            return R.color.orange;
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_FIREY) {
            return R.color.red;
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_SAKURA) {
            return R.color.pink;
        }
        return R.color.blue;
    }

    /**
     * @Author lixh
     * @Date 2019/5/29 15:14
     * @Description: 根据颜色id 获取16进制颜色
     */
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

    //判断文件是否存在
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * @Author lixh
     * @Date 2020/4/16 19:15
     * @Description:图片浏览功能
     */
    public static void lookImageView(Context context, ArrayList<String> filePaths, ImageView mIvFileIcon, int position) {
        // 获取一个自定义 View
        ImageBrowserConfig.TransformType transformType = ImageBrowserConfig.TransformType.Transform_Default;
        ImageBrowserConfig.IndicatorType indicatorType = ImageBrowserConfig.IndicatorType.Indicator_Number;
        ImageBrowserConfig.ScreenOrientationType screenOrientationType = ImageBrowserConfig.ScreenOrientationType.Screenorientation_Default;
        ImageEngine imageEngine = new GlideImageEngine();
        int openAnim = R.anim.mn_browser_enter_anim;
        int exitAnim = R.anim.mn_browser_exit_anim;
        View customView = LayoutInflater.from(context).inflate(R.layout.layout_custom_view, null);
        ImageView ic_close = (ImageView) customView.findViewById(R.id.iv_close);
        final TextView tv_number_indicator = (TextView) customView.findViewById(R.id.tv_number_indicator);
        ic_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭图片浏览
                MNImageBrowser.finishImageBrowser();
            }
        });
        tv_number_indicator.setText(position + "/" + filePaths.size());
        MNImageBrowser.with(context)
                //页面切换效果
                .setTransformType(transformType)
                //指示器效果
                .setIndicatorType(indicatorType)
                //设置隐藏指示器
                .setIndicatorHide(false)
                //设置自定义遮盖层，定制自己想要的效果，当设置遮盖层后，原本的指示器会被隐藏
                .setCustomShadeView(customView)
                //自定义ProgressView，不设置默认默认没有
                .setCustomProgressViewLayoutID(0)
                //当前位置
                .setCurrentPosition(position)
                //图片引擎
                .setImageEngine(imageEngine)
                //图片集合
                .setImageList(filePaths)
                //方向设置
                .setScreenOrientationType(screenOrientationType)
                //点击监听
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(FragmentActivity activity, ImageView view, int position, String url) {

                    }
                })
                //长按监听
                .setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public void onLongClick(final FragmentActivity activity, final ImageView imageView, int position, String url) {
                    }
                })
                //页面切换监听
                .setOnPageChangeListener(new OnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        if (tv_number_indicator != null) {
                            tv_number_indicator.setText((position + 1) + "/" + MNImageBrowser.getImageList().size());
                        }
                    }
                })
                //全屏模式
                .setFullScreenMode(true)
                //打开动画
                .setActivityOpenAnime(openAnim)
                //关闭动画
                .setActivityExitAnime(exitAnim)
                //手势下拉缩小效果
                .setOpenPullDownGestureEffect(true)
                //显示：传入当前View
                .show(mIvFileIcon);
    }

    /**
     * @Author lixh
     * @Date 2020/4/20 19:24
     * @Description: 获取device_id
     */
    public static String getDevice_id() {
        String result = null;
        try {
            result = Settings.Secure.getString(App.mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            Log.w("getAndroidId", "getAndroidId: android id error");
        }
        if (result == null || result.equals("9774d56d682e549c") || result.length() < 5) {
            return "s" + getDeviceIdByDate();
        }
        return result;
    }

    public static String getDeviceIdByDate() {
        Calendar Cld = Calendar.getInstance();
        int YY = Cld.get(Calendar.YEAR);
        int MM = Cld.get(Calendar.MONTH) + 1;
        int DD = Cld.get(Calendar.DATE);
        int HH = Cld.get(Calendar.HOUR_OF_DAY);
        int mm = Cld.get(Calendar.MINUTE);
        int SS = Cld.get(Calendar.SECOND);
        int MI = Cld.get(Calendar.MILLISECOND);
        //由整型而来,因此格式不加0,如  2016/5/5-1:1:32:694
        String date = (YY + "").substring(2, 4) + "/" + MM + "/" + DD + "-" + HH + ":" + mm + ":" + SS + ":" + MI;
        return date;
    }

    /**
     * @Author lixh
     * @Date 2020/4/18 17:05
     * @Description: 获取渠道号
     */
    public static String getChannel() {
        String msg = "";
        ApplicationInfo appInfo = null;
        try {
            appInfo = App.mContext.getPackageManager()
                    .getApplicationInfo(App.mContext.getPackageName(),
                            PackageManager.GET_META_DATA);

            msg = appInfo.metaData.getString("BaiduMobAd_CHANNEL");
            return msg;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            msg = appInfo.metaData.getInt("BaiduMobAd_CHANNEL") + "";
        }
        return msg;
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName() {
        String versionName = "";
        try {
            PackageManager pm = App.mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(App.mContext.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.w("Lxh", "getVersionName: " + e.getMessage());
        }
        return versionName;
    }

    /**
     * 返回当前程序版本号
     */
    public static int getAppVersionCode() {
        int versionCode = 1;
        try {
            PackageManager pm = App.mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(App.mContext.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception e) {
            Log.w("getAppVersionName", "getVersionName: " + e.getMessage());
        }
        return versionCode;
    }

    /**
     * @Author lixh
     * @Date 2020/4/21 19:08
     * @Description: 生成概率
     */
    public static boolean generateRandom(String hprat) {
        Random random = new Random();
        int generateNum = random.nextInt(100) + 1;
        Integer number = Integer.parseInt(hprat.trim());
        if (number < generateNum) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 隐藏键盘
     */
    public static void hideInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        View v = activity.getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * @Author lixh
     * @Date 2020/9/25 9:40
     * @Description: 在list中进行模糊查询
     */
    public static List<TypeFace> search(String name, List<TypeFace> list) {
        List<TypeFace> results = new ArrayList();
        for (TypeFace bean : list) {
            if (bean.getTypeName().contains(name)) {
                results.add(bean);
            }
        }
        return results;
    }


    /**
     * 获取属性名数组
     */
    public static String[] getFiledName(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
//            System.out.println(fields[i].getType());
            fieldNames[i] = fields[i].getName();
        }
        return fieldNames;
    }

    /**
     * @Author lixh
     * @Date 2020/9/25 10:55
     * @Description: 根据属性名获取属性值
     */
    public static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {
            return null;
        }
    }

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

    //file文件读取成byte[]
    public static byte[] readFile(File file) {
        RandomAccessFile rf = null;
        byte[] data = null;
        try {
            rf = new RandomAccessFile(file, "r");
            data = new byte[(int) rf.length()];
            rf.readFully(data);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            closeQuietly(rf);
        }
        return data;
    }

    //关闭读取file
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @Author lixh
     * @Date 2020/11/4 15:13
     * @Description: 是否满足查询数据的条件
     */
    public static boolean isQueryData() {
        // 上次存储的时间
        long lastTime = SPUtils.getInstance().getLong(Constants.QUERY_ONLINE_DATA, 0L);
        // 当前时间
        long currTime = System.currentTimeMillis();
        long apart_time = currTime - lastTime;
        if (lastTime == 0L || apart_time >= Constants.THREE_DAY) {
            SPUtils.getInstance().put(Constants.QUERY_ONLINE_DATA, currTime);
            return true;
        }
        return false;
    }

    public static String getJson(Activity activity, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assets = activity.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assets.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * <p>Title: getContent</p>
     * <p>Description:根据文件路径读取文件转出byte[] </p>
     *
     * @param filePath文件路径
     * @return 字节数组
     * @throws IOException
     */
    public static byte[] getContent(String filePath) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }
        fi.close();
        return buffer;
    }
}
