package com.zlfcapp.poster;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.xinlan.imageeditlibrary.editimage.bean.Colors;
import com.xinlan.imageeditlibrary.editimage.bean.ConstantLogo;
import com.xinlan.imageeditlibrary.editimage.bean.LogoBean;
import com.xinlan.imageeditlibrary.editimage.bean.TextColor;
import com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils;
import com.zlfcapp.poster.bean.BaseConfig;
import com.zlfcapp.poster.mvp.base.PureActivity;
import com.zlfcapp.poster.mvp.dialog.ProtocolDialog;
import com.zlfcapp.poster.mvp.fragment.DesignFragment;
import com.zlfcapp.poster.mvp.fragment.HomeFragment;
import com.zlfcapp.poster.mvp.fragment.MineFragment;
import com.zlfcapp.poster.utils.CommonUtils;
import com.zlfcapp.poster.utils.ImageUtil;
import com.zlfcapp.poster.utils.StatusTitleUtil;
import com.zlfcapp.poster.utils.updateapp.AppUpdateUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import jonathanfinerty.once.Once;

import static com.xinlan.imageeditlibrary.editimage.view.Constants.EXTRA_OUTPUT;
import static com.xinlan.imageeditlibrary.editimage.view.Constants.FILE_PATH;
import static com.xinlan.imageeditlibrary.editimage.view.Constants.IMAGE_IS_EDIT;
import static jonathanfinerty.once.Once.beenDone;
import static jonathanfinerty.once.Once.markDone;

public class MainActivity extends PureActivity {

    @BindView(R.id.adlayout)
    LinearLayout mAdlayout;
    @BindView(R.id.tab_rb_home)
    RadioButton mTabRbHome;
    @BindView(R.id.tab_rb_me)
    RadioButton mTabRbMe;
    HomeFragment mHomeFragment;
    MineFragment mMineFragment;
    DesignFragment mDesignFragment;
    private AlertDialog.Builder builder;
    private static final String SHOW_NEW_SESSION_DIALOG = "Dialog_Comment";
    private static final String INIT_TYPEFACE_DATA = "init_typeface_data";
    ProtocolDialog protocoldialog;
    private boolean mutex = true;

    public static final int REQUEST_PERMISSON_SORAGE = 1;
    public static final int REQUEST_PERMISSON_CAMERA = 2;
    public static final int SELECT_GALLERY_IMAGE_CODE = 7;
    public static final int TAKE_PHOTO_CODE = 8;
    public static final int ACTION_REQUEST_EDITIMAGE = 9;
    public static final int ACTION_STICKERS_IMAGE = 10;
    public static final int GET_PHOTO = 11;
    String path;
    private Uri photoURI = null;
    private int imageWidth, imageHeight;
    private Bundle savedInstanceState;

