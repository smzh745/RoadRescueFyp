package com.road.rescue.app.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;
import com.road.rescue.app.R;

public class CustomMainButtons extends MaterialCardView {

    public CustomMainButtons(@NonNull Context context) {
        super(context);
    }

    public CustomMainButtons(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.custom_mainbutton_layout, this);
        TextView menuText = findViewById(R.id.textView);
        ImageView menuImage = findViewById(R.id.imageView);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomMainButtons);
        menuText.setText(typedArray.getString(R.styleable.CustomMainButtons_maintext));
        menuImage.setImageDrawable(typedArray.getDrawable(R.styleable.CustomMainButtons_mainimage));
        typedArray.recycle();
    }

    public CustomMainButtons(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
