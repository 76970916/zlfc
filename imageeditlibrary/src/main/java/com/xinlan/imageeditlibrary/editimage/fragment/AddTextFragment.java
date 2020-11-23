package com.xinlan.imageeditlibrary.editimage.fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vector.update_app.HttpManager;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.R2;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.bean.LogoBean;
import com.xinlan.imageeditlibrary.editimage.bean.MessageEvent;
import com.xinlan.imageeditlibrary.editimage.bean.TextColor;
import com.xinlan.imageeditlibrary.editimage.bean.TypeFace;
import com.xinlan.imageeditlibrary.editimage.dialog.ColorSelectDialog;
import com.xinlan.imageeditlibrary.editimage.dialog.TextInputDialog;
import com.xinlan.imageeditlibrary.editimage.ui.CircleProgressBar;
import com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils;
import com.xinlan.imageeditlibrary.editimage.utils.CommUtils;
import com.xinlan.imageeditlibrary.editimage.utils.FileUtil;
import com.xinlan.imageeditlibrary.editimage.utils.MD5Utils;
import com.xinlan.imageeditlibrary.editimage.view.Constants;
import com.xinlan.imageeditlibrary.editimage.view.StickerItem;
import com.xinlan.imageeditlibrary.editimage.view.TextStickerView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import okhttp3.Request;
import okhttp3.Response;

import static com.xinlan.imageeditlibrary.editimage.ui.CircleProgressBar.Status.Loading;
import static com.xinlan.imageeditlibrary.editimage.ui.CircleProgressBar.Status.Pause;
import static com.xinlan.imageeditlibrary.editimage.ui.CircleProgressBar.Status.Waiting;
import static com.xinlan.imageeditlibrary.editimage.view.Constants.MODE_NONE;
import static com.xinlan.imageeditlibrary.editimage.view.Constants.MODE_TEXT;


/**
 * @Description: 文字操作
 * @Author: lixh
 * @CreateDate: 2020/9/28 15:43
 * @Version: 1.0
 */
public class AddTextFragment extends BaseEditFragment {
    @BindView(R2.id.recycle_text_style)
    RecyclerView recycle_text_style;

    @BindView(R2.id.recycle_typeface)
    RecyclerView recycle_typeface;

    @BindView(R2.id.line_type_face)
    LinearLayout line_type_face;

    @BindView(R2.id.line_text_size)
    LinearLayout line_text_size;

    @BindView(R2.id.line_colors)
    LinearLayout line_colors;

    @BindView(R2.id.seekbar_size)
    SeekBar seekbar_size;

    @BindView(R2.id.tv_size)
    TextView tv_size;

    @BindView(R2.id.ed_search_content)
    EditText ed_search_content;

    @BindView(R2.id.recycle_colors)
    RecyclerView recycle_colors;

    @BindView(R2.id.line_clarity)
    LinearLayout line_clarity;

    @BindView(R2.id.tv_clarity)
    TextView tv_clarity;

    @BindView(R2.id.clarity_size)
    SeekBar clarity_size;

    @BindView(R2.id.line_edit)
    LinearLayout line_edit;

    @BindView(R2.id.line_attr)
    LinearLayout line_attr;

    @BindView(R2.id.iv_em)
    ImageView iv_em;

    @BindView(R2.id.iv_bold)
    ImageView iv_bold;

    @BindView(R2.id.space_size)
    SeekBar space_size;

    @BindView(R2.id.tv_size_space)
    TextView tv_size_space;

    @BindView(R2.id.text_data)
    TextView text_data;

    @BindView(R2.id.gentle_typeface)
    TextView gentle_typeface;

