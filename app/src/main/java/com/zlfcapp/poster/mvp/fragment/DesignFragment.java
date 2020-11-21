package com.zlfcapp.poster.mvp.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.sharp.OnSvgElementListener;
import com.pixplicity.sharp.Sharp;
import com.pixplicity.sharp.SharpPicture;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.activity.CanvasLargerActivity;
import com.xinlan.imageeditlibrary.editimage.bean.LogoBean;
import com.xinlan.imageeditlibrary.editimage.bean.LogoTemplate;
import com.xinlan.imageeditlibrary.editimage.bean.MainLogoBean;
import com.xinlan.imageeditlibrary.editimage.bean.MessageEvent;
import com.xinlan.imageeditlibrary.editimage.bean.TypeFace;
import com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils;
import com.xinlan.imageeditlibrary.editimage.utils.CommUtils;
import com.xinlan.imageeditlibrary.editimage.utils.FileUtil;
import com.xinlan.imageeditlibrary.editimage.view.Constants;
import com.xinlan.imageeditlibrary.editimage.view.StickerItem;
import com.xinlan.imageeditlibrary.editimage.view.StickerView;
import com.xinlan.imageeditlibrary.editimage.view.TextStickerView;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.bean.TemplateCount;
import com.zlfcapp.poster.mvp.base.BaseFragment;
import com.zlfcapp.poster.mvp.presenter.HomePresenter;
import com.zlfcapp.poster.mvp.view.IHomeView;
import com.zlfcapp.poster.utils.CommonUtils;
import com.zlfcapp.poster.utils.produceReqArg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import butterknife.BindView;

import static com.zlfcapp.poster.mvp.fragment.WorksFragment.ACTION_REQUEST_EDITIMAGE;

/**
 * @Description: 设计模块
 * @Author: lixh
 * @CreateDate: 2020/9/22 16:32
 * @Version: 1.0
 */
public class DesignFragment extends BaseFragment<IHomeView, HomePresenter> implements IHomeView {

    @BindView(R.id.btn_design)
    Button btn_design;

    @BindView(R.id.refreshView)
    SwipeRefreshLayout refreshView;

    @BindView(R.id.recycle_picture)
    RecyclerView recycle_picture;

    @BindView(R.id.line_null_hint)
    LinearLayout line_null_hint;

    @BindView(R.id.add_logo)
    FloatingActionButton add_logo;

    @BindView(R.id.sticker_export)
    StickerView sticker_export;

    @BindView(R.id.frame_export)
    FrameLayout frame_export;

    @BindView(R.id.tv_export)
    TextView tv_export;

    @BindView(R.id.create_data)
    TextView create_data;

    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    List<MainLogoBean> listLogos = new ArrayList<>();
    BaseQuickAdapter typeAdapter;
    int selectType = 0;
    private SaveImageTask mSaveImageTask;
    public String saveFilePath;// 生成的新图片路径
    public String fileName;
    private PopupWindow addWindow = null;

    public static DesignFragment newInstance() {
        Bundle args = new Bundle();
        DesignFragment fragment = new DesignFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_design;
    }

