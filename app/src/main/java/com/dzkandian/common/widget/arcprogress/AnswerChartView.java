package com.dzkandian.common.widget.arcprogress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.dzkandian.R;


/**
 * 弧线对比图
 *
 * @param
 * @author Dirian
 * @return
 * @data 2018年12月11日 下午6:17:34
 **/
public class AnswerChartView extends AppCompatImageView {

    // 圆画笔
    private Paint mCirclePaint;
    // 圆环画笔
    private Paint mRingPaint;
    // 百分数画笔
    private Paint mTextPaint;
    // 文本画笔
    private Paint mTextPaint2;
    // 里面圆颜色
    private int mCircleColor;
    // 里面弧颜色
    private int mInnerRingColor;
    // 外面弧颜色
    private int mOutRingColor;
    // 空白的圆半径
    private float mRadius;
    // 里面的弧半径
    private float mRingRadius;
    // 最外(进度条)弧半径
    private float mRingRadius2;
    // 最外弧半径
    private float mRingRadius3;
    // 圆环的宽度
    private float mStrokeWidth;
    // 进度圆环的宽度
    private float mProgressWidth;
    // 文本的中心x轴位置
    private int mXCenter;
    // 文本的中心y轴位置
    private int mYCenter;
    // 总成绩
    private final int mTotalProgress = 100;
    // 个人的正确率
    private double mInnerProgress = 0;
    // 班级的正确率
    private double mOutProgress;

    private Bitmap mResource;
    private Bitmap mTarget;

    public AnswerChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initVariable();
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.AnswerChartView, 0, 0);
        mRadius = typeArray.getDimension(R.styleable.AnswerChartView_radiusView, 80);
        mStrokeWidth = typeArray.getDimension(R.styleable.AnswerChartView_strokeWidth, 10);
        mProgressWidth = typeArray.getDimension(R.styleable.AnswerChartView_progressWidth, 10);
        mCircleColor = typeArray.getColor(R.styleable.AnswerChartView_circleColor, 0xFFFFFFFF);
        mOutRingColor = typeArray.getColor(R.styleable.AnswerChartView_outringColor, 0xFFFFFFFF);
        mInnerRingColor = typeArray.getColor(R.styleable.AnswerChartView_innerringColor, 0xFFFFFFFF);

        mResource = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_reward_read);


        mRingRadius = mRadius + mStrokeWidth / 2;
        mRingRadius2 = mRadius + mStrokeWidth + mProgressWidth / 2;
        mRingRadius3 = mRadius + mStrokeWidth / 2 * 3 + mProgressWidth;

        mTarget = Bitmap.createScaledBitmap(mResource, (int) mRadius * 2, (int) mRadius * 2, true);

//        float viewWidth = DeviceUtils.dpToPixel(context, 80);
//        Timber.d("=============控件的半径：" + viewWidth);
//        Timber.d("=============圆环的宽度：" + mStrokeWidth);
//        Timber.d("=============圆的半径：" + mRadius);
//        Timber.d("=============内弧的半径：" + mRingRadius);
//        Timber.d("=============进度条弧的半径：" + mRingRadius2);
//        Timber.d("=============外弧的半径：" + mRingRadius3);

        typeArray.recycle();
    }

    public void setImageSrc(Context context, int drawable) {
        mResource = BitmapFactory.decodeResource(context.getResources(), drawable);
        mTarget = Bitmap.createScaledBitmap(mResource, (int) mRadius * 2, (int) mRadius * 2, true);

    }


    private void initVariable() {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
        mCirclePaint.setStyle(Paint.Style.FILL);


        mRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRingPaint.setAntiAlias(true);
        mRingPaint.setDither(true);
        mRingPaint.setColor(mInnerRingColor);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        mXCenter = getWidth() / 2;
        mYCenter = getHeight() / 2;
//        Timber.d("=============绘制圆的中心点：" + "x: " + mXCenter + " y: " + mYCenter);

        mCirclePaint.setColor(getResources().getColor(R.color.color_progress_ring_background));
        canvas.drawCircle(mXCenter, mYCenter, mRadius + mProgressWidth * 2, mCirclePaint);
//        Timber.d("=============绘制最外层圆的半径：" + (mRadius + mProgressWidth * 2));

        RectF oval1 = new RectF();
        oval1.left = (mXCenter - mRingRadius);
        oval1.top = (mYCenter - mRingRadius);
        oval1.right = mRingRadius * 2 + (mXCenter - mRingRadius);
        oval1.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
        mRingPaint.setColor(mOutRingColor);
        mRingPaint.setStrokeWidth(mStrokeWidth);
        canvas.drawArc(oval1, -90, 360, false, mRingPaint);
//        Timber.d("=============绘制内弧圆  left：" + oval1.left + " top：" + oval1.left + " right：" + oval1.bottom + " right：" + oval1.bottom);

        canvas.drawCircle(mXCenter, mYCenter, mRadius, mCirclePaint);
        RectF rectF = new RectF(mXCenter - mRadius, mYCenter - mRadius, mXCenter + mRadius, mYCenter + mRadius);
        canvas.drawBitmap(mTarget, null, rectF, mCirclePaint);
//        Timber.d("=============绘制中心圆的半径：" + mRadius);

        RectF oval = new RectF();
        oval.left = (mXCenter - mRingRadius2);
        oval.top = (mYCenter - mRingRadius2);
        oval.right = mRingRadius2 * 2 + (mXCenter - mRingRadius2);
        oval.bottom = mRingRadius2 * 2 + (mYCenter - mRingRadius2);
        mRingPaint.setColor(mInnerRingColor);
        mRingPaint.setStrokeWidth(mProgressWidth);
        BlurMaskFilter maskFilter = new BlurMaskFilter(2, BlurMaskFilter.Blur.SOLID);
        mRingPaint.setMaskFilter(maskFilter);
        canvas.drawArc(oval, -90, ((float) mInnerProgress / mTotalProgress) * 360, false, mRingPaint); //

        RectF oval2 = new RectF();
        oval2.left = (mXCenter - mRingRadius3);
        oval2.top = (mYCenter - mRingRadius3);
        oval2.right = mRingRadius3 * 2 + (mXCenter - mRingRadius3);
        oval2.bottom = mRingRadius3 * 2 + (mYCenter - mRingRadius3);
        mRingPaint.setColor(mOutRingColor);
        mRingPaint.setStrokeWidth(mStrokeWidth);
        canvas.drawArc(oval2, -90, 360, false, mRingPaint); //
//        Timber.d("=============绘制外弧圆  left：" + oval2.left + " top：" + oval2.left + " right：" + oval2.bottom + " right：" + oval2.bottom);
    }

    public void setOutProgress(double progress) {
        mOutProgress = progress;
        postInvalidate();
    }

    public void setInnerProgress(double progress) {
        mInnerProgress = progress;
        //      invalidate();
        postInvalidate();
    }

    public double getProgress() {
        return mInnerProgress;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mResource != null && !mResource.isRecycled())
            mResource.recycle();
        if (mTarget != null && !mTarget.isRecycled())
            mTarget.recycle();
    }

}
