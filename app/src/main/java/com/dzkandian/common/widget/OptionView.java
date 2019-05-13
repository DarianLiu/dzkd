package com.dzkandian.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzkandian.R;


/**
 * 选项卡
 * Created by LiuLi on 2017/7/15.
 */
public class OptionView extends RelativeLayout {

    private TextView option_right;
    private TextView option_left;

    public OptionView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.include_option_view, this, true);
        option_left = findViewById(R.id.option_left);
        option_right = findViewById(R.id.option_right);
        ImageView ivNext  = findViewById(R.id.iv_next);


        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.OptionView);
        if (attributes != null) {

            //处理option背景色
            int titleBarBackGround = attributes.getResourceId(R.styleable.
                    OptionView_option_background, R.color.white);
            if (titleBarBackGround != 1) {
                setBackgroundResource(titleBarBackGround);
            }

            //设置左边图片icon
            int leftImageViewDrawable = attributes.getResourceId(R.styleable.
                    OptionView_option_left_ImageView_drawable, -1);
            if (leftImageViewDrawable != -1) {
                Drawable img = context.getResources().getDrawable(leftImageViewDrawable);
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                option_left.setCompoundDrawables(img, null, null, null); //设置左图标
            }

            //处理标题
            //获取文字标题
            String titleText = attributes.getString(R.styleable.OptionView_option_title_text);

            if (!TextUtils.isEmpty(titleText)) {
                option_left.setText(titleText);
            }
            //获取标题字体显示颜色/大小
            int titleTextColor = attributes.getColor(R.styleable.OptionView_option_title_text_color,
                    getResources().getColor(R.color.color_text_title));
            if (titleTextColor != -1) {
                option_left.setTextColor(titleTextColor);
            }

//            设置右边图片icon
            int rightImageViewDrawable = attributes.getResourceId(R.styleable.
                    OptionView_option_right_ImageView_drawable, R.drawable.icon_next);
            if (rightImageViewDrawable != -1) {
                Drawable img = context.getResources().getDrawable(rightImageViewDrawable);
                ivNext.setImageDrawable( img); //设置右图标
            }
            attributes.recycle();
        }
    }

    public void setRightText(String str) {
        option_right.setText(str);
        option_right.setTextSize(14);
    }

    public TextView getRightTextView() {
        return option_right;
    }

    @NonNull
    public String getRightText() {
        return option_right.getText().toString();
    }

    public void setLeftText(String str) {
        option_left.setText(str);
    }

    @NonNull
    public String getLeftText() {
        return option_left.getText().toString();
    }
}