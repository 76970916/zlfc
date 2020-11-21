package com.zlfcapp.poster.mvp.fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ObjectUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xinlan.imageeditlibrary.editimage.bean.TextColor;
import com.xinlan.imageeditlibrary.editimage.bean.TypeFace;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.mvp.base.BaseFragment;
import com.zlfcapp.poster.mvp.base.BasePresenter;
import com.zlfcapp.poster.mvp.presenter.HomePresenter;
import com.zlfcapp.poster.mvp.view.IHomeView;
import com.zlfcapp.poster.utils.CommonUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @Description: 字体切换
 * @Author: lixh
 * @CreateDate: 2020/9/24 16:56
 * @Version: 1.0
 */
public class TypeFaceFragment extends BaseFragment<IHomeView, BasePresenter<IHomeView>> implements IHomeView {
    Unbinder unbinder;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;

    @BindView(R.id.recycle_text_style)
    RecyclerView recycle_text_style;

    @BindView(R.id.recycle_typeface)
    RecyclerView recycle_typeface;

    @BindView(R.id.tv_content)
    TextView tv_content;

    @BindView(R.id.line_type_face)
    LinearLayout line_type_face;

    @BindView(R.id.line_text_size)
    LinearLayout line_text_size;

    @BindView(R.id.line_colors)
    LinearLayout line_colors;

    @BindView(R.id.seekbar_size)
    SeekBar seekbar_size;

    @BindView(R.id.tv_size)
    TextView tv_size;

    @BindView(R.id.ed_search_content)
    EditText ed_search_content;

    @BindView(R.id.recycle_colors)
    RecyclerView recycle_colors;

    @BindView(R.id.line_clarity)
    LinearLayout line_clarity;

    @BindView(R.id.tv_clarity)
    TextView tv_clarity;

    @BindView(R.id.clarity_size)
    SeekBar clarity_size;


    private BaseQuickAdapter textStylesAdapter = null;
    private BaseQuickAdapter typefacesAdapter = null;
    private BaseQuickAdapter colorsAdapter = null;
    List<TypeFace> types = new ArrayList<>();
    List<TextColor> colors = new ArrayList<>();
    private List<TypeFace> datas = null;

    public static TypeFaceFragment newInstance() {
        Bundle args = new Bundle();
        TypeFaceFragment fragment = new TypeFaceFragment();
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
        return R.layout.fragment_typeface;
    }

