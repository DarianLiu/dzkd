package com.dzkandian.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.dzkandian.common.widget.sideslip.MySlidingPaneLayout;
import com.jess.arms.base.delegate.IActivity;
import com.jess.arms.integration.cache.Cache;
import com.jess.arms.integration.cache.CacheType;
import com.jess.arms.integration.lifecycle.ActivityLifecycleable;
import com.jess.arms.mvp.IPresenter;
import com.jess.arms.utils.ArmsUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.lang.reflect.Field;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import static com.jess.arms.utils.ThirdViewUtil.convertAutoView;

/**
 * 侧滑基类
 * Created by Administrator on 2018/6/22.
 */

public abstract class SideslipBaseActivity<P extends IPresenter> extends AppCompatActivity
        implements IActivity, ActivityLifecycleable, MySlidingPaneLayout.PanelSlideListener {

    protected final String TAG = this.getClass().getSimpleName();
    private final BehaviorSubject<ActivityEvent> mLifecycleSubject = BehaviorSubject.create();
    private Cache<String, Object> mCache;
    private Unbinder mUnbinder;
    @Inject
    @Nullable
    protected P mPresenter;//如果当前页面逻辑简单, Presenter 可以为 null

    @NonNull
    @Override
    public synchronized Cache<String, Object> provideCache() {
        if (mCache == null) {
            mCache = ArmsUtils.obtainAppComponentFromContext(this).cacheFactory().build(CacheType.ACTIVITY_CACHE);
        }
        return mCache;
    }

    @NonNull
    @Override
    public final Subject<ActivityEvent> provideLifecycleSubject() {
        return mLifecycleSubject;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = convertAutoView(name, context, attrs);
        return view == null ? super.onCreateView(name, context, attrs) : view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initSlideBackClose();
        super.onCreate(savedInstanceState);
        try {
            int layoutResID = initView(savedInstanceState);
            //如果initView返回0,框架则不会调用setContentView(),当然也不会 Bind ButterKnife
            if (layoutResID != 0) {
                setContentView(layoutResID);
                //绑定到butterknife
                mUnbinder = ButterKnife.bind(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initData(savedInstanceState);
    }

    private void initSlideBackClose() {
        if (isSupportSwipeBack()) {
            MySlidingPaneLayout slidingPaneLayout = new MySlidingPaneLayout(this);
            // 通过反射改变mOverhangSize的值为0，
            // 这个mOverhangSize值为菜单到右边屏幕的最短距离，
            // 默认是32dp，现在给它改成0
            try {
                Field overhangSize = MySlidingPaneLayout.class.getDeclaredField("mOverhangSize");
                overhangSize.setAccessible(true);
                overhangSize.set(slidingPaneLayout, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            slidingPaneLayout.setPanelSlideListener(this);
            slidingPaneLayout.setSliderFadeColor(getResources()
                    .getColor(android.R.color.transparent));

            // 左侧的透明视图
            View leftView = new View(this);
            leftView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            slidingPaneLayout.addView(leftView, 0);

            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();


            // 右侧的内容视图
            ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
            decorChild.setBackgroundColor(getResources()
                    .getColor(android.R.color.white));
            decorView.removeView(decorChild);
            decorView.addView(slidingPaneLayout);

            // 为 SlidingPaneLayout 添加内容视图
            slidingPaneLayout.addView(decorChild, 1);
        }
    }

    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
//        Timber.d("=======SlideBaseActivity: onPanelSlide");
    }

    @Override
    public void onPanelOpened(View panel) {
        finish();
    }

    @Override
    public void onPanelClosed(View panel) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY)
            mUnbinder.unbind();
        this.mUnbinder = null;
        if (mPresenter != null)
            mPresenter.onDestroy();//释放资源
        this.mPresenter = null;
    }

    /**
     * 是否使用eventBus,默认为使用(true)，
     */
    @Override
    public boolean useEventBus() {
        return true;
    }

    /**
     * 这个Activity是否会使用Fragment,框架会根据这个属性判断是否注册{@link android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks}
     * 如果返回false,那意味着这个Activity不需要绑定Fragment,那你再在这个Activity中绑定继承于 {@link com.jess.arms.base.BaseFragment} 的Fragment将不起任何作用
     */
    @Override
    public boolean useFragment() {
        return true;
    }
}
