package com.zlfcapp.poster.mvp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xinlan.imageeditlibrary.editimage.bean.ImageResult;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.UserEvent;
import com.zlfcapp.poster.mvp.base.BaseFragment;
import com.zlfcapp.poster.mvp.presenter.HomePresenter;
import com.zlfcapp.poster.mvp.view.IHomeView;
import com.zlfcapp.poster.utils.CommonUtils;
import com.zlfcapp.poster.utils.produceReqArg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

@UserEvent
public class ViewPagerHomeFragment extends Fragment{
    //    private TextView mIndex;
    static BaseQuickAdapter adapter;
    static String type = "节日";
    String getTypename;
    Dialog tipDialog;
    static List<ImageResult> mainList = new ArrayList<>();
//    RecyclerView recyclerViewpager;

    RecyclerView recyclerViewpager;
    static TextView text_tab;
    public static Fragment newInstance(String imgType) {
        ViewPagerHomeFragment fragment = new ViewPagerHomeFragment();
        return fragment;
    }

   public static void setData(String mType){
       mainList.clear();
       mainList.addAll(LitePal.where("type = ?", mType)
               .find(ImageResult.class));
       adapter.notifyDataSetChanged();
       text_tab.setText(mType);
   }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.viewpagerfragment, container, false);
        initList(rootView);
        return rootView;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(List<ImageResult> list) {
        mainList = list;
        recyclerViewpager.setAdapter(adapter);
    }

//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.viewpagerfragment, container, false);
//        initList(rootView);
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }

//        return rootView;
//    }

    public void initList(View rootView) {
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        recyclerViewpager = rootView.findViewById(R.id.recycler_viewpager);
        text_tab = rootView.findViewById(R.id.text_tab);
        text_tab.setText(type);
        recyclerViewpager.setLayoutManager(manager);
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
        recyclerViewpager.setAdapter(adapter);
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


    }



}
