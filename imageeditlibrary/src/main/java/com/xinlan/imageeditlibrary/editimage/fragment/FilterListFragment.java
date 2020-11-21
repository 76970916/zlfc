package com.xinlan.imageeditlibrary.editimage.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xinlan.imageeditlibrary.BaseActivity;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.R2;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.bean.ConstantLogo;
import com.xinlan.imageeditlibrary.editimage.bean.EditData;
import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.xinlan.imageeditlibrary.editimage.view.Constants.MODE_FILTER;


/**
 * 滤镜列表fragment
 *
 * @author panyi
 */
public class FilterListFragment extends BaseEditFragment {
    public static final int INDEX = ModuleConfig.INDEX_FILTER;
    public static final String TAG = FilterListFragment.class.getName();
    @BindView(R2.id.recycler_filter)
    RecyclerView recyclerFilter;

    @BindView(R2.id.line_content)
    LinearLayout line_content;

    @BindView(R2.id.line_hint)
    LinearLayout line_hint;

    private Bitmap fliterBit;// 滤镜处理后的bitmap
    private LinearLayout mFilterGroup;// 滤镜列表
    private String[] fliters;
    private Bitmap currentBitmap;// 标记变量
    List<Bitmap> mFilterBitmap;//过滤13张图集合
    boolean getFilterList = false;//获取集合开关
    int filterPosition;
    Handler mHandler;
    BaseQuickAdapter adapter;

