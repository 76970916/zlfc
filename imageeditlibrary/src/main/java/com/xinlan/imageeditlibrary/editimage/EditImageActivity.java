package com.xinlan.imageeditlibrary.editimage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.tabs.TabLayout;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.PuzzleCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.pixplicity.sharp.OnSvgElementListener;
import com.pixplicity.sharp.Sharp;
import com.pixplicity.sharp.SharpPicture;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.vector.update_app.HttpManager;
import com.xinlan.imageeditlibrary.BaseActivity;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.activity.CropActivity;
import com.xinlan.imageeditlibrary.editimage.activity.ImagePreviewActivity;
import com.xinlan.imageeditlibrary.editimage.adapter.FragmentAdapter;
import com.xinlan.imageeditlibrary.editimage.bean.AlbumInfo;
import com.xinlan.imageeditlibrary.editimage.bean.ConstantLogo;
import com.xinlan.imageeditlibrary.editimage.bean.EditData;
import com.xinlan.imageeditlibrary.editimage.bean.ImageBean;
import com.xinlan.imageeditlibrary.editimage.bean.LogoBean;
import com.xinlan.imageeditlibrary.editimage.bean.LowerListBean;
import com.xinlan.imageeditlibrary.editimage.bean.MainLogoBean;
import com.xinlan.imageeditlibrary.editimage.bean.MessageEvent;
import com.xinlan.imageeditlibrary.editimage.bean.SvgParm;
import com.xinlan.imageeditlibrary.editimage.bean.TouchEvent;
import com.xinlan.imageeditlibrary.editimage.bean.TypeFace;
import com.xinlan.imageeditlibrary.editimage.fragment.AddTextFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.ColorFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.FilterListFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.PaintFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.PhotoFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.RotateFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.StickerFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.TransFragment;
import com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils;
import com.xinlan.imageeditlibrary.editimage.utils.CommUtils;
import com.xinlan.imageeditlibrary.editimage.utils.FileUtil;
import com.xinlan.imageeditlibrary.editimage.utils.GlideEngine;
import com.xinlan.imageeditlibrary.editimage.utils.PxUtil;
import com.xinlan.imageeditlibrary.editimage.utils.recytouch.RecyItemTouchHelperCallback;
import com.xinlan.imageeditlibrary.editimage.view.Constants;
import com.xinlan.imageeditlibrary.editimage.view.CropImageView;
import com.xinlan.imageeditlibrary.editimage.view.RotateImageView;
import com.xinlan.imageeditlibrary.editimage.view.StickerItem;
import com.xinlan.imageeditlibrary.editimage.view.StickerView;
import com.xinlan.imageeditlibrary.editimage.view.TextStickerView;
import com.xinlan.imageeditlibrary.editimage.view.TimeTableView;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.xinlan.imageeditlibrary.R2.id;
import static com.xinlan.imageeditlibrary.editimage.view.Constants.EXTRA_OUTPUT;
import static com.xinlan.imageeditlibrary.editimage.view.Constants.FILE_PATH;
import static com.xinlan.imageeditlibrary.editimage.view.Constants.IMAGE_IS_EDIT;
import static com.xinlan.imageeditlibrary.editimage.view.Constants.MODE_CROP;
import static com.xinlan.imageeditlibrary.editimage.view.Constants.MODE_FILTER;
import static com.xinlan.imageeditlibrary.editimage.view.Constants.MODE_PAINT;
import static com.xinlan.imageeditlibrary.editimage.view.Constants.MODE_ROTATE;
import static com.xinlan.imageeditlibrary.editimage.view.Constants.MODE_STICKERS;
import static com.xinlan.imageeditlibrary.editimage.view.Constants.MODE_TEXT;
import static com.xinlan.imageeditlibrary.editimage.view.Constants.MODE_TRANS;


/**
 * <p>
 * 图片编辑 主页面
 *
 * <p>
 * 包含 1.贴图 2.滤镜 3.剪裁 4.底图旋转 功能
 * add new modules
 */
public class EditImageActivity extends BaseActivity {
    @BindView(id.image_space)
    public ImageView imageSpace;
    @BindView(id.view_pager)
    ViewPager viewPager;
    @BindView(id.recycler_select)
    RecyclerView recyclerSelect;
    @BindView(id.tableView)
    TimeTableView tableView;
    @BindView(id.image_Layer)
    ImageView imageLayer;
    @BindView(id.recycler_image)
    RecyclerView recyclerImage;
    @BindView(id.relatve_bg1)
    RelativeLayout relatveBg1;
    @BindView(id.relatve_bg2)
    RelativeLayout relatveBg2;
    @BindView(id.relatve_bg3)
    RelativeLayout relatveBg3;
    @BindView(id.relatve_bg4)
    RelativeLayout relatveBg4;
    @BindView(id.image_crop)
    RelativeLayout imageCrop;
    @BindView(id.frame_layer)
    FrameLayout frameLayer;
    @BindView(id.frame_work)
    FrameLayout frameWork;
    private Unbinder mUnbinder;
    // 底层显示Bitmap
    @BindView(id.main_image)
    public StickerView mainImage;
    private Bitmap mainBitmap;
    @BindView(id.back_btn)
    View backBtn;
    @BindView(id.save_btn)
    View saveBtn;// 保存按钮
    @BindView(id.svg_image)
    ImageView svg_image;
    @BindView(id.crop_panel)
    public CropImageView mCropPanel;// 剪切操作控件
    @BindView(id.rotate_panel)
    public RotateImageView mRotatePanel;// 旋转操作控件
    @BindView(id.work_space)
    FrameLayout frameLayout;
    @BindView(id.iv_export_img)
    ImageView iv_export_img;
    @BindView(id.line_layer)
    LinearLayout line_layer;
    @BindView(id.frame_export)
    FrameLayout frame_export;
    @BindView(id.sticker_export)
    StickerView sticker_export;
    public StickerView mStickerView;// 贴图层View
    public StickerFragment mStickerFragment;// 贴图Fragment
    public FilterListFragment mFilterListFragment;// 滤镜FliterListFragment
    public TransFragment mTransFragment;// 透明度Fragment
    public RotateFragment mRotateFragment;// 图片旋转Fragment
    public AddTextFragment mAddTextFragment;//图片添加文字
    public PaintFragment mPaintFragment;//绘制模式Fragment
    private SaveImageTask mSaveImageTask;
    public int mode = Constants.MODE_NONE;// 当前操作模式
    protected int mOpTimes = 0;
    protected boolean isBeenSaved = false;
    private Activity activity;
    public String filePath;// 需要编辑图片路径
    public String saveFilePath;// 生成的新图片路径
    private int imageWidth, imageHeight;// 展示图片控件 宽 高
    public int phoneWidth;//屏幕宽
    public int phoneHeight;//屏幕高
    public List<ImageBean> layerList; //图层图片集合
    BaseQuickAdapter imageAdapter;
    BaseQuickAdapter textAdapter;
    List<String> textList;
    List<AlbumInfo> infoList;
    Handler mHandler;
    private int photoPosition = -1;
    private int textPosition = -1;
    private int eventPosition = -1;
    private int layerPosition = 0;
    int CAMERACODE = 001;
    boolean imageVisable = false;
    List<TabLayout.Tab> tabList;
    private List<Fragment> demoFragments = new ArrayList<>();
    private FragmentAdapter fragmentAdapter;
    RecyItemTouchHelperCallback itemTouchHelperCallback;
    private QMUIPopup mNormalPopup;
    String cropPath;
    Sharp mSharp = null;
    Set<Integer> hashSet = new HashSet<>();
    List<Integer> listColors = new ArrayList<>();
    String imagePath;
    public boolean bgimage = true; //选择背景图还是贴图
    public String bgImagePath;//
    private int colorPosition = -1;
    boolean bgNetwork = false;
    public Bitmap bitmapBg;//背景图
    private LoadImageTask mLoadImageTask;
    public List<TouchEvent> eventList;
    public TouchEvent currentEvent;
    int key = 0;
    private int itemId = 0;
    public static List<TextStickerView> mTextList = new ArrayList<>();
    private String bgcolor;

