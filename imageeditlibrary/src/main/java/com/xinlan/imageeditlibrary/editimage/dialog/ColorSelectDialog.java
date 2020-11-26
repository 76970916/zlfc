package com.xinlan.imageeditlibrary.editimage.dialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.xinlan.imageeditlibrary.R;

import net.margaritov.preference.colorpicker.ColorPickerPanelView;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import net.margaritov.preference.colorpicker.ColorPickerView;

import java.util.Locale;

public class ColorSelectDialog extends AppCompatDialog implements ColorPickerView.OnColorChangedListener, View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener {
    private ColorPickerView mColorPicker;

    private ColorPickerPanelView mOldColor;
    private ColorPickerPanelView mNewColor;

    private EditText mHexVal;
    private boolean mHexValueEnabled = false;
    private ColorStateList mHexDefaultTextColor;

    private net.margaritov.preference.colorpicker.ColorPickerDialog.OnColorChangedListener mListener;
    private int mOrientation;
    private View mLayout;
    Context mContext;
    private String mTitle;
    private ImageView image_cancel;
    private ImageView image_commit;
    private OnColorPickerListener clickListener;
    private int changeColor = 0;

    public void setOnColorListener(OnColorPickerListener listener) {
        this.clickListener = listener;
    }

    public interface OnColorPickerListener {
        void commitColorClick(int color);
    }

    public ColorSelectDialog(Context context) {
        super(context);
    }

    public ColorSelectDialog(Context context, int theme) {
        super(context, theme);
    }

    protected ColorSelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public ColorSelectDialog(Context context, int initialColor, String title) {
        super(context);
        mContext = context;
        mTitle = title;
        init(initialColor);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == net.margaritov.preference.colorpicker.R.id.new_color_panel) {
            if (mListener != null) {
                mListener.onColorChanged(mNewColor.getColor());
            }
        }
        dismiss();
    }

    private void init(int color) {
        // To fight color banding.
        getWindow().setFormat(PixelFormat.RGBA_8888);

        setUp(color);

    }

    private void setUp(int color) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = inflater.inflate(R.layout.dialog_color_picker, null);
        mLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);
        image_cancel = mLayout.findViewById(R.id.image_cancel);
        image_commit = mLayout.findViewById(R.id.image_commit);
        mOrientation = getContext().getResources().getConfiguration().orientation;
        setContentView(mLayout);

        setTitle(mTitle);

        mColorPicker = (ColorPickerView) mLayout.findViewById(R.id.color_picker_view);
        mOldColor = (ColorPickerPanelView) mLayout.findViewById(R.id.old_color_panel);
        mNewColor = (ColorPickerPanelView) mLayout.findViewById(R.id.new_color_panel);

        mHexVal = (EditText) mLayout.findViewById(R.id.hex_val);
        mHexVal.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mHexDefaultTextColor = mHexVal.getTextColors();
        mHexVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    if (s.length() > 5 || s.length() < 10) {
//                        int c = ColorPickerPreference.convertToColorInt(s.toString());
//                        mColorPicker.setColor(c, true);
//                        mHexVal.setText(s);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mHexVal.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    String s = mHexVal.getText().toString();
                    if (s.length() > 5 || s.length() < 10) {
                        try {
                            int c = ColorPickerPreference.convertToColorInt(s.toString());
                            mColorPicker.setColor(c, true);
                            mHexVal.setTextColor(mHexDefaultTextColor);
                        } catch (IllegalArgumentException e) {
                            mHexVal.setTextColor(Color.RED);
                        }
                    } else {
                        mHexVal.setTextColor(Color.RED);
                    }
                    return true;
                }
                return false;
            }
        });

        ((LinearLayout) mOldColor.getParent()).setPadding(
                Math.round(mColorPicker.getDrawingOffset()),
                0,
                Math.round(mColorPicker.getDrawingOffset()),
                0
        );

        mOldColor.setOnClickListener(this);
        mNewColor.setOnClickListener(this);
        mColorPicker.setOnColorChangedListener(this);
        mOldColor.setColor(color);
        mColorPicker.setColor(color, true);
        image_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        image_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    if (changeColor != 0) {
                        clickListener.commitColorClick(changeColor);
                    }
                }
                dismiss();
            }
        });
        mNewColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    if (changeColor != 0) {
                        clickListener.commitColorClick(changeColor);
                    }
                }
                dismiss();
            }
        });
    }

    @Override
    public void onGlobalLayout() {
        if (getContext().getResources().getConfiguration().orientation != mOrientation) {
            final int oldcolor = mOldColor.getColor();
            final int newcolor = mNewColor.getColor();
            mLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            setUp(oldcolor);
            mNewColor.setColor(newcolor);
            mColorPicker.setColor(newcolor);
        }
    }

    @Override
    public void onColorChanged(int color) {
        mNewColor.setColor(color);
        changeColor = color;
        if (mHexValueEnabled)
            updateHexValue(color);

    }

    public void setHexValueEnabled(boolean enable) {
        mHexValueEnabled = enable;
        if (enable) {
            mHexVal.setVisibility(View.VISIBLE);
            updateHexLengthFilter();
            updateHexValue(getColor());
        } else
            mHexVal.setVisibility(View.GONE);
    }

    public boolean getHexValueEnabled() {
        return mHexValueEnabled;
    }

    private void updateHexLengthFilter() {
        if (getAlphaSliderVisible())
            mHexVal.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
        else
            mHexVal.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
    }

    private void updateHexValue(int color) {
        if (getAlphaSliderVisible()) {
            mHexVal.setText(ColorPickerPreference.convertToARGB(color).toUpperCase(Locale.getDefault()));
        } else {
            mHexVal.setText(ColorPickerPreference.convertToRGB(color).toUpperCase(Locale.getDefault()));
        }
        mHexVal.setTextColor(mHexDefaultTextColor);
    }

    public void setAlphaSliderVisible(boolean visible) {
        mColorPicker.setAlphaSliderVisible(visible);
        if (mHexValueEnabled) {
            updateHexLengthFilter();
            updateHexValue(getColor());
        }
    }

    public boolean getAlphaSliderVisible() {
        return mColorPicker.getAlphaSliderVisible();
    }

    /**
     * Set a OnColorChangedListener to get notified when the color
     * selected by the user has changed.
     *
     * @param listener
     */
    public void setOnColorChangedListener(net.margaritov.preference.colorpicker.ColorPickerDialog.OnColorChangedListener listener) {
        mListener = listener;
    }

    public int getColor() {
        return mColorPicker.getColor();
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt("old_color", mOldColor.getColor());
        state.putInt("new_color", mNewColor.getColor());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mOldColor.setColor(savedInstanceState.getInt("old_color"));
        mColorPicker.setColor(savedInstanceState.getInt("new_color"), true);
    }
}
