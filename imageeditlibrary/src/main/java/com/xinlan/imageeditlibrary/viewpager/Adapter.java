package com.xinlan.imageeditlibrary.viewpager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xinlan.imageeditlibrary.R;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private Context mContext;
    private int[] mColors = {R.drawable.wechart, R.drawable.bizhiputie, R.drawable.logo, R.drawable.weibo,
            R.drawable.nav_media};

    private onItemClick clickCb;
    private boolean is3D;
    private int clickPosition = -1;
    private boolean click = false;

    public Adapter(Context c, boolean is3D) {
        mContext = c;
        this.is3D = is3D;
    }

    public Adapter(Context c, onItemClick cb, boolean is3D) {
        mContext = c;
        clickCb = cb;
        this.is3D = is3D;
    }

    public void setOnClickLstn(onItemClick cb) {
        this.clickCb = cb;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.layout_item;
        if (is3D) layout = R.layout.layout_item_mirror;
        View v = LayoutInflater.from(mContext).inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(mContext).load(mColors[position]).into(holder.img);
        click = false;
        holder.relativeLayout.setBackground(mContext.getResources().getDrawable(R.drawable.bg_black));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "点击了："+position, Toast.LENGTH_SHORT).show();
                if (clickCb != null) {
                    clickPosition = position;
                    clickCb.clickItem(position);
                    click = true;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mColors.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            relativeLayout = itemView.findViewById(R.id.relative_viewpager_bg);
        }
    }

    interface onItemClick {
        void clickItem(int pos);
    }
}