    List<TimeTableView> tabviewList = new ArrayList<>();//添加的背景图层
    int tabHeight;
    int tabWith;
    int largerCode;
    public int percentWith;
    public int percentHeight;
    DisplayMetrics metrics;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    boolean clear;
    public boolean isExit = false;
    List<ImageBean> imageBeanList;
    String imageUrl;

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        CommUtils.addActivityToMap(this, this.getClass().getSimpleName());
        initView();
        initadapter();
        initHandler();
        checkInitImageLoader();
        initClick();
        checkTypeface();
        checkPosterData();
        imageSpace.setOnClickListener(v -> {
            if (bitmapBg != null) {
                mainBitmap = bitmapBg;
                bgimage = true;
            }
        });
        imageCrop.setOnClickListener(v -> {
            //裁剪
            if (mainBitmap != null) {
                StickerItem stickerItem = mainImage.getItem();
                Intent intent = new Intent(EditImageActivity.this, CropActivity.class);
                if (!bgimage) {
                    intent.putExtra("listPosition", layerPosition);
                    intent.putExtra("path", stickerItem.path);
                    intent.putExtra("bitmapWith", stickerItem.bitmap.getWidth());
                    intent.putExtra("bitmapHeight", stickerItem.bitmap.getHeight());
                    Uri sourceUri = Uri.parse(stickerItem.path);
                    Uri destinationUri = Uri.parse(createImagePath(stickerItem.bitmap));
//                    int with = stickerItem.bitmap.getWidth();
//                    int height = stickerItem.bitmap.getHeight();
//                    UCrop.of(sourceUri, destinationUri)
//                            .withAspectRatio(16, 9)
//                            .withMaxResultSize(with, height)
//                            .start(EditImageActivity.this);
                } else {
                    if (bgNetwork) {
                        //网络图片
                        intent.putExtra("path", bgImagePath);
                        intent.putExtra("NetWork", true);
                    } else {
                        intent.putExtra("path", bgImagePath);
                    }
                }
                startActivity(intent);
            } else {
                ToastUtils.showShort("请选择图片");
            }
        });

    }


    public String createImagePath(Bitmap bitmap) {
        String filePath;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filePath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/"
                    + FileUtil.getPackageName(EditImageActivity.this) + "/appList";
        } else {
            filePath = "/storage/emulated/0/Android/data/" + FileUtil.getPackageName(EditImageActivity.this) + "/appList";
        }
        File fileList = new File(filePath);
        if (!fileList.exists()) {
            fileList.mkdir();
        }
        File file = new File(filePath + "/" + "crop" + ".png");

        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void checkPosterData() {
        int fontId = 0;
        String fontName = null;
        String fontUrl = null;
        Intent intent = getIntent();
        int l_id = intent.getIntExtra(Constants.LOGO_ID, 0);
        if (l_id != 0) {
            imageUrl = intent.getStringExtra("imageUrl");
            imageUrl = imageUrl.substring(1, imageUrl.length() - 1);
            List<LowerListBean> editList = LitePal.where("l_id = ?", String.valueOf(l_id)).find(LowerListBean.class);
            if (!editList.isEmpty()) {
                for (LowerListBean item : editList) {
                    TypeFace typeFace = LitePal.where("onlineid = ?", String.valueOf(item.getTextfontid())).findFirst(TypeFace.class);
                    if (ObjectUtils.isNotEmpty(typeFace)) {
                        if (ObjectUtils.isEmpty(typeFace.getLocalpath())) {
                            fontId = item.getTextfontid();
                            fontName = typeFace.getFontname();
                            fontUrl = typeFace.getFonturl();
                        }
                    }
                }
            }
            if (fontId != 0) {
                int finalFontId = fontId;
                final String fontname = fontName;
                final String fonturl = fontUrl;
                new QMUIDialog.MessageDialogBuilder(activity)
                        .setTitle("提示")
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .setMessage("当前logo包含一款字体是否下载")
                        .addAction("取消", (QMUIDialog dialog, int index) -> {
                            activity.finish();
                            dialog.dismiss();
                        })
                        .addAction(0, "下载", QMUIDialogAction.ACTION_PROP_POSITIVE, new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                Dialog tipDialog = new QMUITipDialog.Builder(activity)
                                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                        .setTipWord("下载中..")
                                        .create();
                                tipDialog.show();
                                String device_id = AddTextFragment.getDevice_id(activity);
                                Map<String, String> map = AddTextFragment.generate(device_id);
                                map.put("id", String.valueOf(finalFontId));
                                map.put("device_id", device_id);
                                String path = Objects.requireNonNull(activity.getExternalFilesDir("")).getPath() + "/download";
                                File file = new File(path);
                                if (!file.exists()) {
                                    file.mkdirs();
                                }
                                dialog.dismiss();

                                CommUtils.download(fonturl, path, map, fontname + ".ttf", new HttpManager.FileCallback() {
                                    @Override
                                    public void onProgress(float progress, long total) {
                                    }

                                    @Override
                                    public void onError(String error) {
                                        tipDialog.dismiss();
                                        ToastUtils.showShort("网络异常,字体加载失败");
                                        initPosterData(l_id);
                                    }

                                    @Override
                                    public void onResponse(File file) {
                                        ContentValues roomValues = new ContentValues();
                                        roomValues.put("isSelect", "false");
                                        LitePal.updateAll(TypeFace.class, roomValues, "");
                                        TypeFace typeFace = LitePal.where("onlineid = ?", String.valueOf(finalFontId)).findFirst(TypeFace.class);
                                        typeFace.setSelect(true);
                                        typeFace.setFontstate(101);
                                        typeFace.setLocalpath(file.getAbsolutePath());
                                        typeFace.update(typeFace.getId());
                                        tipDialog.dismiss();
                                        initPosterData(l_id);
                                    }

                                    @Override
                                    public void onBefore() {
                                    }
                                });
                            }
                        })
                        .create(mCurrentDialogStyle).show();
            } else {
                initPosterData(l_id);
            }
        }

    }

    /**
     * @Author lixh
     * @Date 2020/11/6 20:11
     * @Description: 监测字体是否已经下载
     */
    private void checkTypeface() {
        Intent intent = getIntent();
        key = intent.getIntExtra(Constants.LOGO_ID, 0);
        if (key != 0) {
            // 不为0 就是编辑状态
            int fontId = 0;
            String fontName = null;
            String fontUrl = null;
            List<LogoBean> editList = LitePal.where("fid = ?", String.valueOf(key)).order("createtime desc").find(LogoBean.class);
            if (!editList.isEmpty()) {
                for (LogoBean item : editList) {
                    TypeFace typeFace = LitePal.where("onlineid = ?", String.valueOf(item.getTextfontid())).findFirst(TypeFace.class);
                    if (ObjectUtils.isNotEmpty(typeFace)) {
                        if (ObjectUtils.isEmpty(typeFace.getLocalpath())) {
                            fontId = item.getTextfontid();
                            fontName = typeFace.getFontname();
                            fontUrl = typeFace.getFonturl();
                        }
                    }
                }
            }
            if (fontId != 0) {
                int finalFontId = fontId;
                final String fontname = fontName;
                final String fonturl = fontUrl;
                new QMUIDialog.MessageDialogBuilder(activity)
                        .setTitle("提示")
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .setMessage("当前logo包含一款字体是否下载")
                        .addAction("取消", (QMUIDialog dialog, int index) -> {
                            activity.finish();
                            dialog.dismiss();
                        })
                        .addAction(0, "下载", QMUIDialogAction.ACTION_PROP_POSITIVE, new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                Dialog tipDialog = new QMUITipDialog.Builder(activity)
                                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                        .setTipWord("下载中..")
                                        .create();
                                tipDialog.show();
                                String device_id = AddTextFragment.getDevice_id(activity);
                                Map<String, String> map = AddTextFragment.generate(device_id);
                                map.put("id", String.valueOf(finalFontId));
                                map.put("device_id", device_id);
                                String path = Objects.requireNonNull(activity.getExternalFilesDir("")).getPath() + "/download";
                                File file = new File(path);
                                if (!file.exists()) {
                                    file.mkdirs();
                                }
                                dialog.dismiss();

                                CommUtils.download(fonturl, path, map, fontname + ".ttf", new HttpManager.FileCallback() {
                                    @Override
                                    public void onProgress(float progress, long total) {
                                    }

                                    @Override
                                    public void onError(String error) {
                                        tipDialog.dismiss();
                                        ToastUtils.showShort("网络异常,字体加载失败");
                                        initData();
                                    }

                                    @Override
                                    public void onResponse(File file) {
                                        ContentValues roomValues = new ContentValues();
                                        roomValues.put("isSelect", "false");
                                        LitePal.updateAll(TypeFace.class, roomValues, "");
                                        TypeFace typeFace = LitePal.where("onlineid = ?", String.valueOf(finalFontId)).findFirst(TypeFace.class);
                                        typeFace.setSelect(true);
                                        typeFace.setFontstate(101);
                                        typeFace.setLocalpath(file.getAbsolutePath());
                                        typeFace.update(typeFace.getId());
                                        tipDialog.dismiss();
                                        initData();
                                    }

                                    @Override
                                    public void onBefore() {
                                    }
                                });
                            }
                        })
                        .create(mCurrentDialogStyle).show();
            } else {
                initData();
            }
        }
    }

    private void initPosterData(int l_id) {
        List<LowerListBean> editList = LitePal.where("l_id = ?", String.valueOf(l_id)).order("createtime desc").find(LowerListBean.class);
        imageSpace.setVisibility(View.VISIBLE);
        Glide.with(EditImageActivity.this).load(imageUrl).into(imageSpace);
        for (LowerListBean lowerListBean : editList) {
            if (lowerListBean.getType() == 1) {
                if (ObjectUtils.isNotEmpty(lowerListBean.getText())) {
                    //文字
                    createTextStickView(lowerListBean);
                }
            }
        }
    }

    /**
     * @Author lixh
     * @Date 2020/10/29 20:34
     * @Description: 是否编辑
     */
    private void initData() {
        mTextList.clear();
        clear = getIntent().getBooleanExtra("clear", false);
        if (key != 0) {
            // 不为0 就是编辑状态
            MainLogoBean mainLogoBean = LitePal.where("id = ?", String.valueOf(key)).findFirst(MainLogoBean.class);
            List<LogoBean> editList = LitePal.where("fid = ?", String.valueOf(key)).order("createtime desc").find(LogoBean.class);
            for (LogoBean logoBean : editList) {
                // 添加图片
                if (editList.size() == 1 && !clear) {
                    itemId = logoBean.getId();
                }
                int logoType = logoBean.getType();
                String logoColor = mainLogoBean.getItemBgColor();
                if (ObjectUtils.isNotEmpty(logoColor)) {
                    imageSpace.setVisibility(View.VISIBLE);
                    bgcolor = logoColor;
                    if (logoColor.startsWith("http") || FileUtils.isFile(logoColor)) {
                        Glide.with(activity).load(logoColor).into(imageSpace);
                    } else {
                        imageSpace.setBackgroundColor(Integer.parseInt(logoColor));
                    }
                }
                if (logoType == 1) {
                    //本地
                    byte[] bytes = logoBean.getImage();
                    StickerItem stickerItem = getStickerItem(logoBean);
                    Bitmap bitmap = BitmapUtils.byteToBitmap(bytes);
                    // 按照图片原先的大小重新添加到画布中
//                    Bitmap mBitmap = BitmapUtils.zoomBitmap(bitmap, stickerItem.dstRect.width(), stickerItem.dstRect.height());
                    mainImage.addBitImage(bitmap, 102, stickerItem, percentHeight, percentWith);
                    ImageBean imageBean = new ImageBean();
                    imageBean.setVisable(true);
                    imageBean.setItemType(ConstantLogo.IMAGEANDSVG);
                    imageBean.setBitmap(bitmap);
                    layerList.add(imageBean);
                    layerPosition = layerList.size() - 1;
                    imageAdapter.notifyDataSetChanged();
                    if (ObjectUtils.isNotEmpty(logoBean.getText())) {
                        //文字
                        createTextStickView(logoBean);
                    }
                } else if (logoType == 3 || logoType == 2) {
                    if (ObjectUtils.isNotEmpty(logoBean.getSvgContent())) {
                        //svg
                        StickerItem stickerItem = getStickerItem(logoBean);
                        stickerItem.setSvgContent(logoBean.getSvgContent());
                        stickerItem.setSvg(true);
                        stickerItem.setmAlpha(logoBean.getmAlpha());
                        Sharp sharp = Sharp.loadString(logoBean.getSvgContent());
                        reloadSvg(sharp, false, colorPosition, -1, stickerItem, logoBean);
                    }
                    if (ObjectUtils.isNotEmpty(logoBean.getText())) {
                        //文字
                        createTextStickView(logoBean);
                    }
                }
            }
        }
        if (clear) {
            key = 0;
        }
    }

    private StickerItem getStickerItem(LogoBean logoBean) {
        StickerItem stickerItem = new StickerItem(this);
        Rect srcRect = new Rect(logoBean.getLeftRect(), logoBean.getTopRect(), logoBean.getRightRect(), logoBean.getBottomRect());
        RectF dstRect = new RectF(logoBean.getLeftRectF(), logoBean.getTopRectF(), logoBean.getRightRectF(), logoBean.getBottomRectF());
        stickerItem.srcRect = srcRect;
        stickerItem.dstRect = dstRect;
        if (!clear) {
            stickerItem.setId(logoBean.getId());
        }
        return stickerItem;
    }

    public void setImageData() {
        //修改图层
        LinkedHashMap<Integer, StickerItem> bank = mainImage.getBank();
        if (bank != null && bank.size() != 0) {
            int maxPosition = mainImage.getMaxPosition();
            for (Map.Entry<Integer, StickerItem> entry : bank.entrySet()) {
                if (maxPosition == entry.getKey()) {
                    StickerItem stickerItem = entry.getValue();
                    ImageBean imageBean = new ImageBean();
                    imageBean.setItemType(stickerItem.itemType);
                    imageBean.setVisable(true);
                    imageBean.setBitmap(stickerItem.bitmap);
                    if (stickerItem.itemType == ConstantLogo.TEXT) {
                        imageBean.setText(stickerItem.getmText());
                    }
                    layerList.add(imageBean);
                }
            }
            layerPosition = layerList.size() - 1;
            currentEvent.setLayerList(layerList);
            mainImage.setBank(bank);
            imageAdapter.notifyDataSetChanged();
            itemTouchHelperCallback.setStickerItem(mainImage.getMap());
        }
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        break;
                    case 1:
                        String path = (String) msg.obj;
                        putBitmap(path);
                        break;
                    case 3:
                        //获取服务器上的图片
                        mainBitmap = (Bitmap) msg.obj;
                        currentEvent.setMainBitmap(mainBitmap);
                        break;
                    case 4:
                        ArrayList<Photo> resultPhotos = (ArrayList<Photo>) msg.obj;
                        EasyPhotos.startPuzzleWithPhotos(EditImageActivity.this, resultPhotos, Environment.getExternalStorageDirectory().getAbsolutePath(), "AlbumBuilder", false, GlideEngine.getInstance(), new PuzzleCallback() {
                            @Override
                            public void onResult(Photo photo) {
                                putBitmap(photo.path);
                            }
                        });
                        break;
                }
            }
        };
    }

    private void putBitmap(String path) {
        Bitmap bitmap = BitmapUtils.getSampledBitmap(path, imageWidth,
                imageHeight);
        int left = tableView.getLeft();
        int right = tableView.getRight();
        int bottom = tableView.getBottom();
        int top = tableView.getTop();
        mainImage.setLocation(left, right, bottom, top);
        mainImage.addBitImage(bitmap, path, percentHeight, percentWith);
        mainBitmap = bitmap;
    }

    private void initClick() {
        imageLayer.setOnClickListener(v -> {
            if (imageVisable) {
                recyclerImage.setVisibility(View.VISIBLE);
                imageVisable = false;
            } else {
                recyclerImage.setVisibility(View.GONE);
                imageVisable = true;
            }
        });
        imageSpace.setOnClickListener(v -> {
            if (bitmapBg != null) {
                mainBitmap = bitmapBg;
                bgimage = true;
            }
        });
        imageCrop.setOnClickListener(v -> {
            //裁剪
            if (mainBitmap != null) {
                StickerItem stickerItem = mainImage.getItem();
                Intent intent = new Intent(EditImageActivity.this, CropActivity.class);
                if (!bgimage) {
                    intent.putExtra("mapListPosition", stickerItem.mapListPosition);
                    intent.putExtra("listPosition", stickerItem.listPosition);
                    intent.putExtra("path", stickerItem.path);
                } else {
                    if (bgNetwork) {
                        //网络图片
                        intent.putExtra("path", bgImagePath);
                        intent.putExtra("NetWork", true);
                    } else {
                        intent.putExtra("path", bgImagePath);
                    }
                }
                startActivity(intent);
            } else {
                ToastUtils.showShort("请选择图片");
            }
        });
    }

    private void initadapter() {
        demoFragments.add(new PhotoFragment());
//        demoFragments.add(new PaintFragment());
        demoFragments.add(new FilterListFragment());
        demoFragments.add(new TransFragment());
        demoFragments.add(new AddTextFragment());
        demoFragments.add(new ColorFragment());
        textList.add("相册");
//        textList.add("画笔");
        textList.add("滤镜");
        textList.add("透明度");
        textList.add("文字");
        textList.add("背景");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerSelect.setLayoutManager(layoutManager);
        textAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_text_select, textList) {
            @Override
            protected void convert(BaseViewHolder holder, String item) {
                TextView textBtn = holder.getView(R.id.btn_text);
                textBtn.setText(item);
                if (textPosition != -1) {
                    if (textPosition == holder.getAdapterPosition()) {
                        textBtn.setTextColor(Color.WHITE);
                    } else {
                        textBtn.setTextColor(getResources().getColor(R.color.text_select));
                    }
                } else {
                    if (item.equals("相册")) {
                        textBtn.setTextColor(Color.WHITE);
                    }
                }
                textBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (item) {
                            case "相册":
                                viewPager.setCurrentItem(0);
                                fragmentAdapter.notifyDataSetChanged();
                                textAdapter.notifyDataSetChanged();
                                break;
//                            case "画笔":
//                                viewPager.setCurrentItem(1);
//                                fragmentAdapter.notifyDataSetChanged();
//                                textAdapter.notifyDataSetChanged();
//                                break;
                            case "滤镜":
                                if (mainBitmap == null) {
                                    ToastUtils.showShort("请添加图片");
                                } else {
                                    viewPager.setCurrentItem(1);
                                    fragmentAdapter.notifyDataSetChanged();
                                    textAdapter.notifyDataSetChanged();
                                }
                                break;
                            case "透明度":
                                if (mainBitmap == null) {
                                    ToastUtils.showShort("请添加图片");
                                } else {

                                    viewPager.setCurrentItem(2);
                                    fragmentAdapter.notifyDataSetChanged();
                                    textAdapter.notifyDataSetChanged();
                                }
                                break;
                            case "文字":
                                viewPager.setCurrentItem(3);
                                fragmentAdapter.notifyDataSetChanged();
                                textAdapter.notifyDataSetChanged();
                                break;
                            case "背景":
                                viewPager.setCurrentItem(4);
                                fragmentAdapter.notifyDataSetChanged();
                                textAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                });
            }
        };
        recyclerSelect.setAdapter(textAdapter);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentAdapter = new FragmentAdapter(fragmentManager, demoFragments);
        viewPager.setAdapter(fragmentAdapter);

