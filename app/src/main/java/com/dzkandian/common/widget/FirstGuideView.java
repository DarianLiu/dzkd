package com.dzkandian.common.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;

import com.dzkandian.R;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FirstGuideView extends View implements ViewTreeObserver.OnGlobalLayoutListener,View.OnClickListener {

    public static final int SHAPE_CIRCLE = 1;
    public static final int SHAPE_RECT = 2;

    private Context mContext;
    //targetView是否已经测量过
    private boolean isMeasured;
    //是否设置点击事件
    private boolean hasSetClicked;
    //需要高亮的view
    private List<View> targetViewList;
    //展示的形状 圆或矩形
    private int shape;
    //圆的半径
    private int radius;
    //矩形的长
    private int rectWidth;
    //矩形的宽
    private int rectHeight;
    //key:需要绘制的图片 value:图片左上角的座标,绘制drawable比bitmap更省内存
    private HashMap<Drawable,Point> picture;
    //用于存储targetView中心坐标的sparse数组集合
    private SparseArray<Integer> targetPosition;

    private OnClickListener onClickListener;

    //背景画笔（半透明）
    private Paint bgPaint;

    public FirstGuideView(Context context) {
        super(context);
        init(context);
    }

    public FirstGuideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FirstGuideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context){
        setLayerType(LAYER_TYPE_SOFTWARE,null);
        this.mContext = context;
        targetViewList = new ArrayList<>();
        targetPosition = new SparseArray<>();
        picture = new HashMap<>();
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(Color.parseColor("#80000000"));
        radius = DensityUtil.dp2px(32);
        rectWidth = DensityUtil.dp2px(120);
        rectHeight = DensityUtil.dp2px(65);

    }

    public List<View> getTargetViewList() {
        return targetViewList;
    }

    public void setTargetViewList(List<View> targetViewList) {
        this.targetViewList = targetViewList;
    }

    public int getShape() {
        return shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getRectWidth() {
        return rectWidth;
    }

    public void setRectWidth(int rectWidth) {
        this.rectWidth = rectWidth;
    }

    public int getRectHeight() {
        return rectHeight;
    }

    public void setRectHeight(int rectHeight) {
        this.rectHeight = rectHeight;
    }

    public HashMap<Drawable, Point> getPicture() {
        return picture;
    }

    public void setPicture(HashMap<Drawable, Point> picture) {
        this.picture = picture;
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        hasSetClicked = true;
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View v) {
        if(hasSetClicked) {
            onClickListener.onClick(v);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.drawRect(new RectF(0,0,getWidth(),getHeight()),bgPaint);
        Iterator<Map.Entry<Drawable,Point>> iterator = picture.entrySet().iterator();
        while (iterator.hasNext()){
           Map.Entry<Drawable,Point> entry =  iterator.next();
           Drawable drawable = entry.getKey();
           Point point = entry.getValue();
           canvas.translate(point.getX(),point.getY());
           drawable.setBounds(new Rect(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight()));
           drawable.draw(canvas);
           canvas.translate(point.getX() *(-1f),point.getY() *(-1f));
        }
        if(targetPosition != null && targetPosition.size() >0){
          for(int i = 0;i< targetPosition.size();i++){
             int targetViewX = targetPosition.keyAt(i);
             int targetViewY = targetPosition.valueAt(i);
             bgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
             bgPaint.setColor(Color.TRANSPARENT);
             switch (shape){
                 case SHAPE_CIRCLE:
                 canvas.drawCircle(targetViewX,targetViewY,radius,bgPaint);
                 break;
                 case SHAPE_RECT:
                 canvas.drawRoundRect(new RectF(0,0,rectWidth,rectHeight),targetViewX - rectWidth/2,targetViewY - rectHeight/2,bgPaint);
                 break;
             }
          }
        }
        canvas.restore();
    }

    public static class Builder {
        static FirstGuideView guiderView;
        static Builder instance = new Builder();

       public static Builder newInstance(Context ctx) {
        guiderView = new FirstGuideView(ctx);
         return instance;
       }

       public Builder setTargetViewList(List<View> targetViewList){
         guiderView.setTargetViewList(targetViewList);
         return instance;
       }

       public Builder setShape(int shape){
         guiderView.setShape(shape);
         return instance;
       }

       public Builder setRadius(int radius){
         guiderView.setRadius(radius);
         return instance;
       }

       public Builder setRectWidth(int width){
         guiderView.setRectWidth(width);
         return instance;
       }

       public Builder setRectHeight(int height){
         guiderView.setRectHeight(height);
         return instance;
       }

       public  Builder setPicture(HashMap<Drawable,Point> picture){
           guiderView.setPicture(picture);
          return instance;
       }

       public Builder setOnClickListener(OnClickListener onClickListener){
           guiderView.setOnClickListener(onClickListener);
           return instance;
       }

       public FirstGuideView build(){
           return guiderView;
       }

    }

    //通过builder模式创建后调用此方法，为targetView注册测量监听，测量完毕后回调onGlobalLayout算出targetView的宽高
    public void show(){
      if(targetViewList != null && targetViewList.size() >0){
        for(View targetView: targetViewList) {
           targetView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
        this.bringToFront(); //设置在最上层
         ViewGroup contentView = ((Activity)mContext).getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
         contentView.addView(this,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
      }
    }

    //用户点击黑色遮罩区域后移除掉此view
    public void hide(){
      if(targetViewList != null && targetViewList.size() >0){
          for(View targetView: targetViewList){
              targetView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
          }
          ViewGroup contentView = ((Activity)mContext).getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
          contentView.removeView(this);
          restoreState();
      }
    }

    //状态复原
    private void restoreState(){
      bgPaint = null;
      picture.clear();
      picture = null;
      targetViewList.clear();
      targetViewList = null;
      targetPosition.clear();
      targetViewList = null;
      isMeasured = false;
      hasSetClicked = false;
    }

    @Override
    public void onGlobalLayout() {
      if(isMeasured) {
          return;
      }
      if(targetViewList != null && targetViewList.size() >0){
          for(View targetView: targetViewList){
            isMeasured = true;
            int targetViewWidth = targetView.getWidth();
            int targetViewHeight = targetView.getHeight();
            int []location = new int[2];
            targetView.getLocationInWindow(location);
            targetPosition.put((int)(location[0] + targetViewWidth/2),(int)(location[1] + targetViewHeight/2));
          }

      }
    }

    //记录提示图片的左上角坐标
    public  static class Point{
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

}
