package com.dzkandian.common.widget.barrageview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.dzkandian.R;
import com.dzkandian.storage.bean.news.NewsDanmuBean;
import com.jess.arms.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

/**
 * 弹幕
 * Created by Administrator on 2017/3/30.
 */

public class DanmuContainerView extends ViewGroup implements VideoProgoressCallback {

    private long mLastDanmuTime;
    private Set<Integer> existMarginValues = new HashSet<>();
    public final static int LOW_SPEED = 1;
    public final static int NORMAL_SPEED = 4;
    public final static int HIGH_SPEED = 8;

    public final static int GRAVITY_TOP = 1;    //001
    public final static int GRAVITY_CENTER = 2;  //010
    public final static int GRAVITY_BOTTOM = 4;  //100
    public final static int GRAVITY_FULL = 7;   //111

    private int gravity = 7;

    private int spanCount = 8;

    private int WIDTH;

    public List<View> spanList;

    private int singleLineHeight = 90;

    private XAdapter xAdapter;

    private int speed = NORMAL_SPEED;

    private int currLine = 0;

    private List<NewsDanmuBean> mCachedModelPool;

    private int showLine = 0;  //显示的行数
    private int position = 0;      //显示的当前弹幕
    private Context context;
    //    private MyRunnable runnable;
//    private boolean playThree;   //弹幕线程

    public DanmuContainerView(Context context) {
        this(context, null, 0);
    }

