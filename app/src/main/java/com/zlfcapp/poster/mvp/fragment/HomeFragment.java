package com.zlfcapp.poster.mvp.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.xinlan.imageeditlibrary.editimage.activity.CanvasLargerActivity;
import com.xinlan.imageeditlibrary.editimage.bean.BigTemplate;
import com.xinlan.imageeditlibrary.editimage.bean.LogoBean;
import com.xinlan.imageeditlibrary.editimage.bean.LogoTemplate;
import com.xinlan.imageeditlibrary.editimage.bean.MainLogoBean;
import com.xinlan.imageeditlibrary.editimage.bean.TypeFace;
import com.xinlan.imageeditlibrary.editimage.view.Constants;
import com.zlfcapp.poster.R;
import com.xinlan.imageeditlibrary.editimage.bean.ImageResult;
import com.zlfcapp.poster.bean.TemplateCount;
import com.zlfcapp.poster.mvp.activity.LogoListActivity;
import com.zlfcapp.poster.mvp.adapter.CenterLayoutManager;
import com.zlfcapp.poster.mvp.base.BaseFragment;
import com.zlfcapp.poster.mvp.presenter.HomePresenter;
import com.zlfcapp.poster.mvp.view.IHomeView;
import com.zlfcapp.poster.utils.CommonUtils;
import com.zlfcapp.poster.utils.produceReqArg;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import static com.zlfcapp.poster.mvp.fragment.WorksFragment.ACTION_REQUEST_EDITIMAGE;

/**
 * Created by ZhangBx on 2018/7/25.
 */

public class HomeFragment extends BaseFragment<IHomeView, HomePresenter> implements IHomeView {
    @BindView(R.id.ed_search_content)
    EditText ed_search_content;
    @BindView(R.id.iv_del_content)
    ImageView mIvDelContent;
    @BindView(R.id.tv_seek_start)
    TextView tv_seek_start;
    @BindView(R.id.recycler_temp)
    RecyclerView recycler_temp;
    @BindView(R.id.refreshView)
    SwipeRefreshLayout refreshView;
    @BindView(R.id.tv_querys)
    TextView tv_querys;
    List<BigTemplate> lists = new ArrayList<>();
    BaseQuickAdapter templateAdapter;
    Dialog tipDialog;
    @BindView(R.id.recycler_home)
    RecyclerView recyclerHome;
    @BindView(R.id.viewpager_home)
    ViewPager viewpagerHome;
    List<String> tabList;
    BaseQuickAdapter adapter;
    int templatePosition = -1;
    @BindView(R.id.head)
    RelativeLayout head;
    //    @BindView(R.id.mScrollView)
//    HomeScrollView mScrollView;
//    @BindView(R.id.recycler_add)
//    RecyclerView recyclerAdd;
    private List<Fragment> mFragments = new ArrayList<>();
    ViewPagerAdapter mAdapter;
    static int itemType;
    CenterLayoutManager manager;
    int layoutTop;

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
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
        return R.layout.fragment_home;
    }

    @Override
    public void initUI() {
//        refreshView.setOnRefreshListener(() -> {
//            refreshData();
//            refreshView.setRefreshing(false);
//        });
        head.setOnClickListener(v -> {
            Intent it = new Intent(getActivity(), CanvasLargerActivity.class);
            startActivityForResult(it, ACTION_REQUEST_EDITIMAGE);
        });
//        mScrollView.setOnScrollListener(new HomeScrollView.OnScrollMoveListener() {
//            @Override
//            public void moveListener(int scrollY) {
////                if (scrollY >= recyclerHome.getTop()) {
////                    if (recyclerAdd == null) {
//////                        showSuspend();
////                    }
////                } else if (scrollY <= recyclerHome.getBottom()) {
////                    if (recyclerAdd != null) {
////                        removeSuspend();
////                    }
////                }
//            }
//
//            @Override
//            public void touch() {
//                layoutTop = recyclerHome.getTop();
//            }
//        });
    }