    @BindView(R2.id.iv_data)
    ImageView iv_data;
    private EditText mInputText;
    private FrameLayout work_space;
    private FrameLayout frameLayout;
    private StickerItem mTextView;
    private BaseQuickAdapter textStylesAdapter = null;
    private BaseQuickAdapter typefacesAdapter = null;
    private BaseQuickAdapter colorsAdapter = null;
    List<TypeFace> types = new ArrayList<>();
    List<TextColor> colors = new ArrayList<>();
    public List<TextStickerView> mTextList = new ArrayList<>();
    private static final int addTextColor = 10001;
    public static final String FONT_GET_URL = "http://battery.zlfc.mobi/api/admin/font/fontDown";
    public boolean longitudinal = false;

    public static AddTextFragment newInstance() {
        AddTextFragment fragment = new AddTextFragment();
        return fragment;
    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_edit_image_add_text;
    }

    @Override
    public void initUI() {
        mTextList = EditImageActivity.mTextList;
        EventBus.getDefault().register(this);
        initdata();
        initclick();
    }

    private void initdata() {
        List<String> text = new ArrayList<>();
        text.add("字体");
        text.add("大小");
        text.add("颜色");
        text.add("透明度");
        text.add("其他");
        text.add("纵向");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recycle_text_style.setLayoutManager(layoutManager);
        textStylesAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_text_style, text) {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void convert(BaseViewHolder holder, final String item) {
                final TextView textView = holder.getView(R.id.text_name);
                textView.setText(item);
                textView.setOnClickListener(v -> {
                    textView.setTextColor(Color.WHITE);
                    switch (item) {
                        case "字体":
                            line_type_face.setVisibility(View.VISIBLE);
                            line_text_size.setVisibility(View.GONE);
                            line_colors.setVisibility(View.GONE);
                            line_clarity.setVisibility(View.GONE);
                            line_attr.setVisibility(View.GONE);
                            break;
                        case "大小":
                            line_type_face.setVisibility(View.GONE);
                            line_text_size.setVisibility(View.VISIBLE);
                            line_colors.setVisibility(View.GONE);
                            line_clarity.setVisibility(View.GONE);
                            line_attr.setVisibility(View.GONE);
                            break;
                        case "颜色":
                            line_type_face.setVisibility(View.GONE);
                            line_text_size.setVisibility(View.GONE);
                            line_colors.setVisibility(View.VISIBLE);
                            line_clarity.setVisibility(View.GONE);
                            line_attr.setVisibility(View.GONE);
                            break;
                        case "透明度":
                            line_type_face.setVisibility(View.GONE);
                            line_text_size.setVisibility(View.GONE);
                            line_colors.setVisibility(View.GONE);
                            line_attr.setVisibility(View.GONE);
                            line_clarity.setVisibility(View.VISIBLE);
                            break;
                        case "其他":
                            line_attr.setVisibility(View.VISIBLE);
                            line_type_face.setVisibility(View.GONE);
                            line_text_size.setVisibility(View.GONE);
                            line_colors.setVisibility(View.GONE);
                            line_clarity.setVisibility(View.GONE);
                            break;
                        case "纵向":
                            text.set(text.size() - 1, "横向");
                            longitudinal = true;
                            textStylesAdapter.notifyDataSetChanged();
                            break;
                        case "横向":
                            longitudinal = false;
                            text.set(text.size() - 1, "纵向");
                            textStylesAdapter.notifyDataSetChanged();
                            break;
                        default:
                            break;

                    }
                });
            }
        };
        recycle_text_style.setAdapter(textStylesAdapter);