    public DanmuContainerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DanmuContainerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        spanList = new ArrayList<View>();
        mCachedModelPool = new ArrayList<>();
    }


    //添加缓存弹幕
    public void addDanmuIntoCachePool(NewsDanmuBean tmp) {
        mCachedModelPool.add(tmp);
        Timber.d("======DanmuContainerView" + mCachedModelPool.size());
    }

    //清除缓存弹幕
    public void clearDanmuIntoCachePool() {
        mCachedModelPool.clear();
    }

    //清除控件缓存
    public void clearSpanList() {
        for (View view : spanList) {
            if (view != null)
                view.clearAnimation();
        }
        spanList.clear();
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void setAdapter(XAdapter danmuAdapter) {
        xAdapter = danmuAdapter;
        singleLineHeight = xAdapter.getSingleLineHeight();
    }

    /**
     * 弹幕进度(添加下一条弹幕)
     *
     * @param time 每个弹幕弹出的时间差
     */
    @Override
    public void onProgress(long time) {
        if (mCachedModelPool == null || mCachedModelPool.size() == 0) {
            return;
        }
        // 显示time 至 time + DANMU_STEP 之间的弹幕
        if (mLastDanmuTime < time) {
            if (position >= mCachedModelPool.size()) {
                position = 0;
            }
            NewsDanmuBean model = mCachedModelPool.get(position);
            if (model != null) {
                addDanmu(model);
            }
            int DANMU_STEP = 800;
            mLastDanmuTime = time + DANMU_STEP;
            position++;

//            Timber.d("========弹幕多少   mLastDanmuTime < time           mLastDanmuTime" + mLastDanmuTime);
//            Timber.d("========弹幕多少   mLastDanmuTime < time           time" + time);
        }
//        Timber.d("========弹幕多少   time" + time);
//        Timber.d("========弹幕多少   mLastDanmuTime" + mLastDanmuTime);
    }


    public void resetDanmuProgress() {
        mLastDanmuTime = 0;
    }

    //单项点击监听器
    public interface OnItemClickListener {
        void onItemClick(NewsDanmuBean model);
    }


    /**
     * 弹幕移动速度
     * 建议使用 DanmuContainerView.LOW_SPEED, DanmuContainerView.NORMAL_SPEED, DanmuContainerView.HIGH_SPEED
     * 自定义速度从1 到 8之间，值越大速度越快
     */
    public void setSpeed(int s) {
        speed = s;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        WIDTH = width;
        int HEIGHT = height;

//        Timber.d("行数  onMeasure===" + spanCount);
//        Timber.d("行数  onMeasure HEIGHT ===" + HEIGHT);
//        Timber.d("行数  onMeasure=== singleLineHeight" + singleLineHeight);
        spanCount = HEIGHT / singleLineHeight;

        for (int i = 0; i < this.spanCount; i++) {
            if (spanList.size() <= spanCount)
                spanList.add(i, null);
        }
    }


    //添加单个弹幕
    public void addDanmu(final NewsDanmuBean model) {
        if (xAdapter == null) {
            throw new Error("XAdapter(an interface need to be implemented) can't be null,you should call setAdapter firstly");
        }

        View danmuView = null;
        if (xAdapter.getCacheSize() >= 1) {
//            Timber.d("======DanmuContainerView数据集合xAdapter.getCacheSize()" + xAdapter.getCacheSize());
            danmuView = xAdapter.getView(model, xAdapter.removeFromCacheViews(model.getType()));
            if (danmuView == null)
                addTypeView(model, danmuView, false);
            else
                addTypeView(model, danmuView, true);
        } else {
//            Timber.d("======DanmuContainerView数据集合 addDanmu" + model);
            danmuView = xAdapter.getView(model, null);
            addTypeView(model, danmuView, false);
        }

        mCachedModelPool.add(model);
        //添加监听
        danmuView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(model);
            }
        });

    }


    public void addTypeView(NewsDanmuBean model, View child, boolean isReused) {
        super.addView(child);
        child.measure(0, 0);
        //把宽高拿到，宽高都是包含ItemDecorate的尺寸
        int width = child.getMeasuredWidth();
        int height = child.getMeasuredHeight();
        //获取最佳行数
        int bestLine = getBestLine();

        //获取手机屏幕宽
        int phoneWidth = (int) DeviceUtils.getScreenWidth(context);

//        Timber.d("======DanmuContainerView行数" + bestLine);
        child.layout(WIDTH, singleLineHeight * bestLine, WIDTH + width, singleLineHeight * bestLine + height);

        InnerEntity innerEntity = null;
        innerEntity = (InnerEntity) child.getTag(R.id.tag_inner_entity);
        if (!isReused || innerEntity == null) {
            innerEntity = new InnerEntity();
        }
        innerEntity.model = model;
        innerEntity.bestLine = bestLine;
        child.setTag(R.id.tag_inner_entity, innerEntity);
        if (spanList.size() == 0) {
            spanList.add(0, child);
            return;
        }
        spanList.set(bestLine, child);

        //属性动画
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(child, "translationX", 0f, -WIDTH - width);

//        final Animation translateAnimation = new TranslateAnimation(0, -WIDTH - width, 0, 0);//平移动画  从0,0,平移到100,100
        objectAnimator.setDuration(8000);//动画持续的时间为1.5s
//        objectAnimator.setRepeatCount(Animation.INFINITE);  //循环播放
        objectAnimator.setInterpolator(new LinearInterpolator());

        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                //动画开始
//                Timber.d("===========动画 onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.cancel();
                animator.removeAllListeners();
                for (int i = 0; i < DanmuContainerView.this.getChildCount(); i++) {
                    View view = DanmuContainerView.this.getChildAt(i);
                    if (view.getX() + view.getWidth() >= 0) {
//                        Timber.d("速度是多少       " + offset + "Math.round(offset" + Math.round(offset));
                    } else {
                        //添加到缓存中
//                        Timber.d("===添加进缓存，什么时间执行这句");
                        int type = ((InnerEntity) view.getTag(R.id.tag_inner_entity)).model.getType();
                        xAdapter.addToCacheViews(type, view);
                        xAdapter.shrinkCacheSize();
                        System.gc();
                        DanmuContainerView.this.removeView(view);
                    }
                }
                //动画结束
//                Timber.d("===========动画 onAnimationEnd" + objectAnimator.isRunning() + "    isStarted" + objectAnimator.isStarted());
            }


            @Override
            public void onAnimationCancel(Animator animator) {
                //动画取消
//                Timber.d("===========动画 onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                //动画重复
//                Timber.d("===========动画 onAnimationRepeat");
            }
        });

        objectAnimator.start();


//        Timber.d("行数===        spanList" + bestLine);
    }


    // 开启弹幕线程