//        viewPager.setOffscreenPageLimit(5);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                textPosition = position;
                EditData data_unPaint = new EditData();
                data_unPaint.setType(ConstantLogo.UNPAINT);
                switch (position) {
                    case 0:
//                        imagePaint.setVisibility(View.GONE);
                        EventBus.getDefault().post(data_unPaint);
                        viewPager.setCurrentItem(0);
                        fragmentAdapter.notifyDataSetChanged();
                        textAdapter.notifyDataSetChanged();
                        break;
//                    case 1:
//                        imagePaint.setVisibility(View.VISIBLE);
//                        EditData data_paint = new EditData();
//                        data_paint.setType(ConstantLogo.PAINT);
//                        data_paint.setPosition(layerPosition);
//                        EventBus.getDefault().post(data_paint);
//                        viewPager.setCurrentItem(1);
//                        fragmentAdapter.notifyDataSetChanged();
//                        textAdapter.notifyDataSetChanged();
//                        break;
                    case 1:
//                        imagePaint.setVisibility(View.GONE);
                        if (mainBitmap == null) {
                            ToastUtils.showShort("请添加图片");
                        }
                        viewPager.setCurrentItem(1);
                        StickerItem stickerItem = mainImage.getItem();
                        if (ObjectUtils.isNotEmpty(stickerItem)) {
                            if (stickerItem.isSvg()) {
                                break;
                            }
                        }
                        EventBus.getDefault().post(data_unPaint);
                        fragmentAdapter.notifyDataSetChanged();
                        EditData data = new EditData();
                        data.setType(ConstantLogo.INITFILTER);
                        data.setBitmap(mainBitmap);
                        EventBus.getDefault().postSticky(data);
                        textAdapter.notifyDataSetChanged();
                        break;
                    case 2:
//                        imagePaint.setVisibility(View.GONE);
                        if (mainBitmap == null) {
                            ToastUtils.showShort("请添加图片");
                        }
                        EventBus.getDefault().post(data_unPaint);
                        viewPager.setCurrentItem(2);
                        fragmentAdapter.notifyDataSetChanged();
                        textAdapter.notifyDataSetChanged();
                        break;
                    case 3:
//                        imagePaint.setVisibility(View.GONE);
                        EventBus.getDefault().post(data_unPaint);
                        viewPager.setCurrentItem(3);
                        fragmentAdapter.notifyDataSetChanged();
                        textAdapter.notifyDataSetChanged();
                        break;
                    case 4:
//                        imagePaint.setVisibility(View.GONE);
                        EventBus.getDefault().post(data_unPaint);
                        viewPager.setCurrentItem(4);
                        fragmentAdapter.notifyDataSetChanged();
                        textAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        imageBeanList = new ArrayList<ImageBean>();
        for (int i = 0; i < 4; i++) {
            ImageBean bean = new ImageBean();
            switch (i) {
                case 0:
                    bean.setImage_text("文字");
                    bean.setImage_drawable(getResources().getDrawable(R.drawable.wenzi));
                    imageBeanList.add(bean);
                    break;
                case 1:
                    bean.setImage_text("图片");
                    bean.setImage_drawable(getResources().getDrawable(R.drawable.tupian));
                    imageBeanList.add(bean);
                    break;
                case 2:
                    bean.setImage_text("贴纸");
                    bean.setImage_drawable(getResources().getDrawable(R.drawable.ziyuan));
                    imageBeanList.add(bean);
                    break;
                case 3:
                    bean.setImage_text("裁剪");
                    bean.setImage_drawable(getResources().getDrawable(R.drawable.tailoring));
                    imageBeanList.add(bean);
                    break;
            }
        }
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerImage.setLayoutManager(new LinearLayoutManager(this));
        imageAdapter = new BaseQuickAdapter<ImageBean, BaseViewHolder>(R.layout.item_image_layer, layerList) {
            @Override
            protected void convert(BaseViewHolder holder, ImageBean item) {
                RelativeLayout relative_hide = holder.getView(R.id.relative_hide);
                RelativeLayout relative_bg = holder.getView(R.id.layer_bg);
                ImageView imageView = holder.getView(R.id.item_image_layer);
                ImageView imageDisplay = holder.getView(R.id.image_display);
                TextView textView = holder.getView(R.id.textLayer);
                Glide.with(EditImageActivity.this).load(item.getBitmap()).into(imageView);
                if (item.getItemType() == ConstantLogo.TEXT) {
                    imageView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(item.getText());
                }
                if (layerPosition == holder.getAdapterPosition()) {
                    relative_bg.setVisibility(View.VISIBLE);
                } else {
                    relative_bg.setVisibility(View.GONE);
                }
                if (item.isVisable()) {
                    Glide.with(imageDisplay).load(getDrawable(R.drawable.display)).into(imageDisplay);
                } else {
                    Glide.with(imageDisplay).load(getDrawable(R.drawable.hide)).into(imageDisplay);
                }
                relative_hide.setOnClickListener(v -> {
                    if (item.isVisable()) {
                        item.setVisable(false);
                        layerList.set(holder.getAdapterPosition(), item);
                        mainImage.setGoneMapPosition(holder.getAdapterPosition());
                        notifyDataSetChanged();
                    } else {
                        item.setVisable(true);
                        mainImage.setVisibMapPosition(holder.getAdapterPosition());
                        notifyDataSetChanged();
                    }

                });
            }
        };
        itemTouchHelperCallback = new RecyItemTouchHelperCallback(imageAdapter);
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerImage);
        itemTouchHelperCallback.setCallBackListener(new RecyItemTouchHelperCallback.OnCallBack() {
            @Override

            public void callBackData(LinkedHashMap<Integer, StickerItem> bank, int fromPosition, int toPosition) {
                mainImage.setStickerItem(bank, fromPosition, toPosition);
            }
        });
        recyclerImage.setAdapter(imageAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(EditData data) {
        switch (data.getType()) {
            case ConstantLogo.TEXT:
                //修改文字
                isExit = true;
                ImageBean imageBean = layerList.get(layerPosition);
                imageBean.setText(data.getText());
                layerList.set(mainImage.getItem().listPosition, imageBean);
                imageAdapter.notifyDataSetChanged();
                break;
            case ConstantLogo.ADD_CANVAS:
                tabHeight = data.getHeight();
                tabWith = data.getWith();
                largerCode = data.getCode();
                setCancasBg();
                break;
            case ConstantLogo.PAINTLIST:
                currentEvent.setPaint(data.getPaint());
                eventList.add(currentEvent);
                break;
            case ConstantLogo.COLORPHOTO:
                isExit = true;
                bgcolor = data.getImageUrl();
                bgImagePath = data.getImageUrl();
                BitmapUtils.returnUrlBitMap(bgImagePath, mHandler);
                bgimage = true;
                bgNetwork = true;
                StickerItem stickerItem = mainImage.getItem();
                if (stickerItem.type != 101) {
                    imageCrop.setVisibility(View.VISIBLE);
                } else {
                    imageCrop.setVisibility(View.GONE);
                }
                Glide.with(EditImageActivity.this).load(data.getImageUrl()).into(imageSpace);
                break;
            case ConstantLogo.CROP_IMAGE:
                isExit = true;
                //裁剪图片
                if (bgimage) {
                    //修改背景图
                    Glide.with(EditImageActivity.this).load(data.getBitmap()).into(imageSpace);
                } else {
                    // 修改贴图
                    mainImage.setImageCropBitmap(data.getListPosition(), data.getBitmap(), data.getImageUrl(), percentHeight, percentWith);
                }
                break;
            case ConstantLogo.TRANS:
            case ConstantLogo.FILTER:
                if (bgimage) {
                    Glide.with(EditImageActivity.this).load(data.getBitmap()).into(imageSpace);
                } else {
                    mainImage.setImageBitmap(data.getBitmap());
//                    setImageData();
                }
                break;
            case ConstantLogo.ADDIMAGE:
                break;
            case ConstantLogo.PUZZLE:
            case ConstantLogo.GET_PHOTO:
                isExit = true;
                bgimage = false;
                putBitmap(data.getImageUrl());
                setImageData();
                itemTouchHelperCallback.setStickerItem(mainImage.getMap());
                imagePath = data.getImageUrl();
                if (mainBitmap != null) {
                    imageCrop.setVisibility(View.VISIBLE);
                }
                break;
            case ConstantLogo.COLOR:
                isExit = true;
                bitmapBg = null;
                if (mainImage.getItem() != null) {
                    mainBitmap = mainImage.getItem().bitmap;
                }
                imageSpace.setVisibility(View.VISIBLE);
                Glide.with(this).clear(imageSpace);
                bgcolor = data.getImageUrl();
                imageSpace.setBackgroundColor(data.getColor());
                break;
            case ConstantLogo.TEXT_DATA:
                mTextList.addAll(data.getmTextList());
                break;
            case ConstantLogo.SVG_CONTENT:
                // 对SvgContent 进行赋值
                isExit = true;
                StickerItem currentItem = mainImage.getItem();
                if (ObjectUtils.isNotEmpty(currentItem)) {
                    String svgContent = data.getSvgContent();
                    currentItem.setSvgContent(svgContent);
                }
                break;
        }
    }

    /**
     * @Author lixh
     * @Date 2020/10/20 11:25
     * @Description:
     */

    private void reloadSvg(Sharp sharp, final boolean changeColor, int position, Integer color, StickerItem stickerItem, LogoBean logoBean) {
        if (!hashSet.isEmpty()) {
            hashSet.clear();
        }
        sharp.setOnElementListener(new OnSvgElementListener() {

            @Override
            public void onSvgStart(@NonNull Canvas canvas,
                                   @Nullable RectF bounds) {
            }

            @Override
            public void onSvgEnd(@NonNull Canvas canvas,
                                 @Nullable RectF bounds) {
                mSharp = sharp;
                MessageEvent event = new MessageEvent();
                event.setMessage(Constants.SVG_DATA);
                SvgParm parm = new SvgParm();
                List<Integer> mList = new ArrayList<>(hashSet);
                parm.setListColors(mList);
                parm.setmSharp(mSharp);
                parm.setLogoBean(logoBean);
                stickerItem.percentWith = percentWith;
                stickerItem.percentHeight = percentHeight;
                parm.setSelectItem(stickerItem);
                parm.setStickerView(mainImage);
                event.setObj(parm);
                EventBus.getDefault().postSticky(event);
            }

            @SuppressLint("ResourceType")
            @Override
            public <T> T onSvgElement(@Nullable String id,
                                      @NonNull T element,
                                      @Nullable RectF elementBounds,
                                      @NonNull Canvas canvas,
                                      @Nullable RectF canvasBounds,
                                      @Nullable Paint paint) {
                if (ObjectUtils.isNotEmpty(paint)) {
                    hashSet.add(paint.getColor());
                }
                if (changeColor && paint != null && paint.getStyle() == Paint.Style.FILL) {
                    if (paint.getColor() == listColors.get(position)) {
                        paint.setColor(color);
                        if (element instanceof Path) {
                            canvas.drawPath((Path) element, paint);
                        }
                    }
                }
                if (paint != null && stickerItem.getmAlpha() != -1 && paint.getStyle() == Paint.Style.FILL) {
                    paint.setAlpha(stickerItem.getmAlpha());
                    if (element instanceof Path) {
                        canvas.drawPath((Path) element, paint);
                    }
                }
                return element;
            }

            @Override
            public <T> void onSvgElementDrawn(@Nullable String id,
                                              @NonNull T element,
                                              @NonNull Canvas canvas,
                                              @Nullable Paint paint) {
            }

        });
        sharp.getSharpPicture((SharpPicture picture) -> {
                    Drawable drawable = mSharp.getDrawable();
                    Bitmap bitmap = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        bitmap = CommUtils.getBitmapFromDrawable(activity, drawable, stickerItem);
                    }
                    ImageBean imageBean = new ImageBean();
                    imageBean.setBitmap(bitmap);
                    imageBean.setVisable(true);
                    imageBean.setItemType(ConstantLogo.IMAGEANDSVG);
                    layerList.add(imageBean);
                    layerPosition = layerList.size() - 1;
                    imageAdapter.notifyDataSetChanged();
                    // 如果是替换颜色
                    // 就覆盖HashMap 里面的对象并且保持bitmap
                    // 宽高不变化
                    if (changeColor) {
                        stickerItem.itemType = ConstantLogo.IMAGEANDSVG;
                        mainImage.replaceBitmap(bitmap, 102, stickerItem, percentHeight, percentWith);
                    } else {
                        mainImage.addBitImage(bitmap, 102, stickerItem, percentHeight, percentWith);
                    }
                }
        );
    }

    private void createTextStickView(LowerListBean logoBean) {
        if (ObjectUtils.isNotEmpty(logoBean)) {
            Typeface tf = null;
            int fontid = logoBean.getTextfontid();
            //设置字体
            if (fontid != 0) {
                TypeFace typeFace = LitePal.where("onlineid = ?", String.valueOf(fontid)).findFirst(TypeFace.class);
                if (ObjectUtils.isNotEmpty(typeFace) && ObjectUtils.isNotEmpty(typeFace.getLocalpath())) {
                    File file = new File(typeFace.getLocalpath());
                    tf = Typeface.createFromFile(file);
                }
            }
            DisplayMetrics metrics;
            metrics = getResources().getDisplayMetrics();
            mainImage.addText(null, metrics.heightPixels, metrics.widthPixels);
            StickerItem stickerItem = mainImage.getItem();
            stickerItem.setSiteX(logoBean.getLayout_x());
            stickerItem.setSiteY(logoBean.getLayout_y());
            stickerItem.itemType = ConstantLogo.TEXT;
            stickerItem.setShowHelpBox(false);
            String name = logoBean.getText();
            int alpha = logoBean.getTextAlpha();
            int color = logoBean.getTextColor();
            float textSize = logoBean.getTextsize();
            if (!clear) {
                stickerItem.setId(logoBean.getId());
            }
            stickerItem.setFontId(fontid);
            stickerItem.setTextSize(textSize);
            if (color == 0) {
                color = getResources().getColor(R.color.black);
            }
            stickerItem.setTextColor(color);
            stickerItem.setTextBold(logoBean.isTextBold());
            stickerItem.setTextEm(logoBean.isTextEm());
            stickerItem.setLineSpacing(logoBean.getLineSpacing());
            stickerItem.setmAlpha(alpha);
            stickerItem.resetView();
            stickerItem.setTextType(tf);
            mainImage.setTextStickerContent(stickerItem, name);
            setImageData();
            mainImage.setOnEditClickListener(v -> {
                viewPager.setCurrentItem(3);
                CommUtils.showInputDialog(activity, stickerItem, mainImage);
                if (ObjectUtils.isNotEmpty(mainImage.getItem().getmText())) {
                    CommUtils.mInputText.setText(mainImage.getItem().getmText());
                }
            });
            // 单击文字
            mainImage.setOnViewClickListener(v -> {
                recyclerSelect.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
                MessageEvent event = new MessageEvent();
                event.setMessage(Constants.SYNC_DATA);
                event.setObj(stickerItem);
                EventBus.getDefault().post(event);
            });
            // 删除回调事件
            mainImage.setOnDeleteClickListener((v, id) -> {
                if (id != 0) {
                    this.isExit = true;
                    LitePal.delete(LogoBean.class, id);
                }
            });
        }
    }

    private void createTextStickView(LogoBean logoBean) {
        if (ObjectUtils.isNotEmpty(logoBean)) {
            Typeface tf = null;
            int fontid = logoBean.getTextfontid();
            //设置字体
            if (fontid != 0) {
                TypeFace typeFace = LitePal.where("onlineid = ?", String.valueOf(fontid)).findFirst(TypeFace.class);
                if (ObjectUtils.isNotEmpty(typeFace) && ObjectUtils.isNotEmpty(typeFace.getLocalpath())) {
                    File file = new File(typeFace.getLocalpath());
                    tf = Typeface.createFromFile(file);
                }
            }
            DisplayMetrics metrics;
            metrics = getResources().getDisplayMetrics();
            mainImage.addText(null, metrics.heightPixels, metrics.widthPixels);
            StickerItem stickerItem = mainImage.getItem();
            stickerItem.setSiteX(logoBean.getLeftRect());
            stickerItem.setSiteY(logoBean.getTopRect());
            stickerItem.itemType = ConstantLogo.TEXT;
            stickerItem.setShowHelpBox(false);
            String name = logoBean.getText();
            int alpha = logoBean.getTextAlpha();
            int color = logoBean.getTextColor();
            float textSize = logoBean.getTextSize();
            if (!clear) {
                stickerItem.setId(logoBean.getId());
            }
            stickerItem.setFontId(fontid);
            stickerItem.setTextSize(textSize);
            if (color == 0) {
                color = getResources().getColor(R.color.black);
            }
            stickerItem.setTextColor(color);
            stickerItem.setTextBold(logoBean.isTextBold());
            stickerItem.setTextEm(logoBean.isTextEm());
            stickerItem.setLineSpacing(logoBean.getLineSpacing());
            stickerItem.setmAlpha(alpha);
            stickerItem.resetView();
            stickerItem.setTextType(tf);
            mainImage.setTextStickerContent(stickerItem, name);
            setImageData();
            mainImage.setOnEditClickListener(v -> {
                viewPager.setCurrentItem(3);
                CommUtils.showInputDialog(activity, stickerItem, mainImage);
                if (ObjectUtils.isNotEmpty(mainImage.getItem().getmText())) {
                    CommUtils.mInputText.setText(mainImage.getItem().getmText());
                }
            });
            // 单击文字
            mainImage.setOnViewClickListener(v -> {
                recyclerSelect.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
                MessageEvent event = new MessageEvent();
                event.setMessage(Constants.SYNC_DATA);
                event.setObj(stickerItem);
                EventBus.getDefault().post(event);
            });
            // 删除回调事件
            mainImage.setOnDeleteClickListener((v, id) -> {
                if (id != 0) {
                    this.isExit = true;
                    LitePal.delete(LogoBean.class, id);
                }
            });
        }
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this);
        setStatusViewColor(Color.parseColor("#333333"));
        EventBus.getDefault().register(this);
        tabHeight = getIntent().getIntExtra("imageHeight", 0);
        tabWith = getIntent().getIntExtra("imageWith", 0);
        largerCode = getIntent().getIntExtra("largerCode", 0);
        percentWith = getIntent().getIntExtra("percentWith", 100);
        percentHeight = getIntent().getIntExtra("percentHeight", 100);
        activity = this;
        metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels / 5;
        imageHeight = metrics.heightPixels / 4;

        mAddTextFragment = AddTextFragment.newInstance();
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        phoneWidth = outMetrics.widthPixels;
        phoneHeight = outMetrics.heightPixels;
        textList = new ArrayList<>();
        tabList = new ArrayList<>();
        layerList = new ArrayList<>();
        eventList = new ArrayList<>();
        currentEvent = new TouchEvent();
        setCancasBg();
        saveClick();
        mainImage.setStickerListener(new StickerView.OnStickerClickListener() {
            /**
             * @Author lixh
             * @Date 2020/10/29 11:55
             * @Description: 删除图层回调事件
             */
            @Override
            public void delete(StickerItem item) {
                mainBitmap = item.bitmap;
                isExit = true;
            }

            @Override
            public void deleteListPosition(int position) {
                layerList.remove(position);
                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void getEventItemList(LinkedHashMap<Integer, StickerItem> bank) {
                LinkedHashMap<Integer, StickerItem> map = new LinkedHashMap<>();
                for (Map.Entry<Integer, StickerItem> entry : bank.entrySet()) {
                    map.put(entry.getKey(), entry.getValue());
                }
                TouchEvent touchEvent = new TouchEvent();
                LinkedHashMap<Integer, StickerItem> bankTouch = new LinkedHashMap<>();
                for (Map.Entry<Integer, StickerItem> entryAll : mainImage.getBank().entrySet()) {
                    bankTouch.put(entryAll.getKey(), entryAll.getValue());
                }
                touchEvent.setBank(bankTouch);
                touchEvent.setMainBitmap(currentEvent.getMainBitmap());
                eventList.add(touchEvent);
                eventPosition = eventPosition + 1;
            }

            @Override
            public void getTouchSticker(int position, Bitmap bitmap) {
                mainBitmap = bitmap;
                itemTouchHelperCallback.setStickerItem(mainImage.getMap());
                bgimage = false;
                layerPosition = position;
                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void getTouchEvent() {
            }
        });
    }

    private void setCancasBg() {
        if (tabHeight != 0 && tabWith != 0) {
            FrameLayout.LayoutParams params_imageSpace = (FrameLayout.LayoutParams) imageSpace.getLayoutParams();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tableView.getLayoutParams();
            if (largerCode == 1) {
                params.height = metrics.widthPixels;
                params_imageSpace.height = metrics.widthPixels;
            } else if (largerCode == 2) {
                //1080*1920
            } else {
                params.height = tabHeight;
                params_imageSpace.height = tabHeight;
            }
            params.width = tabWith;
            params.gravity = Gravity.CENTER;
            tableView.setLayoutParams(params);
            params_imageSpace.width = tabWith;
            params_imageSpace.gravity = Gravity.CENTER;
            imageSpace.setLayoutParams(params_imageSpace);
            setWhiteBg();
        }
    }

    private void setWhiteBg() {
        int pxHeight = PxUtil.dip2px(EditImageActivity.this, 420f);
        if (largerCode == 0) {
            //高没满
            relatveBg1.setVisibility(View.VISIBLE);
            relatveBg2.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) relatveBg1.getLayoutParams();

            int height = pxHeight - tabHeight;
            int startY = height / 2;
            params1.height = startY;
            params1.gravity = Gravity.TOP;
            relatveBg1.setLayoutParams(params1);
            FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) relatveBg2.getLayoutParams();
            params2.height = startY;
            params2.gravity = Gravity.BOTTOM;
            relatveBg2.setLayoutParams(params2);
        }
        if (tabWith != metrics.widthPixels) {
            relatveBg3.setVisibility(View.VISIBLE);
            relatveBg4.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params3 = (FrameLayout.LayoutParams) relatveBg3.getLayoutParams();
            //宽没满
            int with = metrics.widthPixels - tabWith;
            int startX = with / 2;
            params3.width = startX;
            params3.gravity = Gravity.LEFT;
            params3.height = pxHeight;
            relatveBg3.setLayoutParams(params3);
            FrameLayout.LayoutParams params4 = (FrameLayout.LayoutParams) relatveBg4.getLayoutParams();
            params4.gravity = Gravity.RIGHT;
            params4.width = startX;
            params4.height = pxHeight;
            relatveBg4.setLayoutParams(params4);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            MainLogoBean item = (MainLogoBean) msg.obj;
            exportImage(item, frame_export);
        }
    };

    public void saveClick() {
        backBtn.setOnClickListener(v -> {
            finish();
        });
        saveBtn.setOnClickListener(v -> {
            MainLogoBean item = doSaveImage(false);
            if (key != 0) {
                item.setId(key);
            }
            Message msg = new Message();
            msg.what = 1;
            item.exit = true;
            msg.obj = item;
            handler.sendMessageDelayed(msg, 300);
            initStickerView(item, sticker_export, frame_export);
        });
    }

    /**
     * @Author lixh
     * @Date 2020/11/4 22:42
     * @Description: 初始View 展示图像
     */
    public void initStickerView(MainLogoBean item, StickerView item_logo, FrameLayout frame_space) {
        item_logo.setStickerListener(new StickerView.OnStickerClickListener() {
        });
        item_logo.setTemplateClickListener((StickerItem items) -> {
        });
        // 先清除view之前的数据
        item_logo.clear();
        // 拦截掉OnTouchEvent事件
        item_logo.setTouchEnable(true);
        List<LogoBean> editList = LitePal.where("fid = ?", String.valueOf(item.getId())).order("createtime asc").find(LogoBean.class);
        frame_space.removeAllViews();
        ImageView image = new ImageView(activity);
        if (image.getParent() != null) {
            ((ViewGroup) image.getParent()).removeView(image);
        }
        frame_space.addView(image);
        if (item.getItemBgColor().startsWith("http") || FileUtils.isFile(item.getItemBgColor())) {
            Glide.with(activity).load(item.getItemBgColor()).into(image);
        } else {
            image.setBackgroundColor(Integer.parseInt(item.getItemBgColor()));
        }
        for (int i = 0; i < editList.size(); i++) {
            LogoBean logoBean = editList.get(i);
            int logoType = logoBean.getType();
            if (logoType == 1) {
                if (item_logo.getParent() != null) {
                    ((ViewGroup) item_logo.getParent()).removeView(item_logo);
                }
                frame_space.addView(item_logo);
                byte[] bytes = logoBean.getImage();
                if (ObjectUtils.isEmpty(bytes)) {
                    continue;
                }
                StickerItem stickerItem = getStickerItem(frame_space, logoBean);
                Bitmap bitmap = BitmapUtils.byteToBitmap(bytes);
                // 按照图片原先的大小缩小
                // 重新添加到画布中
                Bitmap mBitmap = null;
                if (frame_space.getId() == R.id.frame_export) {
                    mBitmap = BitmapUtils.zoomBitmap(bitmap, stickerItem.dstRect.width(), stickerItem.dstRect.height());
                } else {
                    mBitmap = BitmapUtils.zoomBitmap(bitmap, stickerItem.dstRect.width() * 0.38f, stickerItem.dstRect.height() * 0.38f);
                }
                item_logo.addBitImage(mBitmap, 101, stickerItem, stickerItem.percentHeight, stickerItem.percentWith);
                // 清除所有边框
                item_logo.deleteFrame();
                if (ObjectUtils.isNotEmpty(logoBean.getText())) {
                    createTextStickView(activity, logoBean, frame_space);
                }
            } else if (logoType == 3 || logoType == 2) {
                if (ObjectUtils.isNotEmpty(logoBean.getSvgContent())) {
                    if (item_logo.getParent() != null) {
                        ((ViewGroup) item_logo.getParent()).removeView(item_logo);
                    }
                    frame_space.addView(item_logo);
                    StickerItem stickerItem = getStickerItem(logoBean);
                    stickerItem.setmAlpha(logoBean.getmAlpha());
                    Sharp sharp = Sharp.loadString(logoBean.getSvgContent());
                    reloadSvg(sharp, false, item_logo, stickerItem);
                }
                if (ObjectUtils.isNotEmpty(logoBean.getText())) {
                    createTextStickView(activity, logoBean, frame_space);
                }
            }
        }
    }

    private StickerItem getStickerItem(FrameLayout frame_space, LogoBean logoBean) {
        StickerItem stickerItem = new StickerItem(activity);
        Rect srcRect = null;
        RectF dstRect = null;
        // 是否缩放大小和位置迁移
        if (frame_space.getId() == R.id.frame_export) {
            srcRect = new Rect(logoBean.getLeftRect(), logoBean.getTopRect(),
                    logoBean.getRightRect(), logoBean.getBottomRect());
            dstRect = new RectF(logoBean.getLeftRectF(), logoBean.getTopRectF(), logoBean.getRightRectF(), logoBean.getBottomRectF());
        } else {
            srcRect = new Rect(new Double(logoBean.getLeftRect() * 0.38f).intValue(), new Double(logoBean.getTopRect() * 0.47f).intValue(),
                    new Double(logoBean.getRightRect() * 0.38f).intValue(), new Double(logoBean.getBottomRect() * 0.47f).intValue());
            dstRect = new RectF(logoBean.getLeftRectF() * 0.5f, logoBean.getTopRectF() * 0.75f,
                    logoBean.getRightRectF() * 0.5f, logoBean.getBottomRectF() * 0.7f);
        }
        stickerItem.percentHeight = logoBean.getPercentHeight();
        stickerItem.percentWith = logoBean.getPercentWith();
        stickerItem.srcRect = srcRect;
        stickerItem.dstRect = dstRect;
        stickerItem.setId(logoBean.getId());
        return stickerItem;
    }

    private void reloadSvg(Sharp sharp, final boolean changeColor, StickerView item_logo, StickerItem stickerItem) {
        sharp.setOnElementListener(new OnSvgElementListener() {

            @Override
            public void onSvgStart(@NonNull Canvas canvas,
                                   @Nullable RectF bounds) {
            }

            @Override
            public void onSvgEnd(@NonNull Canvas canvas,
                                 @Nullable RectF bounds) {
            }

            @SuppressLint("ResourceType")
            @Override
            public <T> T onSvgElement(@Nullable String id,
                                      @NonNull T element,
                                      @Nullable RectF elementBounds,
                                      @NonNull Canvas canvas,
                                      @Nullable RectF canvasBounds,
                                      @Nullable Paint paint) {
                if (paint != null && stickerItem.getmAlpha() != -1 && paint.getStyle() == Paint.Style.FILL) {
                    paint.setAlpha(stickerItem.getmAlpha());
                    if (element instanceof Path) {
                        canvas.drawPath((Path) element, paint);
                    }
                }
                return element;
            }

            @Override
            public <T> void onSvgElementDrawn(@Nullable String id,
                                              @NonNull T element,
                                              @NonNull Canvas canvas,
                                              @Nullable Paint paint) {
            }

        });
        sharp.getSharpPicture((SharpPicture picture) -> {
                    Drawable drawable = sharp.getDrawable();
                    Bitmap bitmap = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        bitmap = CommUtils.getBitmapFromDrawable(activity, drawable, stickerItem);
                    }
                    // 如果是替换颜色
                    // 就覆盖HashMap 里面的对象并且保持bitmap
                    // 宽高不变化
                    if (changeColor) {
                        item_logo.replaceBitmap(bitmap, 102, stickerItem, 80, 80);
                    } else {
                        StickerItem items = null;
                        item_logo.addBitImage(bitmap, 102, items, 80, 80);
                    }
                    // 清除所有边框
                    item_logo.deleteFrame();
                }
        );
    }

    /**
     * @Author lixh
     * @Date 2020/10/30 9:36
     * @Description: 创建文字对象
     */
    public static void createTextStickView(Activity activity, LogoBean logoBean, FrameLayout frame_space) {
        if (ObjectUtils.isNotEmpty(logoBean)) {
            Typeface tf = null;
            int fontid = logoBean.getTextfontid();
            //设置字体
            if (fontid != 0) {
                // 根据线上ID 查询字体
                TypeFace typeFace = LitePal.where("onlineid = ?", String.valueOf(fontid)).findFirst(TypeFace.class);
                if (ObjectUtils.isNotEmpty(typeFace) && ObjectUtils.isNotEmpty(typeFace.getLocalpath())) {
                    File file = new File(typeFace.getLocalpath());
                    tf = Typeface.createFromFile(file);
                }
            }
            TextStickerView edit_text = new TextStickerView(activity, tf);
            RelativeLayout.LayoutParams layoutparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutparam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            edit_text.setLayoutParams(layoutparam);
            frame_space.addView(edit_text);

            // 如果是导出的布局 不进行缩放
            if (frame_space.getId() == R.id.frame_export) {
                edit_text.setSiteX(logoBean.getLeftRect());
                edit_text.setSiteY(logoBean.getTopRect());
            } else {
                edit_text.setSiteX(new Double(logoBean.getLeftRect() * 0.38f).intValue());
                edit_text.setSiteY(new Double(logoBean.getTopRect() * 0.47).intValue());
            }
            edit_text.setShowHelpBox(false);
            String name = logoBean.getText();
            int alpha = logoBean.getTextAlpha();
            int color = logoBean.getTextColor();
            float textSize = logoBean.getTextSize();
            edit_text.setFontId(fontid);

            // 如果是导出的布局 不进行缩放
            if (frame_space.getId() == R.id.frame_export) {
                edit_text.setTextSize(textSize);
            } else {
                edit_text.setTextSize(textSize * 0.38f);
            }
            edit_text.setTextColor(color);
            edit_text.setmAlpha(alpha);
            edit_text.setText(name);
            edit_text.setTextBold(logoBean.isTextBold());
            edit_text.setTextEm(logoBean.isTextEm());
            edit_text.setLineSpacing(logoBean.getLineSpacing() * 0.38f);
            edit_text.setTouchEnable(true);
        }
    }


    public void loadImage(String filepath) {
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }
        mLoadImageTask = new LoadImageTask();
        mLoadImageTask.execute(filepath);
    }

    /**
     * 导入文件图片任务
     */
    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return BitmapUtils.getSampledBitmap(params[0], imageWidth,
                    imageHeight);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            changeMainBitmap(result, false);
        }
    }

    @Override
    public void onBackPressed() {
        switch (mode) {
            case MODE_STICKERS:
                mStickerFragment.backToMain();
                return;
            case MODE_FILTER:// 滤镜编辑状态
                mFilterListFragment.backToMain();// 保存滤镜贴图
                return;
            case MODE_CROP:// 剪切图片保存
                mTransFragment.backToMain();
                return;
            case MODE_ROTATE:// 旋转图片保存
                mRotateFragment.backToMain();
                return;
            case MODE_TEXT:
                mAddTextFragment.backToMain();
                return;
            case MODE_PAINT:
                mPaintFragment.backToMain();
                return;

            case MODE_TRANS://从透明度中返回
                mTransFragment.backToMain();
                return;
        }// end switch
        if (canAutoExit()) {
            onSaveTaskDone();
        } else {//图片还未被保存    弹出提示框确认
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.exit_without_save)
                    .setCancelable(false).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    activity.finish();
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    /**
     * @Author lixh
     * @Date 2020/10/29 11:44
     * @Description: 保存数据到数据库中
     */
    private MainLogoBean doSaveImage(boolean isExit) {
        if (mSaveImageTask != null) {
            mSaveImageTask.cancel(true);
        }
        mainImage.deleteFrame();
        LinkedHashMap<Integer, StickerItem> hashMap = mainImage.getBank();
        MainLogoBean mainLogoBean = new MainLogoBean();
        mainLogoBean.setName("未命名设计");
        mainLogoBean.setCreatetime(new Date());
        mainLogoBean.setState(1);
        mainLogoBean.setOnline(1);
        if (ObjectUtils.isNotEmpty(bgcolor)) {
            mainLogoBean.setItemBgColor(bgcolor);
        }
        if (key == 0) {
            mainLogoBean.save();
            key = mainLogoBean.getId();
        } else {
            mainLogoBean.update(key);
        }
        for (int keymap : hashMap.keySet()) {
            StickerItem items = hashMap.get(keymap);
            LogoBean logoBean = new LogoBean();
            if (mainLogoBean.getId() != 0) {
                logoBean.setFid(mainLogoBean.getId());
            } else {
                logoBean.setFid(key);
            }
            if (items.itemType == ConstantLogo.IMAGEANDSVG) {
                logoBean.setImage(FileUtil.getImg(items.bitmap));
            }
            if (items.itemType == ConstantLogo.TEXT) {
                if (mainLogoBean.getId() != 0) {
                    logoBean.setFid(mainLogoBean.getId());
                } else {
                    logoBean.setFid(key);
                }
                logoBean.setType(2);
                logoBean.setText(items.getmText());
                logoBean.setTextAlpha(items.getmAlpha());
                logoBean.setTextColor(items.getCurrentTextColor());
                logoBean.setTextfontid(items.getFontId());
                logoBean.setTextSize(items.getTextSize());
                logoBean.setLeftRect(items.layout_x);
                logoBean.setTopRect(items.layout_y);
                logoBean.setLineSpacing(items.getLineSpacing());
                logoBean.setLongitudinal(items.longitudinal);
                if (items.getTextBold()) {
                    logoBean.setTextBold(items.getTextBold());
                } else {
                    logoBean.setToDefault("textBold");
                }
                if (items.getTextEm()) {
                    logoBean.setTextEm(items.getTextEm());
                } else {
                    logoBean.setToDefault("textEm");
                }
                if (items.getId() != 0) {
                    logoBean.setId(items.getId());
                    logoBean.setUpdatetime(new Date());
                    int resultUpdate = logoBean.update(items.getId());
                    if (resultUpdate == 0) {
                        logoBean.setCreatetime(new Date());
                        logoBean.save();
                        items.setId(logoBean.getId());
                    }
                } else {
                    logoBean.setCreatetime(new Date());
                    logoBean.save();
                    items.setId(logoBean.getId());
                }
            } else {
                logoBean.setLeftRect(items.srcRect.left);
                logoBean.setTopRect(items.srcRect.top);
                logoBean.setRightRect(items.srcRect.right);
                logoBean.setBottomRect(items.srcRect.bottom);
                logoBean.setLeftRectF(items.dstRect.left);
                logoBean.setTopRectF(items.dstRect.top);
                logoBean.setRightRectF(items.dstRect.right);
                logoBean.setmAlpha(items.getmAlpha());
                logoBean.setBottomRectF(items.dstRect.bottom);
                if (ObjectUtils.isNotEmpty(items.getSvgContent())) {
                    logoBean.setSvgContent(items.getSvgContent());
                }
                // 图层宽高
                logoBean.setPercentWith(items.percentWith);
                logoBean.setPercentHeight(items.percentHeight);
                if (items.isSvg()) {
                    logoBean.setType(2);
                } else {
                    logoBean.setType(1);
                }
                if (items.getId() != 0) {
                    logoBean.setId(items.getId());
                    logoBean.setUpdatetime(new Date());
                    logoBean.update(items.getId());
                } else {
                    logoBean.setCreatetime(new Date());
                    logoBean.save();
                    items.setId(logoBean.getId());
                }
            }
        }
        if (isExit) {
            finish();
        }
        MessageEvent event = new MessageEvent();
        event.setMessage(Constants.REFRESH_LOGO_DATA);
        EventBus.getDefault().post(event);
        return mainLogoBean;
    }


    /**
     * @Author lixh
     * @Date 2020/11/4 19:43
     * @Description: 导出图片
     */
    private void exportImage(MainLogoBean item, View view) {
        if (mSaveImageTask != null) {
            mSaveImageTask.cancel(true);
        }
        mSaveImageTask = new SaveImageTask();
        TaskParm parm = new TaskParm();
        Bitmap bitmap = loadBitmapFromViewBySystem(view);
        parm.setBitmap(bitmap);
        parm.setItem(item);
        parm.exit = item.exit;
        mSaveImageTask.execute(parm);
    }

    public Bitmap loadBitmapFromViewBySystem(View v) {
        if (v == null) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        Bitmap bitmap = v.getDrawingCache();
        return bitmap;
    }

    /**
     * @param newBit
     * @param needPushUndoStack
     */
    public void changeMainBitmap(Bitmap newBit, boolean needPushUndoStack) {
        if (newBit == null)
            return;
        String dirPath = FileUtil.getImageFolderPath(activity);
        if (mainBitmap == null || mainBitmap != newBit) {
            if (needPushUndoStack) {
                increaseOpTimes();
            }
            mainBitmap = newBit;
            File file = new File(filePath);
            imageSpace.setVisibility(View.VISIBLE);
            Glide.with(this).load(mainBitmap).into(imageSpace);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }
        if (mSaveImageTask != null) {
            mSaveImageTask.cancel(true);
        }
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        mainBitmap = null;
        EventBus.getDefault().unregister(this);
    }

    public void increaseOpTimes() {
        mOpTimes++;
        isBeenSaved = false;
    }


    public boolean canAutoExit() {
        return isBeenSaved || mOpTimes == 0;
    }

    protected void onSaveTaskDone() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FILE_PATH, filePath);
        returnIntent.putExtra(EXTRA_OUTPUT, saveFilePath);
        returnIntent.putExtra(IMAGE_IS_EDIT, mOpTimes > 0);