//    private void removeSuspend() {
//        recyclerAdd.setVisibility(View.GONE);
//    }

    private void showSuspend() {
        //添加recycler
//        recyclerAdd.setVisibility(View.VISIBLE);
//        recyclerAdd.setLayoutManager(manager);
//        recyclerAdd.setAdapter(adapter);
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) recyclerAdd.getLayoutParams();
//        params.gravity = Gravity.TOP;
//        recyclerAdd.setLayoutParams(params);
    }

    private void initAdapter() {
        manager = new CenterLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerHome.setLayoutManager(manager);
        adapter = new BaseQuickAdapter<BigTemplate, BaseViewHolder>(R.layout.item_tab_home, lists) {
            @Override
            protected void convert(BaseViewHolder holder, BigTemplate item) {
                holder.setIsRecyclable(false);
                Button button = holder.getView(R.id.btn_tab);
                button.setText(item.getTypename());
                if (templatePosition == -1) {
                    if (holder.getAdapterPosition() == 0) {
                        button.setTextColor(Color.WHITE);
                        button.setBackground(getResources().getDrawable(R.drawable.button_orange));
                        viewpagerHome.setCurrentItem(0);
                        itemType = item.getType();
                    }
                } else {
                    if (templatePosition == holder.getAdapterPosition()) {
                        button.setTextColor(Color.WHITE);
                        button.setBackground(getResources().getDrawable(R.drawable.button_orange));
                    } else {
                        button.setTextColor(getResources().getColor(R.color.text_bg));
                        button.setBackground(getResources().getDrawable(R.drawable.button_gray));
                    }
                }
                button.setOnClickListener(v -> {
                    int dx = holder.getAdapterPosition() - templatePosition;
//                    moveRecycler(dx);
                    manager.smoothScrollToPosition(recyclerHome, new RecyclerView.State(), holder.getAdapterPosition());
                    itemType = item.getType();
                    templatePosition = holder.getAdapterPosition();
                    viewpagerHome.setCurrentItem(holder.getAdapterPosition());
                    mAdapter.notifyDataSetChanged();
//                    ViewPagerHomeFragment.refreshData();
                    notifyDataSetChanged();
                });
            }
        };
        recyclerHome.setAdapter(adapter);
    }

    /**
     * @Author lixh
     * @Date 2020/11/3 19:55
     * @Description: 刷新数据
     */
    private void refreshData() {
        templateAdapter.setNewData(lists);
//        List mainList = LitePal.where("online = 2 and type = ?", String.valueOf(itemType))
//                .order("createtime desc").find(MainLogoBean.class);
//        viewpagerHome.setCurrentItem(itemType);
//        ViewPagerHomeFragment.mapMainList.put(itemType, mainList);
//        ViewPagerHomeFragment.refreshData();
//        mAdapter.notifyDataSetChanged();
//        viewpagerHome.setAdapter(mAdapter);
        if (ObjectUtils.isNotEmpty(tipDialog)) {
            tipDialog.dismiss();
        }
    }

    @Override
    public void initData() {
//        ed_search_content.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable content) {
//                if (StringUtils.isNotBlank(content)) {
//                    mIvDelContent.setVisibility(View.VISIBLE);
//                } else {
//                    mIvDelContent.setVisibility(View.GONE);
//                }
//            }
//        });
//        // 回车事件监听
//        ed_search_content.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
//            //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
//            if ((event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
//                research();
//                return true;
//            }
//            return false;
//        });
//        mIvDelContent.setOnClickListener(v -> {
//            ed_search_content.setText("");
//        });
//        tipDialog = new QMUITipDialog.Builder(getActivity())
//                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
//                .setTipWord("加载中..")
//                .create();
//        tipDialog.setCanceledOnTouchOutside(false);
//        String device_id = CommonUtils.getDevice_id();
//        Map<String, Object> map = produceReqArg.generateObj(device_id);
//        map.put("device_id", device_id);
//        map.put("page", "0");
//        map.put("limit", "500");
//        // 搜索模板
//        tv_seek_start.setOnClickListener(v -> {
//            research();
//        });
//        map.put("page", "0");
//        tv_querys.setOnClickListener(v -> {
//            LitePal.deleteAll(MainLogoBean.class);
//            LitePal.deleteAll(LogoBean.class);
//            getPresenter().queryAllTempData(map);
//        });
        queryOnlieData();
        newFragment();
        initTemplate();
    }

    private void newFragment() {
        mFragments.add(ViewPagerHomeFragment.newInstance("节日"));
        mFragments.add(ViewPagerHomeFragment.newInstance("环保"));
        mFragments.add(ViewPagerHomeFragment.newInstance("宣传"));
        mFragments.add(ViewPagerHomeFragment.newInstance("简约"));
        mFragments.add(ViewPagerHomeFragment.newInstance("社团"));
        mFragments.add(ViewPagerHomeFragment.newInstance("卡通"));
    }

    private void importData() {
        Gson gson = new Gson();
        String json = CommonUtils.getJson(getActivity(), "muban.json");
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        Type type = new TypeToken<List<MainLogoBean>>() {
        }.getType();
        List<MainLogoBean> list = gson.fromJson(array, type);
        for (MainLogoBean bean : list) {
            List<LogoBean> lists = bean.list;
            switch (bean.getPreViewPath()) {
                case "newimg_13":
                    break;
                case "newimg_12":
                    break;
                case "newimg_11":
                    break;
                case "newimg_10":
                    break;
                case "newimg_9":
                    break;
                case "newimg_8":
                    break;
                case "newimg_7":
                    break;
                case "newimg_6":
                    break;
                case "newimg_5":
                    break;
                case "newimg_4":
                    break;
                case "newimg_3":
                    break;
                case "newimg_2":
                    break;
                case "newimg_1":
                    break;
            }
            bean.save();
            for (LogoBean item : lists) {
                item.setFid(bean.getId());
                item.save();
            }
        }
    }

    /**
     * @Author lixh
     * @Date 2020/11/9 10:45
     * @Description: 搜索模板
     */
    private void research() {
        String content = ed_search_content.getText().toString().trim();
        if (ObjectUtils.isEmpty(content)) {
            ToastUtils.showShort("请您输入文字");
            return;
        }
        Intent intent = new Intent(getActivity(), LogoListActivity.class);
        intent.putExtra(Constants.IS_SEARCH, true);
        intent.putExtra(com.zlfcapp.poster.Constants.TITLE_NAME, content + " 搜索结果");
        intent.putExtra(Constants.SEARCH_CONTENT, content);
        startActivity(intent);
    }

    /**
     * @Author lixh
     * @Date 2020/11/4 15:05
     * @Description: 查询线上数据
     * 在用户打开APP 第一次与第二次间隔时间大于三天时。重新拉取
     * 第一次默认值为0 也要拉取
     */
    private void queryOnlieData() {
        String device_id = CommonUtils.getDevice_id();
        Map<String, Object> map = produceReqArg.generateObj(device_id);
//        if (CommonUtils.isQueryData()) {
//            tipDialog.show();
//            map.put("device_id", device_id);
//            map.put("page", "0");
//
//            map.put("limit", "40");
//            // 查询字体数据
//            getPresenter().subQueryFontList(map);
//            Map<String, Object> mapImage = produceReqArg.generateObj(device_id);
//            mapImage.put("device_id", device_id);
//            mapImage.put("type", "节日");
//            // 查询背景图片素材
//            getPresenter().subQueryImgList(mapImage);
//
//        }
        Map<String, Object> mapImage = produceReqArg.generateObj(device_id);
        mapImage.put("device_id", device_id);
        mapImage.put("type", "节日");        // 查询背景图片素材
        getPresenter().subQueryImgList(mapImage);

//        map.put("device_id", device_id);
//        map.put("page", "0");
//        map.put("limit", "600");
//        // 查询所有模板数据
//        getPresenter().queryAllTempData(map);
    }


    /**
     * @Author lixh
     * @Date 2020/10/26 19:47
     * @Description: 初始化模板
     */
    private void initTemplate() {
        mAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(),mFragments);
        viewpagerHome.setAdapter(mAdapter);
        viewpagerHome.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                templatePosition = position;
                manager.smoothScrollToPosition(recyclerHome, new RecyclerView.State(), position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        for (int i = 0; i < 6; i++) {
            BigTemplate bigTemplate = new BigTemplate();
            bigTemplate.setType(i);
            switch (i) {
                case 0:
                    bigTemplate.setType(0);
                    bigTemplate.setTypename("节日");
                    break;
                case 1:
                    bigTemplate.setType(1);
                    bigTemplate.setTypename("环保");
                    break;
                case 2:
                    bigTemplate.setType(2);
                    bigTemplate.setTypename("宣传");
                    break;
                case 3:
                    bigTemplate.setType(3);
                    bigTemplate.setTypename("简约");
                    break;
                case 4:
                    bigTemplate.setType(4);
                    bigTemplate.setTypename("社团");
                    break;
                case 5:
                    bigTemplate.setType(5);
                    bigTemplate.setTypename("卡通");
                    break;
            }
            lists.add(bigTemplate);
        }
        //海报logo界面
        initAdapter();
    }

    @Override
    public HomePresenter createPresenter() {
        return new HomePresenter(getApp());
    }

    @Override
    public void queryImgList(List<ImageResult> list) {
        LitePal.deleteAll(ImageResult.class, "type=?", "节日");
        if (ObjectUtils.isNotEmpty(list)) {
            for (ImageResult imageResult : list) {
                imageResult.save();
            }
        }
//
//        ViewPagerHomeFragment.setData(list.get(0).getType());
        EventBus.getDefault().post("节日");
        mAdapter.notifyDataSetChanged();

//        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void queryFont(List<TypeFace> list) {
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                TypeFace item = list.get(i);
                TypeFace typeFace = LitePal.where("fontname = ?", item.getFontname()).findFirst(TypeFace.class);
                if (ObjectUtils.isNotEmpty(typeFace)) {
                    continue;
                } else {
                    item.setOnlineid(item.getId());
                    item.save();
                }
            }
        }
    }

    /**
     * @Author lixh
     * @Date 2020/11/5 19:16
     * @Description: 查询所有模板数据返回值 list
     */
    @Override
    public void queryAllTemplate(List<LogoTemplate> list) {
        String json = CommonUtils.getJson(getActivity(), "template.json");
        Gson gson = new GsonBuilder().create();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        Type type = new TypeToken<List<TemplateCount>>() {
        }.getType();
        List<TemplateCount> titleList = gson.fromJson(array, type);
        if (!list.isEmpty()) {
            LitePal.deleteAll(MainLogoBean.class, "online = 2 ");
            LitePal.deleteAll(LogoBean.class, "online = 2 ");
        }
        for (int i = 0; i < list.size(); i++) {
            LogoTemplate item = list.get(i);
            if (item.getTextfontid() == 0 || ObjectUtils.isEmpty(item.getRemark())) {
                continue;
            }
            TemplateCount templatecount = titleList.get(i);
            MainLogoBean mainLogoBean = new MainLogoBean();
            mainLogoBean.setName(templatecount.getTitleName());
            mainLogoBean.setCreatetime(new Date());
            mainLogoBean.setState(1);
            mainLogoBean.setItemBgColor("-1");
            mainLogoBean.setOnline(2);
            if (0 <= i && i <= 80) {
                mainLogoBean.setType(1);
            } else if (81 <= i && i <= 160) {
                mainLogoBean.setType(2);
            } else if (161 <= i && i <= 240) {
                mainLogoBean.setType(3);
            } else if (241 <= i && i <= 320) {
                mainLogoBean.setType(4);
            } else if (321 <= i && i <= 400) {
                mainLogoBean.setType(5);
            } else {
                mainLogoBean.setType(6);
            }
            mainLogoBean.save();
            LogoBean logoBean = new LogoBean();
            logoBean.setFid(mainLogoBean.getId());
            logoBean.setSvgUrl(item.getSvgurl());
            logoBean.setPngUrl(item.getRemark());
            logoBean.setSvgName(item.getSvgname());
            logoBean.setLeftRect(0);
            logoBean.setTopRect(0 - 1000);
            logoBean.setRightRect(500);
            logoBean.setBottomRect(0);
            logoBean.setLeftRectF(270.0f);
            logoBean.setTopRectF(375.0f);
            logoBean.setRightRectF(810.0f);
            logoBean.setBottomRectF(780.0f);
            logoBean.setType(item.getType());
            logoBean.setOnline(2);
            logoBean.setCreatetime(new Date());
            logoBean.setText("LOGO 设计");
            logoBean.setTextAlpha(255);
            logoBean.setTextfontid(item.getTextfontid());
            logoBean.setTextSize(129.0f);
            logoBean.setLeftRect(520);
            logoBean.setTopRect(995);
            logoBean.setBgcolor(item.getBgcolor());
            logoBean.save();
        }
//        refreshData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ObjectUtils.isNotEmpty(tipDialog)) {
            tipDialog.dismiss();
        }
    }

    @Override
    public void onError(int code, String message) {
        // 只要是网络请求异常  就关闭dialog
        ToastUtils.showShort(message);
        if (ObjectUtils.isNotEmpty(tipDialog)) {
            tipDialog.dismiss();
        }
        SPUtils.getInstance().put(com.zlfcapp.poster.Constants.QUERY_ONLINE_DATA, 0L);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> fragmentList;
        public ViewPagerAdapter(FragmentManager fm,List<Fragment>fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
    }

}