        // 导出字体预览图
        gentle_typeface.setOnClickListener(v -> {
            String imgPath = "";
            for (TypeFace item : types) {
                text_data.setText(item.getFontname());
                // 设置字体
                if (ObjectUtils.isNotEmpty(item.getLocalpath()) && ObjectUtils.equals("站酷高端黑修订", item.getFontname())) {
                    String path = Objects.requireNonNull(getActivity().getExternalFilesDir("")).getPath() + "/exportfont";
                    File appDir = new File(path);
                    if (!appDir.exists()) {
                        appDir.mkdirs();
                    }
                    File files = new File(appDir, item.getFontname() + ".png");
                    if (!files.exists()) {
                        appDir.mkdir();
                    }
                    File file = new File(item.getLocalpath());
                    Typeface tf = Typeface.createFromFile(file);
                    text_data.setTypeface(tf);
//                    text_data.setLetterSpacing(0.6f);
                    Bitmap bitmap = BitmapUtils.createBitmapFromView(text_data);
//                    bitmap = CommUtils.getMyImg(bitmap, 600, 150);
                    FileUtil.exportFile(getActivity(), bitmap, files.getAbsolutePath());
                    imgPath = files.getAbsolutePath();
                    item.setRemark(files.getAbsolutePath());
                    item.update(item.getId());
                }
                Glide.with(activity).clear(iv_data);
                Glide.with(activity).load(imgPath).into(iv_data);
            }
        });