    public static FilterListFragment newInstance() {
        FilterListFragment fragment = new FilterListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void filterEvent(EditData data) {
        if (data.getType() == ConstantLogo.INITFILTER) {
            line_hint.setVisibility(View.GONE);
            line_content.setVisibility(View.VISIBLE);
            setListData(data.getBitmap());
        }
    }

    private void setListData(Bitmap bitmap) {
        mFilterBitmap.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (bitmap != null) {
                    for (int i = 0; i < 13; i++) {
                        filterPosition = i;
                        if (i == 0) {// 原始图片效果
//                        activity.imageSpace.setImageBitmap(activity.getMainBit());
                            currentBitmap = bitmap;
                            mFilterBitmap.add(currentBitmap);
                        } else {
                            Bitmap listBitmap = Bitmap.createBitmap(bitmap.copy(
                                    Bitmap.Config.ARGB_8888, true));
                            mFilterBitmap.add(PhotoProcessing.filterPhoto(listBitmap, i));
                        }
                    }
                }
                Message message = new Message();
                message.what = 0;
                mHandler.sendMessage(message);
            }
        }).start();

    }

    @Override
    public void initUI() {
        EventBus.getDefault().register(this);
        mFilterBitmap = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerFilter.setLayoutManager(layoutManager);
        //获取13张过滤图片
        mFilterBitmap.clear();
        initHander();
        initadapter();
        if (activity != null) {
            if (activity.getMainBit() != null) {
                for (int i = 0; i < 13; i++) {
                    filterPosition = i;
                    if (i == 0) {// 原始图片效果
                        currentBitmap = activity.getMainBit();
                        mFilterBitmap.add(currentBitmap);
                    } else {
                        Bitmap listBitmap = Bitmap.createBitmap(activity.getMainBit().copy(
                                Bitmap.Config.ARGB_8888, true));
                        mFilterBitmap.add(PhotoProcessing.filterPhoto(listBitmap, i));
                    }
                }
            }

        }
        recyclerFilter.setAdapter(adapter);
    }

    @SuppressLint("HandlerLeak")
    private void initHander() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 0:
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        };
    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_edit_image_fliter;
    }

    private void initadapter() {
        final String[] filterStr = getResources().getStringArray(R.array.filters);
        adapter = new BaseQuickAdapter<Bitmap, BaseViewHolder>(R.layout.item_fliter_image, mFilterBitmap) {
            @Override
            protected void convert(final BaseViewHolder holder, Bitmap item) {
                ImageView imageView = holder.getView(R.id.image_filter);
                if (getActivity() != null) {
                    Glide.with(getActivity()).load(item).into(imageView);
                }
                int position = holder.getAdapterPosition();
                holder.setText(R.id.text_filter, filterStr[position]);
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition();
                        if (position == 0) {// 原始图片效果
//                            activity.imageSpace.setImageBitmap(activity.getMainBit());
                            currentBitmap = activity.getMainBit();
                            return;
                        }
                        activity.isExit = true;
                        // 滤镜处理
                        ProcessingImage task = new ProcessingImage();
                        task.execute(position);
                    }
                });
            }
        };
    }


    @Override
    public void onShow() {
        activity.mode = MODE_FILTER;
        activity.mFilterListFragment.setCurrentBitmap(activity.getMainBit());
//        if (getActivity() != null) {
//            Glide.with(getActivity()).load(activity.getMainBit()).into(activity.imageSpace);
//        }
        activity.mainImage.setImageBitmap(activity.getMainBit());
//        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
//        activity.mainImage.setScaleEnabled(false);

    }

    /**
     * 返回主菜单
     */
    @Override
    public void backToMain() {

    }

    /**
     * 保存滤镜处理后的图片
     */
    public void applyFilterImage() {
        // System.out.println("保存滤镜处理后的图片");
        if (currentBitmap == activity.getMainBit()) {// 原始图片
            // System.out.println("原始图片");
            backToMain();
            return;
        } else {// 经滤镜处理后的图片
            // System.out.println("滤镜图片");
            activity.changeMainBitmap(currentBitmap, true);
            backToMain();
        }// end if
    }

    /**
     * 装载滤镜
     */
    private void setUpFliters() {
        fliters = getResources().getStringArray(R.array.filters);
        if (fliters == null)
            return;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.leftMargin = 20;
        params.rightMargin = 20;
        mFilterGroup.removeAllViews();
        for (int i = 0, len = fliters.length; i < len; i++) {
            TextView text = new TextView(activity);
            text.setTextColor(Color.WHITE);
            text.setTextSize(20);
            text.setText(fliters[i]);
            mFilterGroup.addView(text, params);
            text.setTag(i);
            text.setOnClickListener(new FliterClick());
        }// end for i
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 选择滤镜效果
     */
    private final class FliterClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            int position = ((Integer) v.getTag()).intValue();
            if (position == 0) {// 原始图片效果
//                activity.imageSpace.setImageBitmap(activity.getMainBit());
                currentBitmap = activity.getMainBit();
                return;
            }
            // 滤镜处理
            ProcessingImage task = new ProcessingImage();
            task.execute(position);
        }
    }// end inner class

    /**
     * 图片滤镜处理任务
     *
     * @author panyi
     */
    private final class ProcessingImage extends AsyncTask<Integer, Void, Bitmap> {
        private Dialog dialog;
        private Bitmap srcBitmap;

        @Override
        protected Bitmap doInBackground(Integer... params) {
            int type = params[0];
            srcBitmap = Bitmap.createBitmap(activity.getMainBit().copy(
                    Bitmap.Config.ARGB_8888, true));
            return PhotoProcessing.filterPhoto(srcBitmap, type);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
            dialog.dismiss();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result == null) {
                return;
            }
            fliterBit = result;
            currentBitmap = fliterBit;
            EditData data = new EditData();
            data.setType(ConstantLogo.FILTER);
            data.setBitmap(fliterBit);
            EventBus.getDefault().post(data);
//            activity.mainImage.setImageBitmap(fliterBit);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = BaseActivity.getLoadingDialog(getActivity(), R.string.handing,
                    false);
            dialog.show();
        }

    }// end inner class

    public Bitmap getCurrentBitmap() {
        return currentBitmap;
    }

    public void setCurrentBitmap(Bitmap currentBitmap) {
        this.currentBitmap = currentBitmap;
    }
}// end class

