package com.dzkandian.common.widget.explosion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * Created by Administrator on 2018/4/4 0004.
 */

public class ExplosionField extends View {
    @NonNull
    private List<ExplosionAnimator> mExplosions = new ArrayList<>();
    @NonNull
    private int[] mExpandInset = new int[2];
    private Bitmap bitmap;

    public ExplosionField(Context context) {
        super(context);
        init();
    }

    public ExplosionField(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExplosionField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Arrays.fill(mExpandInset, Utils.dp2Px(32));
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        for (ExplosionAnimator explosion : mExplosions) {
            explosion.draw(canvas);
        }
    }

    public void expandExplosionBound(int dx, int dy) {
        mExpandInset[0] = dx;
        mExpandInset[1] = dy;
    }

    public void explode(@NonNull Bitmap bitmap, Rect bound, long startDelay, long duration) {
        final ExplosionAnimator explosion = new ExplosionAnimator(this, bitmap, bound);
        explosion.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mExplosions.remove(animation);
            }
        });
        explosion.setStartDelay(startDelay);
        explosion.setDuration(duration);
        mExplosions.add(explosion);
        explosion.start();
    }

    public void explode(@NonNull final View view) {
        Rect r = new Rect();
        view.getGlobalVisibleRect(r);
        int[] location = new int[2];
        getLocationOnScreen(location);
        r.offset(-location[0], -location[1]);
        r.inset(-mExpandInset[0], -mExpandInset[1]);
        int startDelay = 100;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(150);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @NonNull
            Random random = new Random();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                view.setTranslationX((random.nextFloat() - 0.5f) * view.getWidth() * 0.05f);
//                view.setTranslationY((random.nextFloat() - 0.5f) * view.getHeight() * 0.05f);
                view.setAlpha(0f);
            }
        });
        animator.start();
//        view.animate().setDuration(150).setStartDelay(startDelay).scaleX(0f).scaleY(0f).alpha(0f).start();
        bitmap = Utils.createBitmapFromView(view);
        explode(bitmap, r, startDelay, ExplosionAnimator.DEFAULT_DURATION);
    }

    public void clear() {
        mExplosions.clear();
        invalidate();
        if (bitmap != null && !bitmap.isRecycled())
            bitmap.recycle();
    }

    @NonNull
    public static ExplosionField attach2Window(@NonNull Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        ExplosionField explosionField = new ExplosionField(activity);
        rootView.addView(explosionField, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return explosionField;
    }
}