    @Override
    public int getRootViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mHomeFragment != null) {
            getSupportFragmentManager().putFragment(outState, "mHomeFragment", mHomeFragment);
        }
        if (mMineFragment != null) {
            getSupportFragmentManager().putFragment(outState, "mMineFragment", mMineFragment);
        }
        if (mDesignFragment != null) {
            getSupportFragmentManager().putFragment(outState, "mDesignFragment", mDesignFragment);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void initUI() {
        if (savedInstanceState != null) {
            mHomeFragment = (HomeFragment) getSupportFragmentManager().getFragment(savedInstanceState, "mHomeFragment");
            mMineFragment = (MineFragment) getSupportFragmentManager().getFragment(savedInstanceState, "mMineFragment");
            mDesignFragment = (DesignFragment) getSupportFragmentManager().getFragment(savedInstanceState, "mDesignFragment");
        }
        EventBus.getDefault().register(this);
        // 设置状态栏
        StatusTitleUtil.setStatusBarColor(this, R.color.whitefa);
        StatusTitleUtil.StatusBarLightMode(this);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels;
        imageHeight = metrics.heightPixels;
        showHomeFragment();
//        setStatusBarDarkMode();
        // 判断是否更新
        AppUpdateUtil.updateApp(Constants.BASE_CONFIG_INFO, this);
        BaseConfig config = BaseConfig.getInstance(false);
        boolean ishowProtocol = SPUtils.getInstance().getBoolean("isShowProtocol", true);
        if (!beenDone(Once.THIS_APP_INSTALL, SHOW_NEW_SESSION_DIALOG)) {
            showDialogComment(config);
        }
        if (ishowProtocol) {
            showDialogProtocol();
        }
        if (!beenDone(Once.THIS_APP_INSTALL, INIT_TYPEFACE_DATA)) {
            markDone(INIT_TYPEFACE_DATA);
            Colors colors = new Colors();
            String[] strings = CommonUtils.getFiledName(colors);
            for (String color : strings) {
                TextColor textColor = new TextColor();
                Object o = CommonUtils.getFieldValueByName(color, colors);
                if (ObjectUtils.isNotEmpty(o)) {
                    int hexColor = (int) o;
                    textColor.setColor(hexColor);
                    textColor.setName(color);
                    textColor.save();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(LogoBean logoBean) {
        switch (logoBean.getType()) {
            case ConstantLogo.SHOWVIEW:
                logoBean.setType(ConstantLogo.PREVIEW);
                EventBus.getDefault().postSticky(logoBean);
                break;
            case ConstantLogo.SHOWWORK:
                showHomeFragment();
                break;
        }
    }

    /**
     * @Author lixh
     * @Date 2020/4/3 15:02
     * @Description: 显示隐私协议弹窗
     */
    public void showDialogProtocol() {
        protocoldialog = new ProtocolDialog(this);
        protocoldialog.setCancelable(false);
        protocoldialog.show();
    }

    /**
     * @Author lixh
     * @Date 2020/4/3 15:02
     * @Description: 显示前往应用市场弹窗
     */
    public void showDialogComment(BaseConfig config) {
        if (config.isNeedGood()) {
            builder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("提示")
                    .setMessage(config.getGoodMessge()).setPositiveButton("前往", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent("android.intent.action.VIEW", Uri
                                    .parse("market://details?id=" + config.getPackgeName())));
                            markDone(SHOW_NEW_SESSION_DIALOG);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            builder.create().show();
        }
    }

    @OnClick({R.id.tab_rb_home, R.id.tab_rb_me, R.id.tab_rb_design})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tab_rb_home:
                showHomeFragment();
                break;
            case R.id.tab_rb_design:
                showDesignFragment();
                break;
            case R.id.tab_rb_me:
                showMoreFragment();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // System.out.println("RESULT_OK");
            switch (requestCode) {
                case SELECT_GALLERY_IMAGE_CODE://
                    if (data != null) {
                        handleSelectFromAblum(data);
                    }
                    break;
                case TAKE_PHOTO_CODE://拍照返回
                    handleTakePhoto(data);
                    break;
                case ACTION_REQUEST_EDITIMAGE://
                    if (data != null) {
                        handleEditorImage(data);
                    }
                    break;
                case GET_PHOTO:
                    if (data != null) {
                        List<LocalMedia> list = PictureSelector.obtainMultipleResult(data);
                        LogoBean bean = new LogoBean();
                        bean.setType(ConstantLogo.GET_PHOTO);
                        bean.setPath(list.get(0).getPath());
                        bean.setBitmap(ImageUtil.getBitmap(list.get(0).getPath()));
                        EventBus.getDefault().post(bean);
                    }
                    break;

            }// end switch
        }
    }

    /**
     * 处理拍照返回
     *
     * @param data
     */
    private void handleTakePhoto(Intent data) {
        if (photoURI != null) {//拍摄成功
            path = photoURI.getPath();
            startLoadTask();
        }
    }

    private void startLoadTask() {
//        LoadImageTask task = new LoadImageTask();
//        task.execute(path);
    }

    private void handleEditorImage(Intent data) {
        String newFilePath = data.getStringExtra(EXTRA_OUTPUT);
        boolean isImageEdit = data.getBooleanExtra(IMAGE_IS_EDIT, false);

        if (isImageEdit) {
            ToastUtils.showShort("保存成功");
        } else {//未编辑  还是用原来的图片
            newFilePath = data.getStringExtra(FILE_PATH);
        }
        //System.out.println("newFilePath---->" + newFilePath);
        //File file = new File(newFilePath);
        //System.out.println("newFilePath size ---->" + (file.length() / 1024)+"KB");
        Log.d("image is edit", isImageEdit + "");
//        LoadImageTask loadTask = new LoadImageTask();
//        loadTask.execute(newFilePath);
    }

    private void handleSelectFromAblum(Intent data) {
        String filepath = data.getStringExtra("imgPath");
        path = filepath;
        // System.out.println("path---->"+path);
        startLoadTask();
    }

    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return BitmapUtils.getSampledBitmap(params[0], imageWidth / 4, imageHeight / 4);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
//            if (mainBitmap != null) {
//                mainBitmap.recycle();
//                mainBitmap = null;
//                System.gc();
//            }
            LogoBean logoBean = new LogoBean();
            logoBean.setType(ConstantLogo.UPDATE_PHOTO);
            logoBean.setBitmap(result);
            EventBus.getDefault().post(logoBean);
        }
    }// end inner class

    public void showHomeFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(fragmentTransaction);
        if (mHomeFragment == null) {
            mHomeFragment = HomeFragment.newInstance();
            fragmentTransaction.add(R.id.fragmentContent, mHomeFragment, "mHomeFragment");
        }
        commitShowFragment(fragmentTransaction, mHomeFragment);
    }

    public void showMoreFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(fragmentTransaction);
        if (mMineFragment == null) {
            mMineFragment = MineFragment.newInstance();
            fragmentTransaction.add(R.id.fragmentContent, mMineFragment, "mMineFragment");
        }
        commitShowFragment(fragmentTransaction, mMineFragment);
    }

    /**
     * @Author lixh
     * @Date 2020/9/22 17:27
     * @Description: 显示设计页面
     */
    public void showDesignFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(fragmentTransaction);
        if (mDesignFragment == null) {
            mDesignFragment = DesignFragment.newInstance();
            fragmentTransaction.add(R.id.fragmentContent, mDesignFragment, "mDesignFragment");
        }
        commitShowFragment(fragmentTransaction, mDesignFragment);
    }


    public void hideAllFragment(FragmentTransaction fragmentTransaction) {
        hideFragment(fragmentTransaction, mHomeFragment);
        hideFragment(fragmentTransaction, mMineFragment);
        hideFragment(fragmentTransaction, mDesignFragment);
    }

    private void hideFragment(FragmentTransaction fragmentTransaction, Fragment fragment) {
        if (fragment != null) {
            fragmentTransaction.hide(fragment);
        }
    }

    public void commitShowFragment(FragmentTransaction fragmentTransaction, Fragment fragment) {
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