    @Override
    public void initUI() {
        EventBus.getDefault().register(this);
        btn_design.setOnClickListener(v -> {
            editImageClick();
        });
        refreshView.setOnRefreshListener(() -> {
            refreshData();
            refreshView.setRefreshing(false);
        });
        add_logo.setOnClickListener(v -> {
            editImageClick();
        });

        // 导出预览图片
        tv_export.setOnClickListener(v -> {
            String path = Objects.requireNonNull(context.getExternalFilesDir("")).getPath() + "/export";
            // 首先保存图片
            File appDir = new File(path);
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            frame_export.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (MainLogoBean item : listLogos) {
                        Message msg = new Message();
                        msg.obj = item;
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                        try {
                            Thread.sleep(300);
                        } catch (Exception e) {
                        }
                        File newfile = new File(fileName);
                        fileName = newfile.getName().replace("svg", "png");
                        File exporFolder = new File(appDir, fileName);
                        fileName = exporFolder.getAbsolutePath();
                        Bitmap bitmap = BitmapUtils.createBitmapFromView(frame_export);
                        bitmap = CommUtils.getMyImg(bitmap, 500, 500);
                        LogUtils.d(fileName);
                        FileUtil.exportFile(getActivity(), bitmap, fileName);
                        try {
                            Thread.sleep(300);
                        } catch (Exception e) {
                        }
                    }
                }
            }).start();
        });
        create_data.setOnClickListener(v -> {
//            initSvg();
            exportTemplate();
        });
    }

    private void exportTemplate() {
        List<MainLogoBean> mainLogoBeans = LitePal.findAll(MainLogoBean.class);
        for (MainLogoBean item : mainLogoBeans) {
            List<LogoBean> editList = LitePal.where("fid = ?", String.valueOf(item.getId())).order("createtime asc").find(LogoBean.class);
            for (LogoBean bean : editList) {
                bean.setImage(null);
            }
            item.list = editList;
        }
        Gson gson = new Gson();
        LogUtils.e(gson.toJson(mainLogoBeans));
    }

    public void insertData() {
        List<LogoBean> beanList = LitePal.findAll(LogoBean.class);
        Gson gson = new Gson();
        for (LogoBean item : beanList) {
            List<LogoTemplate> templates = new ArrayList<>();
            String path = item.getPath();
            if (ObjectUtils.isEmpty(path)) {
                continue;
            }
            File file = new File(path);
            LogoTemplate template = new LogoTemplate();
            template.setSvgname(file.getName());
            template.setBgcolor("-1");
            template.setText(item.getText());
            template.setType(item.getType());
            template.setTextfontid(item.getTextfontid());
            template.setTextColor("-1");
            template.setWidth(500);
            template.setHeight(500);
            template.setCoordx(100);
            template.setCoordy(100);
            templates.add(template);
            String device_id = CommonUtils.getDevice_id();
            Map<String, Object> map = produceReqArg.generateObj(device_id);
            map.put("device_id", device_id);
            map.put("list", gson.toJson(templates));
            getPresenter().addTempData(map);
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            MainLogoBean item = (MainLogoBean) msg.obj;
            initStickerView(item, sticker_export, frame_export);
        }
    };

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
     * @Author lixh
     * @Date 2020/10/27 10:40
     * @Description: 前往编辑
     */
    private void editImageClick() {
        Intent it = new Intent(context, CanvasLargerActivity.class);
        startActivityForResult(it, ACTION_REQUEST_EDITIMAGE);
    }

    @Override
    public void initData() {

        listLogos = LitePal.where("online = 1").order("createtime desc").find(MainLogoBean.class);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recycle_picture.setLayoutManager(layoutManager);
        typeAdapter = new BaseQuickAdapter<MainLogoBean, BaseViewHolder>(R.layout.item_list_type, listLogos) {
            @Override
            protected void convert(BaseViewHolder holder, MainLogoBean item) {
                // 不重复利用itemview
                holder.setIsRecyclable(false);
                TextView logo_name = holder.getView(R.id.logo_name);
                ImageView iv_oper = holder.getView(R.id.iv_oper);
                ImageView image_space = holder.getView(R.id.image_space);
                FrameLayout frame_space = holder.getView(R.id.frame_space);
                logo_name.setText(item.getName());
                // 操作控件按钮
                iv_oper.setOnClickListener(v -> {
                    showMenu(frame_export, item, iv_oper);
                });
                if (ObjectUtils.isNotEmpty(item.getPreViewPath())) {
                    Glide.with(getActivity()).load(item.getPreViewPath()).into(image_space);
                }
            }

            /**
             * @Author lixh
             * @Date 2020/10/31 15:15
             * @Description: 操作菜单
             */
            private void showMenu(FrameLayout frame_export, MainLogoBean item, ImageView iv_oper) {
                // 弹窗 popWindows
                View popView = LayoutInflater.from(mContext).inflate(R.layout.item_design_menu, null);
                LinearLayout pop_edit = popView.findViewById(R.id.menu_edit);
                LinearLayout pop_delete = popView.findViewById(R.id.menu_delete);
                LinearLayout pop_rename = popView.findViewById(R.id.menu_rename);
                LinearLayout pop_copy = popView.findViewById(R.id.menu_copy);
                // 编辑
                pop_edit.setOnClickListener(view -> {
                    startActivity(new Intent(getActivity(), EditImageActivity.class).putExtra(Constants.LOGO_ID, item.getId()));
                    addWindow.dismiss();
                });
                // 删除
                pop_delete.setOnClickListener(view -> {
                    LitePal.deleteAll(LogoBean.class, "fid = ? ", String.valueOf(item.getId()));
                    LitePal.delete(MainLogoBean.class, item.getId());
                    refreshData();
                    addWindow.dismiss();
                });
                // 重命名
                pop_rename.setOnClickListener(view -> {
                    final QMUIDialog.EditTextDialogBuilder builder1 = new QMUIDialog.EditTextDialogBuilder(getActivity());
                    builder1.setTitle("重命名")
                            .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
                            .setDefaultText(item.getName())
                            .setInputType(InputType.TYPE_CLASS_TEXT)
                            .addAction("取消", (QMUIDialog dialog, int index) -> {
                                dialog.dismiss();
                            })
                            .addAction("确定", (QMUIDialog dialog, int index) -> {
                                CharSequence text = builder1.getEditText().getText();
                                if (text != null && text.length() > 0) {
                                    if (text.length() > 60) {
                                        ToastUtils.showShort("命名不得超过60个字");
                                        return;
                                    }
                                    item.setName(text.toString());
                                    item.update(item.getId());
                                    KeyboardUtils.hideSoftInput(getActivity());
                                    refreshData();
                                    dialog.dismiss();
                                } else {
                                    ToastUtils.showShort("请输入名称");
                                }
                            })
                            .create(mCurrentDialogStyle).show();
                    addWindow.dismiss();
                });
                // 导出图片
                pop_copy.setOnClickListener(view -> {
                    String imagePath = item.getPreViewPath();
                    if (ObjectUtils.isNotEmpty(imagePath)) {
                        File file = new File(imagePath);
                        CommUtils.shareFile(context, file);
                    }
                    addWindow.dismiss();
                });
                addWindow = new PopupWindow(popView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                addWindow.setBackgroundDrawable(new BitmapDrawable());
                addWindow.setFocusable(true);
                addWindow.setOutsideTouchable(true);
                addWindow.update();
                addWindow.showAsDropDown(iv_oper, -120, 0);
            }
        };
        // 点击item 进行编辑
        typeAdapter.setOnItemClickListener((BaseQuickAdapter adapter, View view, int position) -> {
            MainLogoBean itemlogo = listLogos.get(position);
            startActivity(new Intent(getActivity(), EditImageActivity.class).putExtra(Constants.LOGO_ID, itemlogo.getId()));
        });
        recycle_picture.setAdapter(typeAdapter);
        refreshData();
    }

    /**
     * @Author lixh
     * @Date 2020/11/4 22:42
     * @Description: 初始View 展示图像
     */
    public void initStickerView(MainLogoBean item, StickerView item_logo, FrameLayout frame_space) {
        item_logo.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), EditImageActivity.class).putExtra(Constants.LOGO_ID, item.getId()));
        });
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
        for (LogoBean logoBean : editList) {
            String logoColor = logoBean.getBgcolor();
            fileName = logoBean.getPath();
            if (ObjectUtils.isNotEmpty(logoColor)) {
                item_logo.setVisibility(View.VISIBLE);
                if (logoColor.startsWith("http") || com.blankj.utilcode.util.FileUtils.isFile(logoColor)) {
                    Glide.with(getActivity()).load(logoColor).into(item_logo);
                } else {
                    // 先去除之前的图片在设置颜色
                    Glide.with(context).clear(item_logo);
                    item_logo.setBackgroundColor(Integer.parseInt(logoColor));
                }
                if (item_logo.getParent() != null) {
                    ((ViewGroup) item_logo.getParent()).removeView(item_logo);
                }
                frame_space.addView(item_logo);
            }
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
            } else if (logoType == 2) {
                createTextStickView(getActivity(), logoBean, frame_space);
            } else if (logoType == 3) {
                Sharp sharp = Sharp.loadString(logoBean.getSvgContent());
                StickerItem stickerItem = getStickerItem(frame_space, logoBean);
                reloadSvg(sharp, false, item_logo, stickerItem);
                if (ObjectUtils.isNotEmpty(logoBean.getText())) {
                    createTextStickView(getActivity(), logoBean, frame_space);
                }
            }
        }
    }

    private StickerItem getStickerItem(FrameLayout frame_space, LogoBean logoBean) {
        StickerItem stickerItem = new StickerItem(getActivity());
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

    /**
     * @Author lixh
     * @Date 2020/10/20 11:25
     * @Description:
     */

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
                        bitmap = CommUtils.getBitmapFromDrawable(getActivity(), drawable, stickerItem);
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


    private void initSvg() {
        line_null_hint.setVisibility(View.GONE);
        recycle_picture.setVisibility(View.GONE);
        String path = Objects.requireNonNull(getActivity().getExternalFilesDir("")).getPath() + "/svg";
        File file = new File(path);
        File[] subFile = file.listFiles();
        String json = CommonUtils.getJson(getActivity(), "template.json");
        Gson gson = new GsonBuilder().create();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        Type type = new TypeToken<List<TemplateCount>>() {
        }.getType();
        List<TemplateCount> list = gson.fromJson(array, type);
        List<MainLogoBean> listmain = new ArrayList<>();
        for (int i = 0; i < subFile.length; i++) {
            File extFiel = subFile[i];
            LogUtils.d(extFiel.getAbsolutePath());
            MainLogoBean bean = new MainLogoBean();
            bean.setPreViewPath(extFiel.getAbsolutePath());
            listmain.add(bean);
            String svgContent = CommUtils.readTxtFile(extFiel);
            TemplateCount templatecount = list.get(i);
            MainLogoBean mainLogoBean = new MainLogoBean();
            mainLogoBean.setName(templatecount.getTitleName());
            mainLogoBean.setCreatetime(new java.util.Date());
            mainLogoBean.setState(1);
            mainLogoBean.setOnline(2);
            if (0 <= i && i <= 6) {
                mainLogoBean.setType(1);
            } else if (7 <= i && i <= 12) {
                mainLogoBean.setType(2);
            } else if (13 <= i && i <= 18) {
                mainLogoBean.setType(3);
            } else if (19 <= i && i <= 24) {
                mainLogoBean.setType(4);
            } else if (25 <= i && i <= 30) {
                mainLogoBean.setType(5);
            } else if (31 <= i && i <= 36) {
                mainLogoBean.setType(6);
            }
            mainLogoBean.save();
            LogoBean logoBean = new LogoBean();
            logoBean.setPath(extFiel.getAbsolutePath());
            logoBean.setFid(mainLogoBean.getId());
            try {
                logoBean.setImage(CommonUtils.getContent(extFiel.getAbsolutePath()));
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
                LitePal.delete(MainLogoBean.class, mainLogoBean.getId());
                continue;
            }
            logoBean.setLeftRect(0);
            logoBean.setTopRect(0 - 1000);
            logoBean.setRightRect(500);
            logoBean.setBottomRect(0);
            logoBean.setLeftRectF(270.0f);
            logoBean.setTopRectF(285.0f);
            logoBean.setRightRectF(810.0f);
            logoBean.setBottomRectF(780.0f);
            logoBean.setType(1);
            logoBean.setOnline(2);
            logoBean.setCreatetime(new java.util.Date());
//            logoBean.setText("LOGO 设计");
//            logoBean.setSvgContent(svgContent);
            logoBean.setTextAlpha(255);
            Random random = new Random();
            logoBean.setTextfontid(random.nextInt(38) + 1);
            logoBean.setTextSize(129.0f);
            logoBean.setLeftRect(520);
            logoBean.setTopRect(915);
            logoBean.setPercentWith(450);
            logoBean.setPercentHeight(450);
            logoBean.setBgcolor(String.valueOf(-1));
            logoBean.save();
            if (i == 31) {
                break;
            }
//            LogUtils.d(logoBean);
        }
        refreshData();
    }

    /**
     * @Author lixh
     * @Date 2020/11/4 19:43
     * @Description: 导出图片
     */
    private void doSaveImage(View view, LogoBean logoBean) {
        saveFilePath = logoBean.getPath();
        if (mSaveImageTask != null) {
            mSaveImageTask.cancel(true);
        }
        mSaveImageTask = new SaveImageTask();
        Bitmap bitmap = loadBitmapFromViewBySystem(view);
        mSaveImageTask.execute(bitmap);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent event) {
        String message = event.getMessage();
        // 刷新数据
        if (ObjectUtils.equals(message, Constants.REFRESH_LOGO_DATA)) {
            refreshData();
        }
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
                edit_text.setSiteX(523);
                edit_text.setSiteY(918);
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
                edit_text.setTextSize(100.0f);
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

    /**
     * @Author lixh
     * @Date 2020/10/28 16:29
     * @Description: 刷新数据
     */
    private void refreshData() {
        listLogos = LitePal.where("online = 1").order("createtime desc").find(MainLogoBean.class);
        if (ObjectUtils.isEmpty(listLogos) || listLogos.isEmpty()) {
            line_null_hint.setVisibility(View.VISIBLE);
            recycle_picture.setVisibility(View.GONE);
        } else {
            line_null_hint.setVisibility(View.GONE);
            recycle_picture.setVisibility(View.VISIBLE);
            typeAdapter.setNewData(listLogos);
        }
    }

    /**
     * @Author lixh
     * @Date 2020/10/28 16:29
     * @Description: 刷新数据
     */
    private void refreshData(List<MainLogoBean> list) {
        line_null_hint.setVisibility(View.GONE);
        recycle_picture.setVisibility(View.VISIBLE);
        typeAdapter.setNewData(list);
    }

    @Override
    public HomePresenter createPresenter() {
        return new HomePresenter(getApp());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 保存图像
     */
    private final class SaveImageTask extends AsyncTask<Bitmap, Void, Boolean> {
        private Dialog dialog;

        @Override
        protected Boolean doInBackground(Bitmap... params) {
            Bitmap bitmap = CommUtils.getMyImg(params[0], 500, 500);
            saveFilePath = String.valueOf(System.currentTimeMillis()) + ".png";
            return FileUtil.saveImageToGallery(getActivity(), bitmap, saveFilePath);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
        }

        @Override
        protected void onCancelled(Boolean result) {
            super.onCancelled(result);
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = EditImageActivity.getLoadingDialog(getActivity(), "保存中..", false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result) {
                ToastUtils.showShort("保存成功");
            } else {
                ToastUtils.showShort("保存异常");
            }
        }
    }
}
