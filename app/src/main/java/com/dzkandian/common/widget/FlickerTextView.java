package com.dzkandian.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2019/1/29.
 * TextView 实现字体闪烁；
 * 使用点：新闻详情页的网页加载动画
 */

@SuppressLint("AppCompatCustomView")
public class FlickerTextView extends TextView {
    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private Paint mPaint;
    private int mViewWidth = 0;
    private int mTranslate = 0;
    private boolean mAnimating = true;

    public FlickerTextView(Context context) {
        super(context);
    }

    public FlickerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewWidth == 0) {
            mViewWidth = getMeasuredWidth();
            if (mViewWidth > 0) {
                mPaint = getPaint();
                mLinearGradient = new LinearGradient(
                        -mViewWidth/2,
                        0,
                        mViewWidth/2,
                        0,
                        new int[]{0xffFF8080, 0xffFF8080, 0xffd43c3c, 0xffFF8080,0xffFF8080},
                        new float[]{0.0f, 0.46f, 0.5f, 0.54f, 1.0f},
                        Shader.TileMode.CLAMP);
                mPaint.setShader(mLinearGradient);
                mGradientMatrix = new Matrix();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mAnimating && mGradientMatrix != null) {
            mTranslate += mViewWidth / 30;
            if (mTranslate > mViewWidth) {
                mTranslate = -mViewWidth/2;
            }
            mGradientMatrix.setTranslate(mTranslate, 0);
            mLinearGradient.setLocalMatrix(mGradientMatrix);
            postInvalidateDelayed(50);
        }
    }
}
