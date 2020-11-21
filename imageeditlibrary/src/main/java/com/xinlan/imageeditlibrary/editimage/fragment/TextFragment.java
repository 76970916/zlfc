package com.xinlan.imageeditlibrary.editimage.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ObjectUtils;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.dialog.TextInputDialog;
import com.xinlan.imageeditlibrary.editimage.task.StickerTask;
import com.xinlan.imageeditlibrary.editimage.view.TextStickerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.xinlan.imageeditlibrary.editimage.view.Constants.MODE_NONE;
import static com.xinlan.imageeditlibrary.editimage.view.Constants.MODE_TEXT;


/**
 * 添加文本贴图
 *
 * @author 潘易
 */
public class TextFragment extends BaseEditFragment {
    public static final int INDEX = ModuleConfig.INDEX_ADDTEXT;
    public static final String TAG = AddTextFragment.class.getName();
    private Unbinder mUnbinder;
    private View mainView;
    //    private TextStickerView mTextStickerView;// 文字贴图显示控件
    //文字集合
    private List<TextStickerView> mTextList = new ArrayList<>();
    private int mTextColor = Color.WHITE;
    private InputMethodManager imm;

    private SaveTextStickerTask mSaveTask;
    private EditText mInputText;
    LinearLayout line_edit;
    FrameLayout work_space;

    public static TextFragment newInstance() {
        TextFragment fragment = new TextFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mainView = inflater.inflate(R.layout.add_text, null);
        mUnbinder = ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void initUI() {
    }

    @Override
    public int getRootViewId() {
        return 0;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        mTextStickerView = (TextStickerView) getActivity().findViewById(R.id.text_sticker_panel);
        work_space = getActivity().findViewById(R.id.work_space);
//        edit_text = getActivity().findViewById(R.id.edit_texts);
        line_edit = getActivity().findViewById(R.id.line_edit);
        initclick();
    }

    private void initclick() {
        line_edit.setOnClickListener(v -> {
            createTextStickView();
        });
    }

    private void createTextStickView() {
        TextStickerView edit_text = new TextStickerView(getActivity(), null);
        mTextList.add(edit_text);
        RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        edit_text.setLayoutParams(layoutparams);
        work_space.addView(edit_text);
        //从底部弹出文本输入编辑器
        showInputDialog(edit_text);
    }

    private void showInputDialog(final TextStickerView mTextStickerView) {
        final TextInputDialog dialog = new TextInputDialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        //获取dialog中的eittext
        mInputText = dialog.getEditInput();
        //文本贴图
        mInputText.requestFocus();
        mTextStickerView.setEditText(mInputText);
        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                //mTextStickerView change
                String text = editable.toString().trim();
                mTextStickerView.setText(text);

                //给一个特殊标识，防止textwatcher侦听不到
                String flagt = text + "$";

                //避免出现text为空或者删除掉该textstick点击空白处弹出输入框的尴尬
                //StringUtils.isEquals(flagt)说明text为空或者被删除
                if (!ObjectUtils.equals(flagt, "$")) {
                    mTextStickerView.setOnEditClickListener(v -> {
                        showInputDialog(mTextStickerView);
                        mInputText.setText(mTextStickerView.getmText());
                    });
                } else {
                    mTextStickerView.setOnEditClickListener(new TextStickerView.OnEditClickListener() {
                        @Override
                        public void onEditClick(View v) {
                            return;
                        }
                    });
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
        });

        //如果用户未输入任何字符，则textwatch监听不到，防止点击无反应
        if (mTextStickerView.getmText().equals(getResources().getString(R.string.input_hint))) {
            mTextStickerView.setOnEditClickListener(new TextStickerView.OnEditClickListener() {
                @Override
                public void onEditClick(View v) {
                    //点中编辑框
                    showInputDialog(mTextStickerView);
                }
            });
        }

        dialog.show();
        Window window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public void hideInput() {
        if (getActivity() != null && getActivity().getCurrentFocus() != null && isInputMethodShow()) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public boolean isInputMethodShow() {
        return imm.isActive();
    }


    /**
     * 返回主菜单
     */
    @Override
    public void backToMain() {
        hideInput();
        activity.mode = MODE_NONE;
        activity.mainImage.setVisibility(View.VISIBLE);
//        mTextStickerView.setVisibility(View.GONE);
    }

    @Override
    public void onShow() {
        activity.mode = MODE_TEXT;
        activity.mainImage.setImageBitmap(activity.getMainBit());
//        mTextStickerView.setVisibility(View.VISIBLE);
    }

    /**
     * 保存贴图图片
     */
    public void applyTextImage() {
        if (mSaveTask != null) {
            mSaveTask.cancel(true);
        }

        //启动任务
        mSaveTask = new SaveTextStickerTask(activity);
        mSaveTask.execute(activity.getMainBit());
    }

    /**
     * 文字合成任务
     * 合成最终图片
     */
    private final class SaveTextStickerTask extends StickerTask {

        public SaveTextStickerTask(EditImageActivity activity) {
            super(activity);
        }

        @Override
        public void handleImage(Canvas canvas, Matrix m) {
            float[] f = new float[9];
            m.getValues(f);
            int dx = (int) f[Matrix.MTRANS_X];
            int dy = (int) f[Matrix.MTRANS_Y];
            float scale_x = f[Matrix.MSCALE_X];
            float scale_y = f[Matrix.MSCALE_Y];
            canvas.save();
            canvas.translate(dx, dy);
            canvas.scale(scale_x, scale_y);
            //System.out.println("scale = " + scale_x + "       " + scale_y + "     " + dx + "    " + dy);
//            mTextStickerView.drawText(canvas, mTextStickerView.layout_x,
//                    mTextStickerView.layout_y, mTextStickerView.mScale, mTextStickerView.mRotateAngle);
            canvas.restore();
        }

        @Override
        public void onPostResult(Bitmap result) {
//            mTextStickerView.clearTextContent();
//            mTextStickerView.resetView();

            activity.changeMainBitmap(result, true);
            backToMain();
        }
    }//end inner class

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSaveTask != null && !mSaveTask.isCancelled()) {
            mSaveTask.cancel(true);
        }
        if (mUnbinder != null)
            mUnbinder.unbind();
    }
}
