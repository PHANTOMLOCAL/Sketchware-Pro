package com.besome.sketch.editor.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sketchware.remod.R;

import a.a.a.DimensionUtils;

public class ColorGroupItem extends RelativeLayout {

    public final TextView b;
    public final ImageView c;

    public ColorGroupItem(Context context) {
        super(context);
        DimensionUtils.inflate(context, this, R.layout.color_picker_grid_item);
        b = findViewById(R.id.tv_color_name);
        c = findViewById(R.id.img_selector);
        setPadding(0, 0, 4, 0);
    }
}