        LinearLayoutManager layoutvertical = new LinearLayoutManager(getActivity());
        layoutvertical.setOrientation(RecyclerView.VERTICAL);
        recycle_typeface.setLayoutManager(layoutvertical);
        types = LitePal.findAll(TypeFace.class);
        typefacesAdapter = new BaseQuickAdapter<TypeFace, BaseViewHolder>(R.layout.item_text_typeface, types) {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void convert(BaseViewHolder holder, TypeFace item) {
                TextView textView = holder.getView(R.id.text_name);
                ImageView iv_check = holder.getView(R.id.iv_check);
                ImageView iv_name = holder.getView(R.id.iv_name);
                CircleProgressBar item_progress = holder.getView(R.id.item_progress);
                item_progress.setStatus(Pause);
                if (ObjectUtils.isNotEmpty(item.getRemark())) {
                    Glide.with(activity).load(item.getRemark()).into(iv_name);
                    iv_name.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                } else {
                    iv_name.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }
                item_progress.setOnClickListener(v -> {
                    CircleProgressBar.Status status = item_progress.getStatus();
                    switch (status) {
                        case Waiting:
                            item_progress.setStatus(Loading);
                            break;
                        case Loading:
                            // 暂停下载
                            item_progress.setStatus(Pause);
                            break;
                        case Pause:
                            // 开始下载
                            item_progress.setStatus(Loading);
                            break;
                        case Error:
                            item_progress.setStatus(Loading);
                            break;
                        case Finish:
                            item_progress.setStatus(Waiting);
                            break;
                    }

                });
                textView.setText(item.getFontname());
                if (item.isSelect()) {
                    textView.setTextColor(Color.parseColor("#00C4CC"));
                    iv_check.setVisibility(View.VISIBLE);
                } else {
                    iv_check.setVisibility(View.GONE);
                    textView.setTextColor(Color.WHITE);
                }
                textView.setOnClickListener(v -> {
                    click(item, iv_check, item_progress);
                });
                iv_name.setOnClickListener(v -> {
                    click(item, iv_check, item_progress);
                });
            }

            /**
             * @Author lixh
             * @Date 2020/11/10 10:47
             * @Description: 点击事件
             */
            private void click(TypeFace item, ImageView iv_check, CircleProgressBar item_progress) {
                if (ObjectUtils.isEmpty(mTextView)) {
                    ToastUtils.showShort("请您先选择或添加文字");
                    return;
                }
                activity.isExit = true;
                mTextView.setFontId(item.getOnlineid());
                if (ObjectUtils.isNotEmpty(item.getLocalpath())) {
                    File file = new File(item.getLocalpath());
                    Typeface tf = Typeface.createFromFile(file);
                    mTextView.setTextType(tf);
                    activity.mainImage.setTextType(tf);

                    ContentValues roomValues = new ContentValues();
                    roomValues.put("isSelect", "false");
                    LitePal.updateAll(TypeFace.class, roomValues, "");
                    item.setSelect(true);
                    item.update(item.getId());
                    reloadTypeFace();
                } else {
                    iv_check.setVisibility(View.GONE);
                    item_progress.setVisibility(View.VISIBLE);
                    item_progress.setStatus(Loading);
                    String path = Objects.requireNonNull(getActivity().getExternalFilesDir("")).getPath() + "/download";
                    File file = new File(path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    String device_id = getDevice_id(getActivity());
                    Map<String, String> map = generate(device_id);
                    map.put("id", String.valueOf(item.getOnlineid()));
                    map.put("device_id", device_id);
                    download(item.getFonturl(), path, map, item.getFontname() + ".ttf", new HttpManager.FileCallback() {
                        int oldRate = 0;

                        @Override
                        public void onProgress(float progress, long total) {
                            //做一下判断，防止自回调过于频繁，造成更新通知栏进度过于频繁，而出现卡顿的问题。
                            int rate = Math.round(progress * 100);
                            if (oldRate != rate) {
                                Log.d("logd", "进度:" + rate + "%");
                                item_progress.setProgress(rate);
                                //重新赋值
                                oldRate = rate;
                            }
                        }

                        @Override
                        public void onError(String error) {

                        }

                        @Override
                        public void onResponse(File file) {
                            item_progress.setVisibility(View.GONE);
                            Typeface tf = Typeface.createFromFile(file);
                            activity.mainImage.setTextType(tf);
                            ContentValues roomValues = new ContentValues();
                            roomValues.put("isSelect", "false");
                            LitePal.updateAll(TypeFace.class, roomValues, "");
                            item.setSelect(true);
                            item.setFontstate(101);
                            item.setLocalpath(file.getAbsolutePath());
                            item.update(item.getId());
                            reloadTypeFace();
                        }

                        @Override
                        public void onBefore() {

                        }
                    });
                }
            }
        };
        recycle_typeface.setAdapter(typefacesAdapter);

        // 颜色选择
        colors = LitePal.findAll(TextColor.class);
        TextColor textcolor = new TextColor();
        textcolor.setId(addTextColor);
        textcolor.setColor(addTextColor);
        colors.add(0, textcolor);
        GridLayoutManager layoutManagerColors = new GridLayoutManager(getActivity(), 7);
        layoutManagerColors.setOrientation(RecyclerView.VERTICAL);
        recycle_colors.setLayoutManager(layoutManagerColors);
        colors = colors.subList(0, 30);
        colorsAdapter = new BaseQuickAdapter<TextColor, BaseViewHolder>(R.layout.item_image_color, colors) {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void convert(BaseViewHolder holder, TextColor item) {
                mTextView = activity.mainImage.getItem();
                // 选择颜色
                FloatingActionButton iv_color = holder.getView(R.id.iv_color);
                ImageView iv_add_color = holder.getView(R.id.iv_add_color);
                if (item.getId() == addTextColor) {
                    iv_add_color.setVisibility(View.VISIBLE);
                    iv_color.setVisibility(View.GONE);
                    iv_add_color.setOnClickListener(v -> {
                        if (ObjectUtils.isEmpty(mTextView)) {
                            ToastUtils.showShort("请您先选择或添加文字");
                            return;
                        }
                        // 自定义颜色弹窗
                        ColorSelectDialog colorPickerDialog = new ColorSelectDialog(getActivity(), getResources().getColor(R.color.white), "颜色自定义");
                        colorPickerDialog.setAlphaSliderVisible(true);
                        colorPickerDialog.setHexValueEnabled(true);
                        colorPickerDialog.setOnColorListener((int color) -> {
                            activity.mainImage.setTextColor(mTextView, color);
                            activity.isExit = true;
                        });
                        colorPickerDialog.show();
                    });
                } else {
                    iv_color.setBackgroundTintList(CommUtils.getColorStateListTest(getActivity(), item.getColor()));
                    iv_color.setOnClickListener(v -> {
                        if (ObjectUtils.isEmpty(mTextView)) {
                            ToastUtils.showShort("请您先选择或添加文字");
                            return;
                        }
                        activity.isExit = true;
                        int color = ContextCompat.getColor(activity, item.getColor());
                        activity.mainImage.setTextColor(mTextView, color);
                    });
                }
            }
        };
        colorsAdapter.bindToRecyclerView(recycle_colors);
        work_space = getActivity().findViewById(R.id.work_space);
        frameLayout = getActivity().findViewById(R.id.frame_work);
        line_edit.setOnClickListener(v -> {
            LogoBean logoBean = null;
            activity.isExit = true;
            createTextStickView(logoBean);
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent event) {
        String message = event.getMessage();
        if (ObjectUtils.equals(message, Constants.MTEXT_DATA)) {
            LogoBean logoBean = (LogoBean) event.getObj();
            createTextStickView(logoBean);
        } else if (ObjectUtils.equals(message, Constants.SYNC_DATA)) {
            StickerItem textStickerViews = (StickerItem) event.getObj();
            mTextView = textStickerViews;
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
     * @Date 2020/10/13 20:19
     * @Description: 创建文字控件
     */
    private void createTextStickView(LogoBean logoBean) {
        DisplayMetrics metrics;
        metrics = getResources().getDisplayMetrics();
        if (longitudinal) {
            activity.mainImage.addTextLongitudinal(null, metrics.heightPixels, metrics.widthPixels);
            mTextView = activity.mainImage.getItem();
            mTextView.setmAlpha(255);
            CommUtils.showLongInputDialog(activity, mTextView, activity.mainImage);
            activity.mainImage.setOnEditClickListener(v -> {
                CommUtils.showLongInputDialog(activity, mTextView, activity.mainImage);
                CommUtils.mInputText.setText(mTextView.getmText());
            });
        } else {
            activity.mainImage.addText(null, metrics.heightPixels, metrics.widthPixels);
            mTextView = activity.mainImage.getItem();
            mTextView.setmAlpha(255);
            CommUtils.showInputDialog(activity, mTextView, activity.mainImage);
            activity.mainImage.setOnEditClickListener(v -> {
                CommUtils.showInputDialog(activity, mTextView, activity.mainImage);
                CommUtils.mInputText.setText(mTextView.getmText());
            });
        }


        activity.mainImage.setOnViewClickListener(v -> {
            mTextView = activity.mainImage.getItem();
        });
//        // 删除回调事件
        activity.mainImage.setOnDeleteClickListener((v, id) -> {
            if (id != 0) {
                activity.isExit = true;
                LitePal.delete(LogoBean.class, id);
            }
        });
        activity.setImageData();
    }

    /**
     * @Author lixh
     * @Date 2020/10/13 20:20
     * @Description: 弹出输入框
     */
    private void showInputDialog(final StickerItem stickerItem) {
        final TextInputDialog dialog = new TextInputDialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        //获取dialog中的eittext
        mInputText = dialog.getEditInput();
        //文本贴图
        mInputText.requestFocus();
        stickerItem.setEditText(mInputText);
        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                mTextView = stickerItem;
                String text = editable.toString().trim();
                activity.mainImage.setTextStickerContent(stickerItem, text);
                //给一个特殊标识，防止textwatcher侦听不到
                String flagt = text + "$";
                //避免出现text为空或者删除掉该textstick点击空白处弹出输入框的尴尬
                //StringUtils.isEquals(flagt)说明text为空或者被删除
                if (!ObjectUtils.equals(flagt, "$")) {
                    stickerItem.setOnEditClickListener(v -> {
                        showInputDialog(stickerItem);
                        mInputText.setText(stickerItem.getmText());
                        activity.isExit = true;
                    });
                } else {
                    stickerItem.setOnEditClickListener(v -> {
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
        if (stickerItem.getmText().equals(getResources().getString(R.string.input_hint))) {
            stickerItem.setOnEditClickListener(v -> {
                mTextView = stickerItem;
                //点中编辑框
                showInputDialog(stickerItem);
            });
        }
        dialog.show();
        Window window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    /**
     * 返回按钮逻辑
     *
     * @author panyi
     */
    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }

    }

    /**
     * 返回主菜单
     */
    @Override
    public void backToMain() {
        activity.mode = MODE_NONE;
//        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
    }

    @Override
    public void onShow() {
        activity.mode = MODE_TEXT;
    }

    /**
     * @Author lixh
     * @Date 2020/9/24 19:34
     * @Description: 重新加载 字体数据
     */
    public void reloadTypeFace() {
        types = LitePal.findAll(TypeFace.class);
        typefacesAdapter.setNewData(types);
    }

    /**
     * @Author lixh
     * @Date 2020/9/25 10:07
     * @Description: 点击事件响应
     */
    private void initclick() {
        seekbar_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (ObjectUtils.isEmpty(mTextView)) {
                    ToastUtils.showShort("请您先选择或添加文字");
                    return;
                }
                activity.isExit = true;
                tv_size.setText("" + progress);
                seekbar_size.setProgress(progress);
                activity.mainImage.setTextSize(new Float(progress * 3));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        clarity_size.setMax(255);
        clarity_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (ObjectUtils.isEmpty(mTextView)) {
                    ToastUtils.showShort("请您先选择或添加文字");
                    return;
                }
                activity.isExit = true;
                tv_clarity.setText("" + progress);
//                int currentColor = mTextView.getCurrentTextColor();
//                int red = (currentColor & 0xff0000) >> 16;
//                int green = (currentColor & 0x00ff00) >> 8;
//                int blue = (currentColor & 0x0000ff);
//                mTextView.setTextColor(Color.argb(progress, red, green, blue));
                clarity_size.setProgress(progress);
                activity.mainImage.setmAlpha(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        /**
         *  @Author lixh
         *  @Date 2020/9/25 9:42
         *  @Description: 输入监听
         */
        ed_search_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = s.toString();
                if (ObjectUtils.isEmpty(content)) {
                    if (ObjectUtils.isNotEmpty(types)) {

                    }
                } else {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        iv_bold.setOnClickListener(v -> {
            if (ObjectUtils.isEmpty(mTextView)) {
                ToastUtils.showShort("请您先选择或添加文字");
                return;
            }
            activity.isExit = true;
            if (mTextView.getTextBold()) {
                activity.mainImage.setTextBold(false);
            } else {
                activity.mainImage.setTextBold(true);
            }
        });

        iv_em.setOnClickListener(v -> {
            if (ObjectUtils.isEmpty(mTextView)) {
                ToastUtils.showShort("请您先选择或添加文字");
                return;
            }
            activity.isExit = true;
            if (mTextView.getTextEm()) {
                activity.mainImage.setTextEm(false);
            } else {
                activity.mainImage.setTextEm(true);
            }

        });
        // 字体间距
        space_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = ((float) progress / 10.0f);
                space_size.setProgress(progress);
                if (ObjectUtils.isEmpty(mTextView)) {
                    ToastUtils.showShort("请您先选择或添加文字");
                    return;
                }
                activity.isExit = true;
                tv_size_space.setText(String.valueOf(value));
                activity.mainImage.setLineSpacing(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public static Map<String, String> generate(String mark) {
        Map<String, String> map = new HashMap<>();
        int tm = (int) (System.currentTimeMillis() / 1000);
        String appendMD5 = mark + "dcrlgl" + tm;
        map.put("tm", String.valueOf(tm));
        map.put("key", MD5Utils.MD5(appendMD5));
        return map;
    }

    /**
     * @Author lixh
     * @Date 2020/4/20 19:24
     * @Description: 获取device_id
     */
    public static String getDevice_id(Context context) {
        String result = null;
        try {
            result = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            Log.w("getAndroidId", "getAndroidId: android id error");
        }
        if (result == null || result.equals("9774d56d682e549c") || result.length() < 5) {
            return "暂无" + getDeviceIdByDate();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
