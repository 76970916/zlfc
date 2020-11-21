package com.zlfcapp.poster.mvp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.UserEvent;
import com.xinlan.imageeditlibrary.editimage.bean.ImageResult;
import com.zlfcapp.poster.http.HttpClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

@UserEvent
public class ViewPagerHomeFragment extends Fragment {
    //    private TextView mIndex;
     BaseQuickAdapter adapter;
    HttpClient mHttpClient;
    static String type;
    String getTypename;
    Dialog tipDialog;
     List<ImageResult> mainList = new ArrayList<>();
     RecyclerView recyclerViewpager;
    GridLayoutManager manager;

    public static Fragment newInstance(String imgType) {
        ViewPagerHomeFragment fragment = new ViewPagerHomeFragment();
        type = imgType;
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe (threadMode = ThreadMode.MAIN)
    public void Event(String imgType){
        type = imgType;
        mainList.clear();
        mainList.addAll(LitePal.where("type = ?", type)
                .find(ImageResult.class));
        recyclerViewpager.setAdapter(adapter);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.viewpagerfragment, container, false);
        initList(rootView);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        manager = new GridLayoutManager(getActivity(), 3);
        return rootView;
    }

    public void initList(View rootView) {
        recyclerViewpager = rootView.findViewById(R.id.recycler_viewpager);
        recyclerViewpager.setLayoutManager(manager);
        mainList.clear();
        mainList.addAll(LitePal.where("type = ?", type)
                .find(ImageResult.class));
        adapter = new BaseQuickAdapter<ImageResult, BaseViewHolder>(R.layout.list_simple_temp, mainList) {
            @Override
            protected void convert(BaseViewHolder holder, ImageResult item) {
                holder.setIsRecyclable(false);
                ImageView item_logo = holder.getView(R.id.item_logo);
                if (getActivity() != null) {
                    Glide.with(getActivity()).load(item.getUrl()).into(item_logo);
                }
            }
        };
//        List<MainLogoBean> finalMainList = mainList;
//        // 跳转到编辑页面
//        adapter.setOnItemClickListener((BaseQuickAdapter adapter, View view, int position) -> {
//            MainLogoBean itemlogo = finalMainList.get(position);
//            Intent intent = new Intent(getActivity(), PreviewActivity.class);
//            intent.putExtra(Constants.LOGO_KEY_ID, itemlogo.getId());
//            intent.putExtra(Constants.TITLE_NAME, getTypename);
//            intent.putExtra(Constants.LOGO_TYPE, String.valueOf(itemlogo.getType()));
//            startActivity(intent);
//        });
        recyclerViewpager.setAdapter(adapter);
    }


}
