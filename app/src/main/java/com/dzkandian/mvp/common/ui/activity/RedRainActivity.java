package com.dzkandian.mvp.common.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.config.imageloader.CustomImageConfig;
import com.dzkandian.mvp.mine.ui.activity.InvitationActivity;
import com.dzkandian.mvp.mine.ui.activity.MessageCenterActivity;
import com.dzkandian.mvp.mine.ui.activity.QuickCashActivity;
import com.dzkandian.storage.event.ChangeTabEvent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;

import org.simple.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.mob.MobSDK.getContext;

public class RedRainActivity extends Activity {

    @BindView(R.id.red_rain_LottieAnimationView)
    LottieAnimationView redRainLottieView;
    @BindView(R.id.red_rain_gold)
    TextView redRainGold;
    @BindView(R.id.red_rain_close)
    ImageView redRainClose;
    @BindView(R.id.red_rain_layout)
    RelativeLayout redRainLayout;
    @BindView(R.id.red_rain_activity)
    ImageView redRainActivity;
    @BindView(R.id.red_rain_activity_close)
    ImageView redRainActivityClose;
    private boolean isRedRain = false;//红包雨是否完成
    private boolean isRedNewOpen = false;//新手红包是否完成
    private boolean isRedOldOpen = false;//老用户红包是否完成
    private int indexPop;//首页弹窗类型
    private String indexPopAttachData;//弹窗附加数据
    private String indexPopActivityPic;//首页弹窗活动图片地址
    private String indexPopActivityEvent;//首页弹窗活动事件
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//将系统自带的标题栏隐藏掉
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//整个窗体全屏
//        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//显示状态栏
        setContentView(R.layout.activity_rain_red);
        ButterKnife.bind(this);
//        WindowManager windowManager = getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.height = (int) (display.getHeight() * 0.3);
//        params.width = (int) (display.getWidth() * 0.8);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.alpha = 1.0f;
        getWindow().setAttributes(params);
        getWindow().setGravity(Gravity.CENTER);
        imageLoader = ArmsUtils.obtainAppComponentFromContext(this).imageLoader();

        getIntentValue(getIntent());

