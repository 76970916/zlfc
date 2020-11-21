package com.xinlan.imageeditlibrary.editimage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.dialog.ColorSelectDialog;
import com.xinlan.imageeditlibrary.editimage.fragment.PaintFragment;


/**
 * 颜色列表Adapter
 *
 * @author panyi
 */
public class ColorListAdapter extends RecyclerView.Adapter<ViewHolder> {
    public static final int TYPE_COLOR = 1;
    public static final int TYPE_MORE = 2;
    public Context context;
    private int photoPosition = -1;
    public interface IColorListAction{
        void onColorSelected(final int position, final int color);
        void onMoreSelected(final int position);
        void onColorFree(int Color);
    }

    private PaintFragment mContext;
    private int[] colorsData;

    private IColorListAction mCallback;


    public ColorListAdapter(Context context,PaintFragment frg, int[] colors, IColorListAction action) {
        super();
        this.context = context;
        this.mContext = frg;
        this.colorsData = colors;
        this.mCallback = action;
    }

    public class ColorViewHolder extends ViewHolder {
        View colorPanelView;
        View addLayout;
        View relativeLayout;
        public ColorViewHolder(View itemView) {
            super(itemView);
            this.colorPanelView = itemView.findViewById(R.id.color_frame);
            this.addLayout = itemView.findViewById(R.id.relative_add);
            this.relativeLayout = itemView.findViewById(R.id.relative_bg);
        }
    }// end inner class

    public class MoreViewHolder extends ViewHolder {
        View moreBtn;
        public MoreViewHolder(View itemView) {
            super(itemView);
            this.moreBtn = itemView.findViewById(R.id.color_panel_more);
        }

    }//end inner class

    @Override
    public int getItemCount() {
        return colorsData.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return colorsData.length == position ? TYPE_MORE : TYPE_COLOR;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        ViewHolder viewHolder = null;
        if (viewType == TYPE_COLOR) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_color_image, parent,false);
            viewHolder = new ColorViewHolder(v);
        }
        else if (viewType == TYPE_MORE) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_color_more_panel,parent,false);
            viewHolder = new MoreViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if(type == TYPE_COLOR){
            onBindColorViewHolder((ColorViewHolder)holder,position);
        }
//        else if(type == TYPE_MORE){
//            onBindColorMoreViewHolder((MoreViewHolder)holder,position);
//        }
    }

    private void onBindColorViewHolder(final ColorViewHolder holder,final int position){
        holder.setIsRecyclable(false);
        if (holder.getAdapterPosition() == colorsData.length - 1) {
            holder.colorPanelView.setVisibility(View.GONE);
          holder.addLayout.setVisibility(View.VISIBLE);
        }
        holder.colorPanelView.setBackgroundColor(colorsData[position]);
        holder.colorPanelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoPosition = holder.getAdapterPosition();
                if(mCallback!=null){
                    mCallback.onColorSelected(position,colorsData[position]);
                }
                notifyDataSetChanged();
            }
        });
        if (photoPosition != -1) {
            if (photoPosition == holder.getAdapterPosition()) {
                holder.relativeLayout.setVisibility(View.VISIBLE);
            } else {
                holder.relativeLayout.setVisibility(View.GONE);
            }
        }

        holder.addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorSelectDialog colorSelectDialog = new ColorSelectDialog(context, context.getResources().getColor(R.color.green), "颜色自定义");
                colorSelectDialog.setAlphaSliderVisible(true);
                colorSelectDialog.setHexValueEnabled(true);
                colorSelectDialog.setOnColorListener(new ColorSelectDialog.OnColorPickerListener() {
                    @Override
                    public void commitColorClick(int color) {
                        if (mCallback != null) {
                            mCallback.onColorFree(color);
                        }
                    }
                });
                colorSelectDialog.show();
            }
        });
    }

//    private void onBindColorMoreViewHolder(final MoreViewHolder holder,final int position){
//        holder.setIsRecyclable(false);
//        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mCallback!=null){
//                    mCallback.onMoreSelected(position);
//                }
//            }
//        });
//    }

}// end class
