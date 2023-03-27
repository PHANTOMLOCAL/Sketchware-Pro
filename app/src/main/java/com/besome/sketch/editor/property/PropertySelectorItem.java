package com.besome.sketch.editor.property;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sketchware.remod.R;

import a.a.a.Kw;
import a.a.a.aB;
import a.a.a.mB;
import a.a.a.sq;
import a.a.a.DimensionUtils;
import mod.hey.studios.util.Helper;

@SuppressLint("ViewConstructor")
public class PropertySelectorItem extends RelativeLayout implements View.OnClickListener {

    private String key = "";
    private int value = -1;
    private TextView tvName;
    private TextView tvValue;
    private ImageView imgLeftIcon;
    private int icon;
    private View propertyItem;
    private View propertyMenuItem;
    private ViewGroup radioGroupContent;
    private Kw valueChangeListener;

    public PropertySelectorItem(Context context, boolean z) {
        super(context);
        initialize(context, z);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
        int identifier = getResources().getIdentifier(key, "string", getContext().getPackageName());
        if (identifier > 0) {
            tvName.setText(Helper.getResString(identifier));
            switch (this.key) {
                case "property_orientation":
                    icon = R.drawable.grid_3_48;
                    break;

                case "property_text_style":
                    icon = R.drawable.abc_96_color;
                    break;

                case "property_text_size":
                    icon = R.drawable.text_width_96;
                    break;

                case "property_ime_option":
                case "property_input_type":
                    icon = R.drawable.keyboard_48;
                    break;

                case "property_spinner_mode":
                    icon = R.drawable.pull_down_48;
                    break;

                case "property_choice_mode":
                    icon = R.drawable.multiple_choice_48;
                    break;

                case "property_first_day_of_week":
                    icon = R.drawable.monday_48;
                    break;
            }
            if (propertyMenuItem.getVisibility() == VISIBLE) {
                ((ImageView) findViewById(R.id.img_icon)).setImageResource(icon);
                ((TextView) findViewById(R.id.tv_title)).setText(Helper.getResString(identifier));
                return;
            }
            imgLeftIcon.setImageResource(icon);
        }
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        tvValue.setText(sq.a(key, value));
    }

    @Override
    public void onClick(View v) {
        if (!mB.a()) {
            switch (key) {
                case "property_orientation":
                case "property_text_style":
                case "property_text_size":
                case "property_ime_option":
                case "property_input_type":
                case "property_spinner_mode":
                case "property_choice_mode":
                case "property_first_day_of_week":
                    showDialog();
            }
        }
    }

    public void setOnPropertyValueChangeListener(Kw onPropertyValueChangeListener) {
        valueChangeListener = onPropertyValueChangeListener;
    }

    public void setOrientationItem(int orientationItem) {
        if (orientationItem == 0) {
            propertyItem.setVisibility(GONE);
            propertyMenuItem.setVisibility(VISIBLE);
        } else {
            propertyItem.setVisibility(VISIBLE);
            propertyMenuItem.setVisibility(GONE);
        }
    }

    private void initialize(Context context, boolean z) {
        DimensionUtils.inflate(context, this, R.layout.property_selector_item);
        tvName = findViewById(R.id.tv_name);
        tvValue = findViewById(R.id.tv_value);
        imgLeftIcon = findViewById(R.id.img_left_icon);
        propertyItem = findViewById(R.id.property_item);
        propertyMenuItem = findViewById(R.id.property_menu_item);
        if (z) {
            setOnClickListener(this);
            setSoundEffectsEnabled(true);
        }
    }

    private void showDialog() {
        aB dialog = new aB((Activity) getContext());
        dialog.b(tvName.getText().toString());
        dialog.a(icon);
        View view = DimensionUtils.inflate(getContext(), R.layout.property_popup_selector_single);
        radioGroupContent = view.findViewById(R.id.rg_content);
        TextView desc = view.findViewById(R.id.desc);
        if (key.equals("property_ime_option")) {
            desc.setText(Helper.getResString(R.string.property_description_edittext_ime_options));
            desc.setVisibility(VISIBLE);
        } else {
            desc.setVisibility(GONE);
        }
        for (Pair<Integer, String> pair : sq.a(key)) {
            radioGroupContent.addView(getOption(pair));
        }
        for (int i = 0; radioGroupContent.getChildCount() > i; i++) {
            RadioButton radioButton = (RadioButton) radioGroupContent.getChildAt(i);
            if (Integer.parseInt(radioButton.getTag().toString()) == value) {
                radioButton.setChecked(true);
            }
        }
        dialog.a(view);
        dialog.b(Helper.getResString(R.string.common_word_select), v -> {
            for (int i = 0; radioGroupContent.getChildCount() > i; i++) {
                RadioButton radioButton = (RadioButton) radioGroupContent.getChildAt(i);
                if (radioButton.isChecked()) {
                    setValue(Integer.parseInt(radioButton.getTag().toString()));
                }
            }
            if (valueChangeListener != null) {
                valueChangeListener.a(key, value);
            }
            dialog.dismiss();
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private RadioButton getOption(Pair<Integer, String> pair) {
        RadioButton radioButton = new RadioButton(getContext());
        radioButton.setText(pair.second);
        radioButton.setTag(pair.first);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (DimensionUtils.dpToPx(getContext(), 1.0f) * 40.0f));
        radioButton.setGravity(Gravity.CENTER | Gravity.LEFT);
        radioButton.setLayoutParams(layoutParams);
        return radioButton;
    }
}
