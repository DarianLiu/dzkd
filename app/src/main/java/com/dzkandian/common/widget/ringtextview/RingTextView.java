package com.dzkandian.common.widget.ringtextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by kay on 16/5/8.
 */
public class RingTextView extends View {
    TextPaint paint ;//文字画
    Paint circlePaint;//圆
    Paint ringPaint;//戒指
    @NonNull
    String content = "跳过";
    final int STROKE_WIDTH = 5;//笔画宽度
    float textWidth ;//文字宽度
    int desireWidth;//希望宽度
    float sweepAngle = 0;//扫描角
    RectF rectF;
    @NonNull
    Rect rect = new Rect();
    RingTextViewOnClickListener clickListener;

    //构造函数
    public RingTextView(Context context) {
        super(context);
    }
    public RingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new TextPaint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(30);
        paint.setColor(Color.WHITE);

        circlePaint = new Paint();
        circlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.GRAY);

        ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringPaint.setColor(Color.RED);
        ringPaint.setStrokeWidth(STROKE_WIDTH);
        ringPaint.setStyle(Paint.Style.STROKE);

        textWidth =  paint.measureText(content);
        desireWidth =(int)(textWidth+STROKE_WIDTH*6);

        rectF = new RectF(STROKE_WIDTH/2,
                STROKE_WIDTH/2,
                desireWidth-STROKE_WIDTH/2,
                desireWidth-STROKE_WIDTH/2);
        //获取显示文字的区域
        paint.getTextBounds(content,0,content.length(),rect);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(desireWidth, desireWidth);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {

        canvas.drawCircle((int)(desireWidth/2),(int)(desireWidth/2),(int)(desireWidth/2),circlePaint);

        //int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;

        //控件的高度一半 下移 文字高度的一半减去基线下面的距离
        int yPos = (int) ((canvas.getHeight() / 2) + (rect.height()/2-rect.bottom)) ;
        //x偏移为线宽+间距
        //y偏移
        canvas.drawText(content, STROKE_WIDTH * 2, yPos, paint);
        canvas.save();
        canvas.rotate(-90f, desireWidth/2,desireWidth/2);
        canvas.drawArc(rectF, 0, sweepAngle, false,ringPaint);
        canvas.restore();
    }

    public void setProgess(int total ,int now){
       float each = 360/total;
        sweepAngle = now*each;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                setAlpha(0.3f);
                break;
            case MotionEvent.ACTION_UP:
                setAlpha(1.0f);
                if(clickListener!=null){
                    clickListener.onClick(this);
                }
                break;
        }
        return true;
    }

    public void setClickListener(RingTextViewOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

}