        initData();

    }

    /**
     * 获取Adapter传过来的值
     */
    private void getIntentValue(Intent intent) {
        indexPop = intent.getIntExtra("indexPop", 0);
        indexPopAttachData = intent.getStringExtra("indexPopAttachData");
        indexPopActivityPic = intent.getStringExtra("indexPopActivityPic");
        indexPopActivityEvent = intent.getStringExtra("indexPopActivityEvent");
    }

    private void initData() {
        if (indexPop == 1) {
            redRainLottieView.setVisibility(View.VISIBLE);
            redRainLottieView.setAnimation("redrain.json");
            redRainLottieView.setImageAssetsFolder("imagesredrain");
        } else if (indexPop == 2) {
            redRainLottieView.setVisibility(View.VISIBLE);
            redRainClose.setVisibility(View.GONE);
            redRainLottieView.setAnimation("rednewopen.json");
            redRainLottieView.setImageAssetsFolder("imagesrednewopen");
        } else if (indexPop == 3) {
            redRainLottieView.setVisibility(View.GONE);
            redRainClose.setVisibility(View.GONE);
            redRainGold.setVisibility(View.GONE);

            int width = (ArmsUtils.getScreenWidth(getContext()) - ArmsUtils.dip2px(getContext(), 50));
            RelativeLayout.LayoutParams lpActivity = new RelativeLayout.LayoutParams(width, width);
            lpActivity.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lpActivity.addRule(RelativeLayout.CENTER_VERTICAL);
            redRainActivity.setLayoutParams(lpActivity);
            redRainActivity.setVisibility(View.VISIBLE);
            imageLoader.loadImage(getContext(), CustomImageConfig.builder()
                    .url(indexPopActivityPic)
                    .cacheStrategy(1)
                    .errorPic(R.drawable.icon_activity_transparent)
                    .placeholder(R.drawable.icon_activity_transparent)
                    .imageView(redRainActivity)
                    .build());

            RelativeLayout.LayoutParams lpActivityClose = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpActivityClose.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            lpActivityClose.addRule(RelativeLayout.BELOW, redRainActivity.getId());
            lpActivityClose.topMargin = 200;
            redRainActivityClose.setLayoutParams(lpActivityClose);
            redRainActivityClose.setVisibility(View.VISIBLE);
        } else if (indexPop == 4) {
            redRainLottieView.setVisibility(View.VISIBLE);
            redRainClose.setVisibility(View.GONE);
            redRainLottieView.setAnimation("redoldopen.json", LottieAnimationView.CacheStrategy.Weak);
            redRainLottieView.setImageAssetsFolder("imagesredoldopen");
        }

        if (indexPop == 3) {
            /*活动图片点击事件*/
            redRainActivity.setOnClickListener(view -> {
                if (!TextUtils.isEmpty(indexPopActivityEvent)) {
                    switch (indexPopActivityEvent) {
                        case "innerJump":  //内部跳转
                            Intent innerIntent = new Intent(RedRainActivity.this, WebViewActivity.class);
                            innerIntent.putExtra("URL", indexPopAttachData);
                            startActivity(innerIntent);
                            break;
                        case "outnerJump": //外部跳转
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(indexPopAttachData)));
                            break;
                        case "invitation": //好友邀请
                            startActivity(new Intent(RedRainActivity.this, InvitationActivity.class));
                            break;
                        case "withdrawals": //快速提现
                            startActivity(new Intent(RedRainActivity.this, QuickCashActivity.class));
                            break;
                        case "activityPage":  //活动公告页
                            Intent activityIntent = new Intent(RedRainActivity.this, MessageCenterActivity.class);
                            activityIntent.putExtra("tab", 0);
                            startActivity(activityIntent);
                            break;
                        case "myMsgPage":   //我的消息页面
                            Intent messageIntent = new Intent(RedRainActivity.this, MessageCenterActivity.class);
                            messageIntent.putExtra("tab", 1);
                            startActivity(messageIntent);
                            break;
                        case "taskCenter":   //任务中心
                            Intent intent = new Intent(RedRainActivity.this, MainActivity.class);
                            startActivity(intent);
                            EventBus.getDefault().post(new ChangeTabEvent.Builder().indexTab(2).build(), EventBusTags.TAG_CHANGE_TAB);
                            break;
                        default:
                            break;
                    }
                    onDestroy();
                }
            });

            /*活动关闭按钮*/
            redRainActivityClose.setOnClickListener(view -> {
                onDestroy();
            });

        } else {
            startAnima();
            redRainLottieView.addAnimatorUpdateListener(valueAnimator -> {
                if (valueAnimator.getAnimatedFraction() == 1) {
                    if (redRainLottieView.getImageAssetsFolder().equals("imagesredrain")) {
                        isRedRain = true;
                        /*配置红包雨关闭图标的位置*/
                        int rheight = redRainLayout.getMeasuredHeight();
                        int rwidth = redRainLayout.getMeasuredWidth();
                        Timber.d("==redrain  relativeLayout:  高度：" + rheight + "  宽度：" + rwidth);
                        RelativeLayout.LayoutParams lpClose = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        lpClose.setMargins(rwidth / 4 * 3, rheight / 12, 0, 0);
                        redRainClose.setLayoutParams(lpClose);
                        redRainClose.setVisibility(View.VISIBLE);
                    } else if (redRainLottieView.getImageAssetsFolder().equals("imagesrednewopen")) {
                        isRedNewOpen = true;
                    } else if (redRainLottieView.getImageAssetsFolder().equals("imagesredoldopen")) {
                        isRedOldOpen = true;
                    }
                }

                if (redRainLottieView.getImageAssetsFolder().equals("imagesrednewopen") || redRainLottieView.getImageAssetsFolder().equals("imagesredoldopen")) {
                    if (valueAnimator.getAnimatedFraction() != 1 && valueAnimator.getAnimatedFraction() > 0.86 && redRainGold.getVisibility() == View.GONE) {
                        /*配置红包金币数的位置*/
                        int rheight = redRainLayout.getMeasuredHeight();
                        int rwidth = redRainLayout.getMeasuredWidth();
                        Timber.d("==redrain  relativeLayout:  高度：" + rheight + "  宽度：" + rwidth);
                        RelativeLayout.LayoutParams lpGold = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        lpGold.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                        lpGold.setMargins(0, rheight / 2, 0, 0);
                        redRainGold.setLayoutParams(lpGold);
                        redRainGold.setVisibility(View.VISIBLE);
                        redRainGold.setText(indexPopAttachData + " 元");
                    }
                }
            });

            redRainLottieView.setOnClickListener(view -> {
                if (isRedRain) {
                    isRedRain = false;
                    stopAnima();
                    redRainLottieView.destroyDrawingCache();
                    Intent intent = new Intent(RedRainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    onDestroy();
                } else if (isRedNewOpen) {
                    isRedNewOpen = false;
                    Intent intent = new Intent(RedRainActivity.this, MainActivity.class);
                    startActivity(intent);
                    EventBus.getDefault().post(new ChangeTabEvent.Builder().indexTab(2).build(), EventBusTags.TAG_CHANGE_TAB);
                    onDestroy();
                } else if (isRedOldOpen) {
                    isRedOldOpen = false;
                    Intent intent = new Intent(RedRainActivity.this, MainActivity.class);
                    startActivity(intent);
                    EventBus.getDefault().post(new ChangeTabEvent.Builder().indexTab(2).build(), EventBusTags.TAG_CHANGE_TAB);
                    onDestroy();
                }
            });

            redRainClose.setOnClickListener(view -> {
                if (isRedRain) {
                    onDestroy();
                }
            });
        }
    }

    /*
     * 开始动画
     */
    private void startAnima() {
        boolean inPlaying = redRainLottieView.isAnimating();
        if (!inPlaying) {
            redRainLottieView.setProgress(0f);
            redRainLottieView.playAnimation();
        }
    }

    /*
    * 停止动画
    */
    private void stopAnima() {
        boolean inPlaying = redRainLottieView.isAnimating();
        if (inPlaying) {
            redRainLottieView.cancelAnimation();
            redRainLottieView.clearAnimation();
            redRainLottieView.destroyDrawingCache();//删除缓存
        }
    }

    //返回键按钮的点击事件：
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            imageLoader = null;
            redRainLottieView.cancelAnimation();//暂停动画
            redRainLottieView.clearAnimation();
            redRainLottieView.destroyDrawingCache();//删除缓存
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        imageLoader = null;
        redRainLottieView.cancelAnimation();//暂停动画
        redRainLottieView.clearAnimation();
        redRainLottieView.destroyDrawingCache();//删除缓存
        super.onDestroy();
        finish();
    }
}
