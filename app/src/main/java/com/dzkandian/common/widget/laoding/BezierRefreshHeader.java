package com.dzkandian.common.widget.laoding;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;
import com.dzkandian.R;
import com.dzkandian.common.uitls.StatusBarUtil;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;
import com.scwang.smartrefresh.layout.util.DensityUtil;

/*
 *任务中心，我的模块的下拉刷新新样式
 */
public class BezierRefreshHeader extends InternalAbstract implements RefreshHeader {

    //贝塞尔曲线背景的路径
    protected Path mPath;
    //贝塞尔曲线背景的画笔
    protected Paint mBackPaint;
    //"下拉刷新"字样的画笔
    protected Paint mTextPaint;
    //飞出的红包画笔
    protected Paint mRedPacketPaint;
    //飞出的小金币画笔
    protected Paint mSmallGoldPaint;
    //曲线的高度
    protected float mWaveHeight;
    //未达到刷新高度时的实时高度
    protected float mHeadHeight;
    //大金币的旋转角度
    protected float mRotateAngle;
    //"下拉刷新"字样的宽度
    protected float mTextWidth;

    //未达到一定下拉高度的标志位
    protected boolean mWavePulling = false;
    //已经达到一定下拉高度的标志位
    protected boolean mWaveDraging = false;
    //上拉时的标志位
    protected boolean mRotatePulling = false;

    //大金币的矩阵
    protected Matrix goldMatrix;
    //左边红包的矩阵
    protected Matrix redPacketLeftMatrix;
    //右边红包的矩阵
    protected Matrix redPacketRightMatrix;
    //大金币位图
    protected Bitmap goldCoinBitmap;
    //红包位图
    protected Bitmap redPacketBitmap;
    //经过旋转的左边红包位图
    protected Bitmap dstRedPacketLeft;
    //经过旋转的右边红包位图
    protected Bitmap dstRedPackerRight;
    //小金币位图
    protected Bitmap smallGoldCoinBitmap;

    //红包实时的相对x坐标
    protected float mRedPacketXpoint;
    //红包实时的透明度
    protected int mRedPacketAlpha;

    //释放刷新时的向上平移动画
    protected ValueAnimator waveAnimator;
    //释放刷新后的大金币旋转动画
    protected ValueAnimator rotateAnimator;
    //释放刷新后的红包，小金币飞出动画
    protected ValueAnimator redPacketAnimator;

    private boolean hasSetClicked = false;

    public interface OnFinishListener{
        public void onFinish();
    }

    public OnFinishListener onFinishListener;


    public BezierRefreshHeader(Context context) {
        this(context, null);
        initView();
    }

