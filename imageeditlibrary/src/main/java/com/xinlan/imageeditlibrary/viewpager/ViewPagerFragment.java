package com.xinlan.imageeditlibrary.viewpager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.bean.ConstantLogo;
import com.xinlan.imageeditlibrary.editimage.bean.EditData;
import com.xinlan.imageeditlibrary.editimage.utils.CustomLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;

import recycler.coverflow.CoverFlowLayoutManger;
import recycler.coverflow.RecyclerCoverFlow;

public class ViewPagerFragment extends Fragment {
    private RecyclerCoverFlow mList;
    Adapter adapter;

    //    private TextView mIndex;
    public static Fragment newInstance() {
        ViewPagerFragment fragment = new ViewPagerFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment, container, false);
        initList(rootView);
        return rootView;
    }

    float x_tmp1;
    float y_tmp1;
    float x_tmp2;
    float y_tmp2;
    private int mCoverFlowPosition = 0;
    @SuppressLint("ClickableViewAccessibility")
    private void initList(View rootView) {
        adapter = new Adapter(getActivity(), false);
        mList = (RecyclerCoverFlow) rootView.findViewById(R.id.list);
//        mIndex = ((TextView)rootView.findViewById(R.id.index));
//        mList.setFlatFlow(true); //平面滚动
        mList.setGreyItem(true); //设置灰度渐变
//        mList.setAlphaItem(true); //设置半透渐变
        mList.setAdapter(adapter);

//        layoutManger.
//        mList.setLayoutManager(linearLayoutManager);

        adapter.setOnClickLstn(new Adapter.onItemClick() {
            @Override
            public void clickItem(int position) {
//                EditData data = new EditData();
//                data.setPosition(position+1);
//                data.setType(ConstantLogo.SELECT_VIEW);
//                EventBus.getDefault().post(data);
            }
        });
        mList.setOnItemSelectedListener(new CoverFlowLayoutManger.OnSelected() {
            @Override
            public void onItemSelected(int position) {
//                mIndex.setText((position+1)+"/"+mList.getLayoutManager().getItemCount());
                EditData data = new EditData();
                data.setPosition(position + 1);
                data.setType(ConstantLogo.SELECT_VIEW);
                EventBus.getDefault().post(data);
            }
        });
    }

}