    @Override
    public void initUI() {
        mToolBar.setTitle("字体切换");
        setSupportActionBarBackgroup(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initData() {
        List<String> text = new ArrayList<>();
        text.add("字体");
        text.add("大小");
        text.add("颜色");
        text.add("透明度");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recycle_text_style.setLayoutManager(layoutManager);
        textStylesAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_text_style, text) {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void convert(BaseViewHolder holder, String item) {
                TextView textView = holder.getView(R.id.text_name);
                textView.setText(item);
                textView.setOnClickListener(v -> {
                    textView.setTextColor(Color.WHITE);
                    switch (item) {
                        case "字体":
                            line_type_face.setVisibility(View.VISIBLE);
                            line_text_size.setVisibility(View.GONE);
                            line_colors.setVisibility(View.GONE);
                            line_clarity.setVisibility(View.GONE);
                            break;
                        case "大小":
                            line_type_face.setVisibility(View.GONE);
                            line_text_size.setVisibility(View.VISIBLE);
                            line_colors.setVisibility(View.GONE);
                            line_clarity.setVisibility(View.GONE);
                            break;
                        case "颜色":
                            line_type_face.setVisibility(View.GONE);
                            line_text_size.setVisibility(View.GONE);
                            line_colors.setVisibility(View.VISIBLE);
                            line_clarity.setVisibility(View.GONE);
                            break;
                        case "透明度":
                            line_type_face.setVisibility(View.GONE);
                            line_text_size.setVisibility(View.GONE);
                            line_colors.setVisibility(View.GONE);
                            line_clarity.setVisibility(View.VISIBLE);
                            break;
                    }
                });
            }
        };
        recycle_text_style.setAdapter(textStylesAdapter);

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
                textView.setText(item.getTypeName());
                if (item.isSelect()) {
                    textView.setTextColor(Color.parseColor("#00C4CC"));
                    iv_check.setVisibility(View.VISIBLE);
                } else {
                    iv_check.setVisibility(View.GONE);
                    textView.setTextColor(Color.WHITE);
                }
                textView.setOnClickListener(v -> {
                    AssetManager mgr = getActivity().getAssets();
                    //根据路径得到Typeface
                    Typeface tf = null;
                    switch (item.getTypeName()) {
                        case "汉仪白棋体":
                            tf = Typeface.createFromAsset(mgr, "font/agave-r.ttf");
                            break;
                        case "阿里巴巴普惠体":
                            tf = Typeface.createFromAsset(mgr, "font/Anonymous Pro B.ttf");
                            break;
                        case "包图小字体":
                            tf = Typeface.createFromAsset(mgr, "font/AurulentSansMono-Regular.otf");
                            break;
                        case "宋体":
                            tf = Typeface.createFromAsset(mgr, "font/BPmono.ttf");
                            break;
                        case "日系豆豆体":
                            tf = Typeface.createFromAsset(mgr, "font/日系豆豆体.ttf");
                            break;
                        default:
                            break;
                    }
                    tv_content.setTypeface(tf);
                    ContentValues roomValues = new ContentValues();
                    roomValues.put("isSelect", "false");
                    LitePal.updateAll(TypeFace.class, roomValues, "");
                    item.setSelect(true);
                    item.clearSavedState();
                    item.update(item.getId());
                    reloadTypeFace();
                });
            }
        };
        recycle_typeface.setAdapter(typefacesAdapter);

        // 颜色选择
        colors = LitePal.findAll(TextColor.class);
        GridLayoutManager layoutManagerColors = new GridLayoutManager(getActivity(), 9);
        layoutManagerColors.setOrientation(RecyclerView.VERTICAL);
        recycle_colors.setLayoutManager(layoutManagerColors);
        colorsAdapter = new BaseQuickAdapter<TextColor, BaseViewHolder>(R.layout.item_image_color, colors) {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void convert(BaseViewHolder holder, TextColor item) {
                // 选择颜色
                FloatingActionButton iv_color = holder.getView(R.id.iv_color);
//                Glide.with(context).load(item.getColor())
//                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
//                        .into(iv_color);
                iv_color.setBackgroundTintList(CommonUtils.getColorStateListTest(getActivity(), item.getColor()));
                iv_color.setOnClickListener(v -> {
                    tv_content.setTextColor(CommonUtils.getColorStateListTest(getActivity(), item.getColor()));
                });
            }
        };
        colorsAdapter.bindToRecyclerView(recycle_colors);
        initclick();
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
                tv_content.setTextSize(progress);
                tv_size.setText("" + progress);
                seekbar_size.setProgress(progress);
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
                tv_clarity.setText("" + progress);
                int currentColor = tv_content.getCurrentTextColor();
                int red = (currentColor & 0xff0000) >> 16;
                int green = (currentColor & 0x00ff00) >> 8;
                int blue = (currentColor & 0x0000ff);
                tv_content.setTextColor(Color.argb(progress, red, green, blue));
                clarity_size.setProgress(progress);
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
                        typefacesAdapter.setNewData(types);
                    }
                    datas = null;
                } else {
                    datas = CommonUtils.search(content, types);
                    if (ObjectUtils.isNotEmpty(datas)) {
                        typefacesAdapter.setNewData(datas);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

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

    @Override
    public BasePresenter<IHomeView> createPresenter() {
        return new HomePresenter(getApp());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void queryFontId(String url) {

    }
}
