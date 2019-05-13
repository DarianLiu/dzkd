package com.dzkandian.mvp.mine.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.config.imageloader.CustomImageConfig;
import com.dzkandian.common.uitls.AndroidUtil;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.MobOneKeyShare;
import com.dzkandian.common.widget.autoviewpager.AutoScrollViewPager;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.common.ui.activity.WebViewActivity;
import com.dzkandian.mvp.mine.contract.InvitationContract;
import com.dzkandian.mvp.mine.di.component.DaggerInvitationComponent;
import com.dzkandian.mvp.mine.di.module.InvitationModule;
import com.dzkandian.mvp.mine.presenter.InvitationPresenter;
import com.dzkandian.storage.bean.ShareBean;
import com.dzkandian.storage.bean.WeChatShareBean;
import com.dzkandian.storage.bean.mine.BannerBean;
import com.dzkandian.storage.bean.mine.InvitePageBean;
import com.dzkandian.storage.event.ChangeTabEvent;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.utils.ArmsUtils;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 好友邀请主页
 */
public class InvitationActivity extends BaseActivity<InvitationPresenter> implements InvitationContract.View {

    @Nullable
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.b_invite)
    Button bInvite;
    @Nullable
    @BindView(R.id.tv_invite_inviteCount)
    TextView tvInviteInviteCount;
    @Nullable
    @BindView(R.id.ll_invite_chenggong)
    RelativeLayout llInviteChenggong;
    @Nullable
    @BindView(R.id.tv_invite_totalIncome)
    TextView tvInviteTotalIncome;
    @Nullable
    @BindView(R.id.ll_invite_zongshouyi)
    RelativeLayout llInviteZongshouyi;
    @Nullable
    @BindView(R.id.tv_invite_inviteProfitText)
    TextView tvInviteInviteProfitText;
    @Nullable
    @BindView(R.id.tv_invite_addedProfitText)
    TextView tvInviteAddedProfitText;
    @Nullable
    @BindView(R.id.tv_rewardRule1)
    TextView tvRewardRule1;
    @Nullable
    @BindView(R.id.tv_rewardRule2)
    TextView tvRewardRule2;
    @Nullable
    @BindView(R.id.tv_rewardRule3)
    TextView tvRewardRule3;
    @Nullable
    @BindView(R.id.tv_rewardRule4)
    TextView tvRewardRule4;
    @Nullable
    @BindView(R.id.tv_rewardRule5)
    TextView tvRewardRule5;
    @Nullable
    @BindView(R.id.tv_rewardRule6)
    TextView tvRewardRule6;
    @Nullable
    @BindView(R.id.tv_rewardRule7)
    TextView tvRewardRule7;
    @Nullable
    @BindView(R.id.tv_invite_rewardRuleText)
    TextView tvInviteRewardRuleText;
    @BindView(R.id.iv_invite_RQcodeImg)
    ImageView ivInviteRQcodeImg;

    @BindView(R.id.autoScrollViewPager)
    AutoScrollViewPager autoScrollViewPager;

    @BindView(R.id.autoScrollIndicator)
    LinearLayout autoScrollIndicator;

    @Nullable
    @Inject
    ImageLoader imageLoader;
    @BindView(R.id.rl_invitation)
    RelativeLayout rlInvitation;
    @BindView(R.id.btn_retry)
    Button btnRetry;
    @BindView(R.id.ll_error_view)
    LinearLayout llErrorView;

    private WeChatShareBean weChatShareBeans;
    private String imagePath;
    @Nullable
    private LoadingProgressDialog loadingProgressDialog;
    private String imageUserPath;

    private IWXAPI api; // IWXAPI 是第三方app和微信通信的openapi接口
    private MobOneKeyShare oneKeyShare;

    //轮播图低部滑动图片红点
    private ArrayList<ImageView> mScrollImageViews = new ArrayList<>();
    //轮播图图片
    private List<BannerBean> bannerBeans = new ArrayList<>();
    private long clickInvitationTimes;//点击好友邀请按钮的上一次时间；
    private long clickRetryTimes;//点击重新加载的上一次时间；

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerInvitationComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .invitationModule(new InvitationModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_invitation; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(R.string.mine_invite);
        toolbar.setNavigationOnClickListener(v -> killMyself());

        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID);

        imageUserPath = getIntent().getStringExtra("imageUserPath");
        Timber.d("==share  invitation:" + imageUserPath);

        int width = ArmsUtils.getScreenWidth(this);
        int heightRQcode = width / 2;
        ivInviteRQcodeImg.setLayoutParams(new LinearLayout.LayoutParams(width, heightRQcode));

        if (isInternet()) {
            assert mPresenter != null;
            mPresenter.invitePageData();
            mPresenter.inviteShare();
            mPresenter.getBannerImgs();
        } else {
            setErrorLayout();
        }
        /*重新加载按钮*/
        btnRetry.setOnClickListener(view -> {
            if (System.currentTimeMillis() - clickRetryTimes > 2000) {
                clickRetryTimes = System.currentTimeMillis();
                if (isInternet() && mPresenter != null) {
                    mPresenter.invitePageData();
                    mPresenter.inviteShare();
                    mPresenter.getBannerImgs();
                } else {
                    showMessage("网络请求失败，请连网后重试");
                }
            }
        });

        setBannerHeight();

        //初始化一键分享页面
        oneKeyShare = new MobOneKeyShare(this);

    }

    /**
     * 无网络显示“重新加载”按钮
     */
    @Override
    public void setErrorLayout() {
        rlInvitation.setVisibility(View.GONE);
        llErrorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        if (loadingProgressDialog == null)
            loadingProgressDialog = new LoadingProgressDialog.Builder(this).create();
        loadingProgressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing())
            loadingProgressDialog.dismiss();
    }


    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.makeText(getApplicationContext(), message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }


    /**
     * 刷新整个页面
     *
     * @param invitePageBean 收徒页面展示的必需数据
     */
    @Override
    public void updateView(@NonNull InvitePageBean invitePageBean) {
//        if (invitePageBean.getBannerImg() != null) {
//            imageLoader.loadImage(this, ImageConfigImpl.builder()
//                    .url(invitePageBean.getBannerImg())
//                    .cacheStrategy(1)
//                    .errorPic(R.drawable.icon_dzkd_place)
//                    .placeholder(R.drawable.icon_dzkd_place)
//                    .imageView(ivInviteBannerImg)
//                    .build());
//            Timber.d("==share  getBannerImg  :  " + invitePageBean.getBannerImg());
//        }
        rlInvitation.setVisibility(View.VISIBLE);
        llErrorView.setVisibility(View.GONE);
        tvInviteInviteCount.setText(invitePageBean.getInviteCount() + " 人");
        tvInviteTotalIncome.setText(invitePageBean.getTotalIncome() + "");
        AndroidUtil.setTextSizeColor(
                tvInviteTotalIncome,
                new String[]{invitePageBean.getTotalIncome() + "", "金币"},
                new int[]{getResources().getColor(R.color.color_text_red), getResources().getColor(R.color.color_text_tip)},
                new int[]{14, 14});
        tvInviteInviteProfitText.setText(text(invitePageBean.getInviteProfitText()));
        tvInviteAddedProfitText.setText(text(invitePageBean.getAddedProfitText()));
        tvInviteRewardRuleText.setText(text(invitePageBean.getRewardRuleText()));
        tvRewardRule1.setText(invitePageBean.getRewardRule().getDay1() + "");
        tvRewardRule2.setText(invitePageBean.getRewardRule().getDay2() + "");
        tvRewardRule3.setText(invitePageBean.getRewardRule().getDay3() + "");
        tvRewardRule4.setText(invitePageBean.getRewardRule().getDay4() + "");
        tvRewardRule5.setText(invitePageBean.getRewardRule().getDay5() + "");
        tvRewardRule6.setText(invitePageBean.getRewardRule().getDay6() + "");
        tvRewardRule7.setText(invitePageBean.getRewardRule().getDay7() + "");
    }

    @Override
    public void updateShareData(@NonNull WeChatShareBean weChatShareBean) {
        if (weChatShareBean.getInviteURL() != null) {
            weChatShareBeans = weChatShareBean;

            if (weChatShareBeans.getFaceToFaceInviteImg() != null) {
                imageLoader.loadImage(this, CustomImageConfig.builder()
                        .url(weChatShareBeans.getFaceToFaceInviteImg())
                        .cacheStrategy(1)
                        .errorPic(R.drawable.icon_invitation_code)
                        .placeholder(R.drawable.icon_invitation_code)
                        .imageView(ivInviteRQcodeImg)
                        .build());
                Timber.d("==share  getFaceToFaceInviteImg  :  " + weChatShareBean.getFaceToFaceInviteImg());
            }

            Timber.d("==share  getInviteImages" + weChatShareBean.getInviteImages());

            String[] imgArray = new String[]{};
            imgArray = weChatShareBeans.getInviteImages().toArray(imgArray);
//            assert mPresenter != null;
//            mPresenter.requestPermission(weChatShareBean.getInviteImages().get(0));
            oneKeyShare.setShareContent(new ShareBean.Builder()
                    .title(weChatShareBeans.getInviteTitle())
                    .content(weChatShareBeans.getInviteContent())
                    .imageUrl(weChatShareBeans.getInviteImage())
                    .imagePath(imageUserPath)
                    .pageUrl(weChatShareBeans.getInviteURL())
                    .imgUrls(imgArray)
                    .weChatShareType(weChatShareBeans.getSharingWechat())
                    .weChatMomentsShareType(weChatShareBeans.getSharingWechatCircle())
                    .qqShareType(weChatShareBeans.getSharingQQ())
                    .qZoneShareType(weChatShareBeans.getSharingQqZone())
                    .sinaShareType(weChatShareBeans.getSharingWebo())
                    .inviteCode(weChatShareBean.getInviteCode())
                    .isUserHead(true)
                    .wxAppInviteImage(weChatShareBeans.getWxAppInviteImage())
                    .create());
        }
    }

    /**
     * 设置banner控件的高度
     */
    private void setBannerHeight() {
        int screenWidth = ArmsUtils.getScreenWidth(this);
        int height = (int) (screenWidth * 0.75);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(screenWidth, height);
        autoScrollViewPager.setLayoutParams(params);
    }


    /**
     * 初始化轮播图控件
     */
    private void initAutoScrollViewPager() {
        autoScrollViewPager.setAdapter(mPagerAdapter);

        // viewPagerIndicator.setViewPager(autoScrollViewPager);
        // viewPagerIndicator.setSnap(true);

        autoScrollViewPager.setScrollFactgor(10); // 控制滑动速度
        autoScrollViewPager.setOffscreenPageLimit(5); //设置缓存屏数
        autoScrollViewPager.startAutoScroll(3000);  //设置间隔时间

        autoScrollViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                showSelectScrollImage(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    /**
     * 当前滑动的轮播图对应底部的标识
     *
     * @param position 当前位置
     */
    private void showSelectScrollImage(int position) {
        if (position < 0 || position >= mScrollImageViews.size()) return;
        if (mScrollImageViews != null) {
            for (ImageView iv : mScrollImageViews) {
                iv.setImageResource(R.drawable.icon_indicator_normal);
            }
            mScrollImageViews.get(position).setImageResource(R.drawable.icon_indicator_selected);
        }
    }

    /**
     * 轮播图底部的滑动的下划线
     *
     * @param size 轮播图数量
     */
    private void addScrollImage(int size) {
        autoScrollIndicator.removeAllViews();
        mScrollImageViews.clear();

        for (int i = 0; i < size; i++) {
            ImageView iv = new ImageView(this);
            iv.setPadding(10, 0, 10, 20);
            if (i != 0) {
                iv.setImageResource(R.drawable.icon_indicator_normal);
            } else {
                iv.setImageResource(R.drawable.icon_indicator_selected);
            }
            iv.setLayoutParams(new ViewGroup.LayoutParams(40, 40));
            if (size >= 2) {  //如果图片只有一张 则不显示下面的红点
                autoScrollIndicator.addView(iv);// 将图片加到显示红点布局里
            }
            mScrollImageViews.add(iv);
        }
    }

    /**
     * 轮播图适配器
     */
    PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return mScrollImageViews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
            View view = getLayoutInflater().inflate(R.layout.include_image, null);
            ImageView ivBanner = view.findViewById(R.id.bannerImg);

            GlideArms.with(ivBanner.getContext()).load(bannerBeans.get(position).getImg())
                    .centerCrop().error(R.drawable.icon_dzkd_banner).into(ivBanner);
            container.addView(view);

            view.setOnClickListener(v -> {
                if (!TextUtils.isEmpty(bannerBeans.get(position).getEvent())) {
                    skipType(bannerBeans.get(position).getEvent(), bannerBeans.get(position).getUrl());
                }
            });
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    };

    public void skipType(String type, String url) {
        switch (type) {
            case "innerJump":  //内部跳转
                if (!TextUtils.isEmpty(url)) {
                    Intent innerIntent = new Intent(InvitationActivity.this, WebViewActivity.class);
                    innerIntent.putExtra("URL", url);
                    startActivity(innerIntent);
                }
                break;
            case "outnerJump": //外部跳转
                if (!TextUtils.isEmpty(url))
                    launchActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                break;
            case "invitation": //好友邀请
                launchActivity(new Intent(this, InvitationActivity.class));
                break;
            case "withdrawals": //快速提现
                launchActivity(new Intent(this, QuickCashActivity.class));
                break;
            case "activityPage":  //活动公告页
                Intent activityIntent = new Intent(this, MessageCenterActivity.class);
                activityIntent.putExtra("tab", 0);
                launchActivity(activityIntent);
                break;
            case "myMsgPage":   //我的消息页面
                Intent messageIntent = new Intent(this, MessageCenterActivity.class);
                messageIntent.putExtra("tab", 1);
                launchActivity(messageIntent);
                break;
            case "taskCenter": //任务中心
                EventBus.getDefault().post(new ChangeTabEvent.Builder().indexTab(2).build(), EventBusTags.TAG_CHANGE_TAB);
                killMyself();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (autoScrollViewPager != null)
            autoScrollViewPager.startAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (autoScrollViewPager != null)
            autoScrollViewPager.stopAutoScroll();
    }


    @Override
    public void banner(List<BannerBean> bannerBean) {
        bannerBeans = bannerBean;
        addScrollImage(bannerBean.size());
        initAutoScrollViewPager();
    }

    @Override
    public void downloadCallBack(@NonNull String filePath) {
        if (!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
            imagePath = filePath;
            Timber.d("==share   imagePath: " + imagePath);
        }
    }

    /*字体颜色转换*/
    private Spanned text(String str) {
        return Html.fromHtml(str);
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }

    @OnClick({R.id.b_invite, R.id.ll_invite_chenggong, R.id.ll_invite_zongshouyi})
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.b_invite:
                if (System.currentTimeMillis() - clickInvitationTimes > 2000) {
                    clickInvitationTimes = System.currentTimeMillis();
                    if (isInternet() && weChatShareBeans != null) {
                        oneKeyShare.show(api);
                    } else {
                        showMessage("网络请求失败，请连网后重试");
                    }
                }
                break;
            case R.id.ll_invite_chenggong:
                launchActivity(new Intent(this, InviteDiscipleActivity.class));
                break;
            case R.id.ll_invite_zongshouyi:
                launchActivity(new Intent(this, InviteEarningsActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
        if (!TextUtils.isEmpty(imagePath)) {
            File file = new File(imagePath);
            if (file.exists()) {
                file.delete();
            }
        }
        super.onDestroy();
//        ShareUtils.destroy();
        oneKeyShare.destroy();
        imageLoader = null;
        if (api != null) {
            api.detach();
        }
    }
}