//        FileUtil.ablumUpdate(this, saveFilePath);
        setResult(RESULT_OK, returnIntent);
        finish();
    }


    class TaskParm {
        private MainLogoBean item;
        private Bitmap bitmap;
        private String path;
        private boolean exit;

        public boolean isExit() {
            return exit;
        }

        public void setExit(boolean exit) {
            this.exit = exit;
        }

        public MainLogoBean getItem() {
            return item;
        }

        public void setItem(MainLogoBean item) {
            this.item = item;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    /**
     * 保存图像
     */
    private final class SaveImageTask extends AsyncTask<TaskParm, Void, TaskParm> {
        private Dialog dialog;

        @Override
        protected TaskParm doInBackground(TaskParm... params) {
            TaskParm parm = params[0];
            parm.exit = params[0].exit;
            Bitmap bitmap = CommUtils.getMyImg(parm.getBitmap(), 500, 500);
            saveFilePath = System.currentTimeMillis() + ".png";
            String path = FileUtil.saveImageString(activity, bitmap, saveFilePath);
            parm.setPath(path);
            return parm;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
        }

        @Override
        protected void onCancelled(TaskParm result) {
            super.onCancelled(result);
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = getLoadingDialog(activity, "保存中..", false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(TaskParm result) {
            super.onPostExecute(result);
            line_layer.setVisibility(View.VISIBLE);
            dialog.dismiss();
            if (ObjectUtils.isNotEmpty(result)) {
                String path = result.getPath();
                MainLogoBean item = result.getItem();
                item.setPreViewPath(path);
                item.update(item.getId());
                MessageEvent event = new MessageEvent();
                event.setMessage(Constants.REFRESH_LOGO_DATA);
                EventBus.getDefault().post(event);
                if (result.exit) {
                    Intent intent = new Intent(activity, ImagePreviewActivity.class);
                    intent.putExtra(Constants.IMAGE_PATH, path);
                    startActivityForResult(intent, Constants.SAVE_RESULT);
                }
            }
        }
    }

    public Bitmap getMainBit() {
        return mainBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERACODE) {
            if (resultCode == RESULT_OK) {
                //刷新相册图
                EditData data_photo = new EditData();
                data_photo.setType(ConstantLogo.CAMER);
                EventBus.getDefault().post(data_photo);
            }
        } else if (requestCode == ConstantLogo.COLORPHOTO) {
            if (resultCode == RESULT_OK) {
                ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                filePath = resultPhotos.get(0).path;
                bitmapBg = FileUtil.getBitmapForFile(resultPhotos.get(0).path);
                bgImagePath = filePath;
                bgNetwork = false;

                String path = Objects.requireNonNull(activity.getExternalFilesDir("")).getPath() + "/images";
                File file = new File(path, resultPhotos.get(0).name);
                if (!file.exists()) {
                    file.mkdir();
                }
                FileUtils.copy(filePath, file.getAbsolutePath());
                bgcolor = file.getAbsolutePath();
                loadImage(filePath);
            }
        } else if (requestCode == ConstantLogo.PUZZLE) {
            if (data != null) {
                Photo puzzlePhoto = data.getParcelableExtra(EasyPhotos.RESULT_PHOTOS);
                putBitmap(puzzlePhoto.path);
            }
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            //裁剪
            final Uri resultUri = UCrop.getOutput(data);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    /**
     * @Author lixh
     * @Date 2020/11/13 23:32
     * @Description: 保存提示
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExit) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage(R.string.exit_without_save)
                        .setCancelable(false).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainLogoBean item = doSaveImage(false);
                        if (key != 0) {
                            item.setId(key);
                        }
                        Message msg = new Message();
                        msg.what = 1;
                        item.exit = false;
                        msg.obj = item;
                        handler.sendMessageDelayed(msg, 300);
                        initStickerView(item, sticker_export, frame_export);
                        activity.finish();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}

