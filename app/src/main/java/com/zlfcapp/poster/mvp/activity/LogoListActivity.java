package com.zlfcapp.poster.mvp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.ObjectUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xinlan.imageeditlibrary.editimage.bean.LogoBean;
import com.xinlan.imageeditlibrary.editimage.bean.MainLogoBean;
import com.zlfcapp.poster.Constants;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.mvp.base.PureActivity;
import com.zlfcapp.poster.utils.StatusTitleUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * @Description: logo 列表
 * @Author: lixh
 * @CreateDate: 2020/11/3 19:26
 * @Version: 1.0
 */
public class LogoListActivity extends PureActivity {

    @BindView(R.id.refreshView)
    SwipeRefreshLayout refreshView;

    @BindView(R.id.recycle_svg)
    RecyclerView recycle_svg;

    @BindView(R.id.iv_left_return)
    ImageView iv_left_return;

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.line_null)
    LinearLayout line_null;

    BaseQuickAdapter mAdapter = null;
    Activity activity;
    List<MainLogoBean> mainList = new ArrayList<>();
    String type;
    String titleName = null;
    String content = null;
    boolean isSerach = false;

    @Override
    public int getRootViewId() {
        return R.layout.activity_logo_list;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void initUI() {
        activity = this;
        // 设置状态栏
        StatusTitleUtil.setStatusBarColor(this, R.color.whitefa);
        StatusTitleUtil.StatusBarLightMode(this);


        titleName = getIntent().getStringExtra(Constants.TITLE_NAME);
        isSerach = getIntent().getBooleanExtra(com.xinlan.imageeditlibrary.editimage.view.Constants.IS_SEARCH, false);
        content = getIntent().getStringExtra(com.xinlan.imageeditlibrary.editimage.view.Constants.SEARCH_CONTENT);
        type = getIntent().getStringExtra(Constants.LOGO_TYPE);
        tv_title.setText(titleName);
        iv_left_return.setOnClickListener(v -> {
            finish();
        });
        refreshView.setOnRefreshListener(() -> {
            refreshData();
            refreshView.setRefreshing(false);
        });
        LoadIngTask loadIngTask = new LoadIngTask();
        TaskParm parm = new TaskParm();
        parm.isSearch = isSerach;
        parm.contetn = content;
        loadIngTask.execute(parm);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recycle_svg.setLayoutManager(layoutManager);
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

        // 跳转到 预览页面
        mAdapter.setOnItemClickListener((BaseQuickAdapter adapter, View view, int position) -> {
            MainLogoBean logoBean = mainList.get(position);
            Intent intent = new Intent(activity, PreviewActivity.class);
            intent.putExtra(com.zlfcapp.poster.Constants.LOGO_KEY_ID, logoBean.getId());
            intent.putExtra(com.zlfcapp.poster.Constants.TITLE_NAME, titleName);
            intent.putExtra(com.zlfcapp.poster.Constants.LOGO_TYPE, String.valueOf(logoBean.getType()));
            startActivity(intent);
        });
        mAdapter.bindToRecyclerView(recycle_svg);
    }

    /**
     * @Author lixh
     * @Date 2020/11/3 19:30
     * @Description: 刷新数据
     */
    private void refreshData() {
        if (isSerach && ObjectUtils.isNotEmpty(content)) {
            mainList.clear();
            List<MainLogoBean> list = LitePal.where("online = 2 and name like ?", "%" + content + "%").order("createtime desc").find(MainLogoBean.class);
            mainList.addAll(list);
            if (mainList.isEmpty()) {
                line_null.setVisibility(View.VISIBLE);
                refreshView.setVisibility(View.GONE);
            } else {
                mAdapter.setNewData(mainList);
            }
        } else {
            mainList = LitePal.where("online = 2 and type = ?", type).order("createtime desc").find(MainLogoBean.class);
            if (ObjectUtils.isEmpty(mainList) || mainList.isEmpty()) {
            } else {
                mAdapter.setNewData(mainList);
            }
        }
    }

    /**
     * @Author lixh
     * @Date 2020/11/9 11:35
     * @Description: 异步参数
     */
    class TaskParm {
        boolean isSearch;
        String contetn;
    }

    /**
     * @Author lixh
     * @Date 2020/11/9 11:04
     * @Description: 加载数据
     */
    private final class LoadIngTask extends AsyncTask<TaskParm, Void, Boolean> {

        @Override
        protected Boolean doInBackground(TaskParm... params) {
            TaskParm parm = params[0];
            if (parm.isSearch) {
                mainList.clear();
                List<MainLogoBean> list = LitePal.where("online = 2 and name like ?", "%" + parm.contetn + "%").order("createtime desc").find(MainLogoBean.class);
                mainList.addAll(list);
            } else {
                mainList = LitePal.where("online = 2 and type = ?", type).order("createtime desc").find(MainLogoBean.class);
            }
            return true;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onCancelled(Boolean result) {
            super.onCancelled(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (mainList.isEmpty()) {
                line_null.setVisibility(View.VISIBLE);
                refreshView.setVisibility(View.GONE);
            } else {
                mAdapter.setNewData(mainList);
            }
        }
    }

}