    public BezierRefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initView();
    }

    public BezierRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setOnFinishListener(OnFinishListener onFinishListener){
        hasSetClicked = true;
        this.onFinishListener = onFinishListener;
    }

    private void initView() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mSpinnerStyle = SpinnerStyle.Scale;
        final View thisView = this;
        mBackPaint = new Paint();
        mBackPaint.setColor(0xffededed);
        mBackPaint.setAntiAlias(true);
        mSmallGoldPaint = new Paint();
        mSmallGoldPaint.setAntiAlias(true);
        mRedPacketPaint = new Paint();
        mRedPacketPaint.setAntiAlias(true);
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.parseColor("#cccccc"));
        mTextPaint.setTextSize(DensityUtil.dp2px(13));
        mTextWidth = mTextPaint.measureText("下拉刷新↓");
        mPath = new Path();
        goldMatrix = new Matrix();
        redPacketLeftMatrix = new Matrix();
        redPacketRightMatrix = new Matrix();
        goldCoinBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_goldcoin);
        redPacketBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_redpacket);
        smallGoldCoinBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_smallgoldcoin);

        redPacketLeftMatrix.postRotate(-30, redPacketBitmap.getWidth() / 2, redPacketBitmap.getHeight() / 2);
        dstRedPacketLeft = Bitmap.createBitmap(redPacketBitmap, 0, 0, redPacketBitmap.getWidth(), redPacketBitmap.getHeight(), redPacketLeftMatrix, true);

        redPacketRightMatrix.postRotate(30, redPacketBitmap.getWidth() / 2, redPacketBitmap.getHeight() / 2);
        dstRedPackerRight = Bitmap.createBitmap(redPacketBitmap, 0, 0, redPacketBitmap.getWidth(), redPacketBitmap.getHeight(), redPacketRightMatrix, true);

        thisView.setMinimumHeight(DensityUtil.dp2px(80));
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        final View thisView = this;
        final int viewWidth = thisView.getWidth();
        final int viewHeight = thisView.getHeight();

        drawCoinAndWave(canvas, viewWidth, viewHeight);
        super.dispatchDraw(canvas);
    }
    /*
     * viewHeight:计算公式DensityUtil.dp2px(dp)+（offset - DensityUtil.dp2px(dp)）*1.6
     * mHeadHeight:恒为DensityUtil.dp2px(dp)
     */

    private void drawCoinAndWave(Canvas canvas, int viewWidth, int viewHeight) {
        float baseHeight = Math.min(mHeadHeight, viewHeight);
        int saveCount = canvas.saveLayer(new RectF(0, 0, viewWidth, viewHeight), mBackPaint, Canvas.ALL_SAVE_FLAG);
        if (mWaveHeight != 0) {
            mPath.reset();
            mPath.lineTo(viewWidth, 0);
            mPath.lineTo(viewWidth, baseHeight);
            mPath.quadTo(viewWidth / 2, baseHeight + mWaveHeight * 2, 0, baseHeight);
            mPath.close();
            canvas.drawPath(mPath, mBackPaint);
        } else {
            canvas.drawRect(0, 0, viewWidth, baseHeight, mBackPaint);
        }

        if (mWavePulling) {
            if (mWaveDraging) {
                canvas.drawText("释放刷新↑", viewWidth / 2 + (-0.5f) * mTextWidth, StatusBarUtil.getStatusBarHeight(getContext()) + DensityUtil.dp2px(20), mTextPaint);
            } else {
                canvas.drawText("下拉刷新↓", viewWidth / 2 + (-0.5f) * mTextWidth, StatusBarUtil.getStatusBarHeight(getContext()) + DensityUtil.dp2px(20), mTextPaint);
            }
            goldMatrix.setTranslate(viewWidth / 2 + goldCoinBitmap.getWidth() * (-0.5f), DensityUtil.dp2px(60));
        } else {
            if (mRotatePulling) {
                canvas.translate(viewWidth / 2 - redPacketBitmap.getWidth() / 2, DensityUtil.dp2px(80) - redPacketBitmap.getHeight() / 2);
                mRedPacketPaint.setAlpha(mRedPacketAlpha);
                canvas.drawBitmap(dstRedPackerRight, mRedPacketXpoint, mRedPacketXpoint * (-1.3f), mRedPacketPaint);
                canvas.drawBitmap(dstRedPacketLeft, mRedPacketXpoint * (-1f), mRedPacketXpoint * (-1.3f), mRedPacketPaint);
                canvas.translate(redPacketBitmap.getWidth() * (0.5f), redPacketBitmap.getHeight() / 2+ goldCoinBitmap.getHeight() / 2 - DensityUtil.dp2px(15) );
                if (mRedPacketXpoint > goldCoinBitmap.getHeight() / 3) {
                    mSmallGoldPaint.setAlpha(mRedPacketAlpha);
                    canvas.drawBitmap(smallGoldCoinBitmap, mRedPacketXpoint * (-1.4f), mRedPacketXpoint * (-1.25f), mSmallGoldPaint);
                    canvas.drawBitmap(smallGoldCoinBitmap, mRedPacketXpoint * (-1.5f), mRedPacketXpoint * (-0.85f), mSmallGoldPaint);
                    canvas.drawBitmap(smallGoldCoinBitmap, mRedPacketXpoint * (-1.3f), mRedPacketXpoint * (-1.65f), mSmallGoldPaint);
                    canvas.drawBitmap(smallGoldCoinBitmap, mRedPacketXpoint * (1.4f), mRedPacketXpoint * (-1.25f), mSmallGoldPaint);
                    canvas.drawBitmap(smallGoldCoinBitmap, mRedPacketXpoint * (1.5f), mRedPacketXpoint * (-0.85f), mSmallGoldPaint);
                    canvas.drawBitmap(smallGoldCoinBitmap, mRedPacketXpoint * (1.3f), mRedPacketXpoint * (-1.65f), mSmallGoldPaint);
                }
                canvas.translate(goldCoinBitmap.getWidth() * (-0.5f), goldCoinBitmap.getHeight() * (-1f) + DensityUtil.dp2px(15));
                goldMatrix.setRotate(mRotateAngle, goldCoinBitmap.getWidth() * 0.5f, goldCoinBitmap.getHeight() * 0.5f);
            } else {
                goldMatrix.setTranslate(viewWidth / 2 + goldCoinBitmap.getWidth() * (-0.5f), DensityUtil.dp2px(80) + mWaveHeight + goldCoinBitmap.getHeight() * (-0.5f));
            }
        }
        mBackPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(goldCoinBitmap, goldMatrix, mBackPaint);
        mBackPaint.setXfermode(null);
        canvas.restoreToCount(saveCount);
        canvas.restore();
        goldMatrix.reset();

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        if (isDragging || mWavePulling) {
            mWavePulling = true;
            mRotatePulling = false;
            mHeadHeight = height;
            mWaveHeight = Math.max(offset - height, 0);
            if (mWaveHeight > DensityUtil.dp2px(60)) {
                mWaveHeight = DensityUtil.dp2px(60);
            }
            if (mWaveHeight > 0) {
                mWaveDraging = true;
            } else {
                mWaveDraging = false;
            }
        }
    }


    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        mWavePulling = false;
        mRotatePulling = false;
        mHeadHeight = height;
        waveAnimator = ValueAnimator.ofFloat(
                mWaveHeight, 0.8f * mWaveHeight, 0.6f * mWaveHeight, 0.4f * mWaveHeight, 0.2f * mWaveHeight, 0
        );
        waveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float curValue = (float) animation.getAnimatedValue();
                if (!mWavePulling) {
                    mWaveHeight = curValue;
                    final View thisView = BezierRefreshHeader.this;
                    thisView.invalidate();
                }
            }
        });
        waveAnimator.setInterpolator(new EasingInterpolator(Ease.QUAD_IN_OUT));
        waveAnimator.setDuration(200);

        rotateAnimator = ValueAnimator.ofFloat(0, 3000);
        rotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRotatePulling = true;
                float rotateAngle = (float) animation.getAnimatedValue();
                mRotateAngle = rotateAngle;
                final View thisView = BezierRefreshHeader.this;
                thisView.invalidate();
            }
        });
        rotateAnimator.setInterpolator(new LinearInterpolator());
        //经测试，这里只能使用setRepeatMode不能使用setRepeatCount,若使用setRepeatCount(-1)会扰乱AnimatorSet的play和before顺序
        rotateAnimator.setRepeatMode(ValueAnimator.RESTART);
        rotateAnimator.setDuration(10000);

        //红包喷出动画
        redPacketAnimator = ValueAnimator.ofFloat(0, 1);
        redPacketAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float redPacketXpoint = (float) (goldCoinBitmap.getHeight() * (float) animation.getAnimatedValue()/1.414);
                mRedPacketXpoint = redPacketXpoint;
                mRedPacketAlpha = Math.round(255 * (1 - (float) animation.getAnimatedValue()));
                final View thisView = BezierRefreshHeader.this;
                thisView.invalidate();
            }
        });
        redPacketAnimator.setInterpolator(new LinearInterpolator());
        redPacketAnimator.setRepeatCount(-1);
        redPacketAnimator.setDuration(1000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(waveAnimator).before(rotateAnimator).before(redPacketAnimator);
        animatorSet.start();
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        if (waveAnimator.isRunning()) {
            waveAnimator.cancel();
            waveAnimator.removeAllUpdateListeners();
        }

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rotateAnimator.isRunning()) {
                    rotateAnimator.cancel();
                    rotateAnimator.removeAllUpdateListeners();
                }
                if (redPacketAnimator.isRunning()) {
                    redPacketAnimator.cancel();
                    redPacketAnimator.removeAllUpdateListeners();
                }
                if(hasSetClicked){
                    onFinishListener.onFinish();
                }
            }
        }, 1000);

        mRotatePulling = false;
        mWavePulling = false;
        return 1000;
    }

    /**
     * @param colors 对应Xml中配置的 srlPrimaryColor srlAccentColor
     * @deprecated 请使用 {@link RefreshLayout#setPrimaryColorsId(int...)}
     */
    @Override
    @Deprecated
    public void setPrimaryColors(@ColorInt int... colors) {
        if (colors.length > 0) {
            mBackPaint.setColor(colors[0]);
        }
    }


}
