package com.zlfcapp.poster.mvp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.vector.update_app.HttpManager;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.bean.LogoBean;
import com.xinlan.imageeditlibrary.editimage.bean.MainLogoBean;
import com.xinlan.imageeditlibrary.editimage.utils.NetWorkHelper;
import com.zlfcapp.poster.Constants;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.mvp.base.PureActivity;
import com.zlfcapp.poster.utils.CommonUtils;
import com.zlfcapp.poster.utils.StatusTitleUtil;
import com.zlfcapp.poster.utils.produceReqArg;

import org.litepal.LitePal;

import java.util.List;
import java.util.Map;

import butterknife.BindView;


/**
 * @Description: 预览图页面
 * @Author: lixh
 * @CreateDate: 2020/11/4 10:16
 * @Version: 1.0
 */
public class PreviewActivity extends PureActivity {


    @BindView(R.id.recycle_recommend)
    RecyclerView recycle_recommend;

    @BindView(R.id.iv_preview)
    ImageView iv_preview;

    @BindView(R.id.iv_left_return)
    ImageView iv_left_return;

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.line_preview)
    LinearLayout line_preview;

    BaseQuickAdapter mAdapter = null;
    Activity activity;
    List<MainLogoBean> mainList;
    String type;
    int key;
    String svgUrl;
    String svgName;
    LogoBean currLogoBean;

    @Override
    public int getRootViewId() {
        return R.layout.activity_preview;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void initUI() {
        activity = this;
        // 设置状态栏
        StatusTitleUtil.setStatusBarColor(this, R.color.whitefa);
        StatusTitleUtil.StatusBarLightMode(this);
        String name = getIntent().getStringExtra(Constants.TITLE_NAME);
        tv_title.setText(name);
        iv_left_return.setOnClickListener(v -> {
            finish();
        });
        key = getIntent().getIntExtra(Constants.LOGO_KEY_ID, 0);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recycle_recommend.setLayoutManager(layoutManager);
        type = getIntent().getStringExtra(Constants.LOGO_TYPE);
        mainList = LitePal.where("online = 2 and type = ?", type)
                .order("createtime desc").find(MainLogoBean.class);
        mainList = mainList.subList(0, 32);
        mAdapter = new BaseQuickAdapter<MainLogoBean, BaseViewHolder>(R.layout.list_simple_temp, mainList) {
            @Override
            protected void convert(BaseViewHolder holder, MainLogoBean item) {
                holder.setIsRecyclable(false);
                ImageView item_logo = holder.getView(R.id.item_logo);
                List<LogoBean> editList = LitePal.where("fid = ?", String.valueOf(item.getId())).order("createtime asc").find(LogoBean.class);
                for (LogoBean logoBean : editList) {
                    Glide.with(activity).load(logoBean.getPngUrl()).into(item_logo);
                }
            }
        };
        mAdapter.setOnItemClickListener((BaseQuickAdapter adapter, View view, int position) -> {
            MainLogoBean logoBean = mainList.get(position);
            refresh(logoBean.getId());
        });
        // 跳转到编辑页面
        line_preview.setOnClickListener(v -> {
            Map<String, String> map = produceReqArg.generate(CommonUtils.getDevice_id());
            if (!NetworkUtils.isConnected()) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("网络连接异常,请您检查网络")
                        .setCancelable(false).setNegativeButton("确定", (DialogInterface dialog, int id) -> {
                    dialog.cancel();
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return;
            }
            if (ObjectUtils.isEmpty(currLogoBean.getSvgContent())) {
                NetWorkHelper.getInstance().asyncGet(svgUrl, map, new HttpManager.Callback() {
                    @Override
                    public void onResponse(String result) {
                        if (ObjectUtils.isNotEmpty(currLogoBean)) {
                            currLogoBean.setSvgContent(result);
                            currLogoBean.update(currLogoBean.getId());
                            Intent intent = new Intent(activity, EditImageActivity.class);
                            intent.putExtra(com.xinlan.imageeditlibrary.editimage.view.Constants.LOGO_ID, key);
                            intent.putExtra("clear", true);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        ToastUtils.showShort("网络请求错误");
                    }
                });
            } else {
                Intent intent = new Intent(activity, EditImageActivity.class);
                intent.putExtra(com.xinlan.imageeditlibrary.editimage.view.Constants.LOGO_ID, key);
                intent.putExtra("clear", true);
                startActivity(intent);
            }
        });
        mAdapter.bindToRecyclerView(recycle_recommend);
        refresh(key);
    }

    /**
     * @Author lixh
     * @Date 2020/11/4 11:26
     * @Description: 刷新数据
     */
    public void refresh(int keys) {
        key = keys;
        if (keys != 0) {
            List<LogoBean> editList = LitePal.where("fid = ?", String.valueOf(keys)).order("createtime desc").find(LogoBean.class);
            for (LogoBean item : editList) {
                currLogoBean = item;
                String path = item.getPngUrl();
                svgUrl = item.getSvgUrl();
                svgName = item.getSvgName();
                if (ObjectUtils.isNotEmpty(path)) {
                    Glide.with(activity).clear(iv_preview);
                    Glide.with(activity).load(path).into(iv_preview);
                }
            }
        }
        mainList = LitePal.where("online = 2 and type = ?", type).order("createtime desc").find(MainLogoBean.class);
        mainList = mainList.subList(0, 32);
        if (ObjectUtils.isNotEmpty(mAdapter)) {
            mAdapter.setNewData(mainList);
        }
    }
}