//    public void setPlay(boolean playThree) {
//        this.playThree = playThree;
//        if (playThree) {
//            if (runnable == null) {
//                runnable = new MyRunnable();
//            }
//            Thread thread = new Thread(runnable);
//            thread.start();
//            singleLineHeight = xAdapter.getSingleLineHeight();
//        }
//
//    }


    //设置行数
    private int getBestLine() {
        if (spanList.size() == 0) {
            if (spanCount - 1 > 0 && spanCount - 1 < 3) {
                return spanCount;
            } else {
//                Timber.d("行数===        getBestLine()=== spanList.size： " + 0);
                return 1;
            }
        }
        //转换成2进制
        int gewei = gravity % 2;   //个位是
        int temp = gravity / 2;
        int shiwei = temp % 2;
        temp = temp / 2;
        int baiwei = temp % 2;
//        //Timber.d("行数===        getwei：" + gewei + "  shiwei：" + shiwei + " baidwei：" + baiwei);

        //将所有的行分为三份,前两份行数相同,将第一份的行数四舍五入
        int firstPart = (int) (spanCount / 3.0f + 0.5f);
//        Timber.d("行数===        getBestLine()=== firstPart： " + firstPart);
        //构造允许输入行的列表
        List<Integer> legalLines = new ArrayList<>();
        if (gewei == 1) {
            for (int i = 0; i < firstPart; i++) {
                legalLines.add(i);
//                Timber.d("行数===        构造gewei允许输入行的列表" + i);     //gewei 一直为0
            }

        }
        if (shiwei == 1) {
            for (int i = firstPart; i < 2 * firstPart; i++) {
                legalLines.add(i);
//                Timber.d("行数===        构造shiwei允许输入行的列表" + i);     //shiwei 一直为1
            }

        }
        if (baiwei == 1) {
            for (int i = 2 * firstPart; i < spanCount; i++) {
                legalLines.add(i);
//                Timber.d("行数===        构造baiwei允许输入行的列表" + i);     //baiwei 一直为2
            }

        }


        int bestLine = 0;
        //  这里如果有空行 直接返回
        for (int i = 0; i < spanCount; i++) {
//            Timber.d("行数===  bestLine for +: " + i);
            if (spanList.size() == 0 || i >= spanList.size()) {
                return 0;
            }
            if (spanList.get(i) == null) {
                bestLine = i;
//                Timber.d("行数===  bestLine for ++: " + bestLine);
//                Timber.d("行数===  构造gewei允许输入行的列表" + i);
                if (legalLines.contains(bestLine)) {
//                    Timber.d("行数===  bestLine for ++legalLines: " + bestLine);
                    return bestLine;
                }
            }
        }


        float minSpace = Integer.MAX_VALUE;
        //  没有空行，就找最大空间的
        for (int i = spanCount - 1; i >= 0; i--) {
            //Timber.d("行数===        legalLines.contains(i): " + legalLines.contains(i));
            if (legalLines.contains(i)) {
                //Timber.d("行数===        spanList.get(i).getX() + spanList.get(i).getWidth(): "
//                        + (spanList.get(i).getX() + spanList.get(i).getWidth()));
                //Timber.d("行数===        minSpace = Integer.MAX_VALUE: "
//                        + (minSpace));
                //Timber.d("行数===        spanList.get(i).getX() + spanList.get(i).getWidth() <= minSpace: "
//                        + (spanList.get(i).getX() + spanList.get(i).getWidth() <= minSpace));

                if (spanList.get(i).getX() + spanList.get(i).getWidth() <= minSpace) {
//                    Timber.d("行数  onMeasure  i = " + i + "\n spanList.get(i).getX()" + spanList.get(i).getX() +
//                            "\n" + "spanList.get(i).getWidth()" + spanList.get(i).getWidth() + "\n"
//                            + "之和" + (spanList.get(i).getX() + spanList.get(i).getWidth()) + "        minSpace" + minSpace
//
//                    );
                    minSpace = spanList.get(i).getX() + spanList.get(i).getWidth();
                    bestLine = i;
//                    Timber.d("行数===  bestLine for --: " + bestLine);
                }
            }
        }
//        Timber.d("行数===  bestLine end: " + bestLine);
        return bestLine;
    }

    class InnerEntity {
        public int bestLine;
        public NewsDanmuBean model;
    }

    public void onDestroy() {
        clearDanmuIntoCachePool();
        clearSpanList();

    }

}
