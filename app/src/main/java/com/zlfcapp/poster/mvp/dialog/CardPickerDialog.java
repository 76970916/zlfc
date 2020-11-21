package com.zlfcapp.poster.mvp.dialog;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zlfcapp.poster.R;
import com.zlfcapp.poster.utils.ThemeHelper;

/**
 * @author xyczero
 * @time 16/5/29
 */
public class CardPickerDialog extends DialogFragment implements View.OnClickListener {
    public static final String TAG = "CardPickerDialog";
    ImageView[] mCards = new ImageView[8];
    Button mConfirm;
    Button mCancel;

    private int mCurrentTheme;
    private ClickListener mClickListener;
    private OnColorChange mOnColorChange;

    private int mBeforeTheme;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_AppCompat_Dialog_Alert);
        mCurrentTheme = ThemeHelper.getTheme(getActivity());
        mBeforeTheme = mCurrentTheme;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_theme_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCancel = (Button) view.findViewById(android.R.id.button2);
        mConfirm = (Button) view.findViewById(android.R.id.button1);
        mCards[0] = (ImageView) view.findViewById(R.id.theme_pink);
        mCards[1] = (ImageView) view.findViewById(R.id.theme_purple);
        mCards[2] = (ImageView) view.findViewById(R.id.theme_blue);
        mCards[3] = (ImageView) view.findViewById(R.id.theme_green);
        mCards[4] = (ImageView) view.findViewById(R.id.theme_green_light);
        mCards[5] = (ImageView) view.findViewById(R.id.theme_yellow);
        mCards[6] = (ImageView) view.findViewById(R.id.theme_orange);
        mCards[7] = (ImageView) view.findViewById(R.id.theme_red);
        setImageButtons(mCurrentTheme);
        for (ImageView card : mCards) {
            card.setOnClickListener(this);
        }
        mCancel.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.button1:
                if (mClickListener != null) {
                    mClickListener.onConfirm(mCurrentTheme);
                }
                dismiss();
                break;
            case android.R.id.button2:
                if (mClickListener != null) {
                    mClickListener.onConfirm(mBeforeTheme);
                }
                dismiss();
                break;
            case R.id.theme_pink:
                mCurrentTheme = ThemeHelper.CARD_SAKURA;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_purple:
                mCurrentTheme = ThemeHelper.CARD_HOPE;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_blue:
                mCurrentTheme = ThemeHelper.CARD_STORM;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_green:
                mCurrentTheme = ThemeHelper.CARD_WOOD;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_green_light:
                mCurrentTheme = ThemeHelper.CARD_LIGHT;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_yellow:
                mCurrentTheme = ThemeHelper.CARD_THUNDER;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_orange:
                mCurrentTheme = ThemeHelper.CARD_SAND;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_red:
                mCurrentTheme = ThemeHelper.CARD_FIREY;
                setImageButtons(mCurrentTheme);
                break;
            default:
                break;
        }
        if (mOnColorChange != null) {
            mOnColorChange.OnColorChange(mCurrentTheme, v);
        }
    }

    private void setImageButtons(int currentTheme) {
        if (mClickListener != null) {
            mClickListener.onConfirm(mCurrentTheme);
        }
        mCards[0].setSelected(currentTheme == ThemeHelper.CARD_SAKURA);
        mCards[1].setSelected(currentTheme == ThemeHelper.CARD_HOPE);
        mCards[2].setSelected(currentTheme == ThemeHelper.CARD_STORM);
        mCards[3].setSelected(currentTheme == ThemeHelper.CARD_WOOD);
        mCards[4].setSelected(currentTheme == ThemeHelper.CARD_LIGHT);
        mCards[5].setSelected(currentTheme == ThemeHelper.CARD_THUNDER);
        mCards[6].setSelected(currentTheme == ThemeHelper.CARD_SAND);
        mCards[7].setSelected(currentTheme == ThemeHelper.CARD_FIREY);
    }

    public void setClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setOnColorChange(OnColorChange onColorChange) {
        this.mOnColorChange = onColorChange;
    }

    public interface ClickListener {
        void onConfirm(int currentTheme);
    }

    public interface OnColorChange {
        void OnColorChange(int currentTheme, View view);
    }
}
