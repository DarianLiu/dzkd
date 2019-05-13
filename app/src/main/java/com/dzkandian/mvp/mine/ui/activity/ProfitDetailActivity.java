package com.dzkandian.mvp.mine.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.mine.contract.ProfitDetailContract;
import com.dzkandian.mvp.mine.di.component.DaggerProfitDetailComponent;
import com.dzkandian.mvp.mine.di.module.ProfitDetailModule;
import com.dzkandian.mvp.mine.presenter.ProfitDetailPresenter;
import com.dzkandian.mvp.mine.ui.adapter.TaskRecordAdapter;
import com.dzkandian.storage.bean.mine.TaskRecordBean;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 收益明细
 */
public class ProfitDetailActivity extends BaseActivity<ProfitDetailPresenter> implements ProfitDetailContract.View {

    @Nullable
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @Nullable
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private List<TaskRecordBean> taskRecordBeans;
    private TaskRecordAdapter taskRecordAdapter;
    @Nullable
    private LoadingProgressDialog loadingProgressDialog;

    private int haveTouchHardware;//是否有触摸硬件（触摸面积不为0）
    private String first_touch_area;
    private long refreshProfitLastTimes;//收益明细 刷新 的上一次时间；
    private long loadMoreProfitLastTimes;//收益明细 加载 的上一次时间；

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerProfitDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .profitDetailModule(new ProfitDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_profit_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(R.string.title_record);
        toolbar.setNavigationOnClickListener(v -> finish());

        //是否有触摸硬件（触摸面积不为0）
        haveTouchHardware = DataHelper.getIntergerSF(getApplicationContext(), Constant.SP_KEY_HAVE_TOUCH_HARDWARE);

        first_touch_area = DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_TOUCH_AREA);

        initRecyclerView();
        checkTouchHardware();

        refreshLayout.setEnableLoadMore(true);//开启加载更多
        refreshLayout.setDisableContentWhenRefresh(true);//刷新时禁止滑动
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isInternet()) {
                    if (mPresenter != null) {
                        mPresenter.questTaskRecord(false);
                    }
                } else {
                    if (System.currentTimeMillis() - loadMoreProfitLastTimes > 2000) {
                        loadMoreProfitLastTimes = System.currentTimeMillis();
                        showMessage("网络请求失败，请连网后重试");
                    }
                    finishLoadMore();//隐藏加载更多
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (isInternet()) {
                    if (mPresenter != null) {
                        refreshLayout.setNoMoreData(false);
                        mPresenter.questTaskRecord(true);
                    }
                } else {
                    if (System.currentTimeMillis() - refreshProfitLastTimes > 2000) {
                        refreshProfitLastTimes = System.currentTimeMillis();
                        showMessage("网络请求失败，请连网后重试");
                        refreshFailed(true);
                    }
                    finishRefresh();//隐藏刷新
                }
            }
        });
        refreshLayout.autoRefresh();
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }

    /**
     * 检测触摸硬件
     */
    private void checkTouchHardware() {
        if (haveTouchHardware != 1) {
            mRecyclerView.setOnTouchListener((v, event) -> {
                float touch_area = event.getSize();
                Timber.d("========touch area：" + touch_area);
                if (touch_area > 0 && touch_area != 1 && haveTouchHardware != 1) {
                    if (TextUtils.isEmpty(first_touch_area)) {
                        DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_TOUCH_AREA, String.valueOf(touch_area));
                    } else if (!TextUtils.equals(first_touch_area, String.valueOf(touch_area))) {
                        haveTouchHardware = 1;
                        DataHelper.setIntergerSF(getApplicationContext(), Constant.SP_KEY_HAVE_TOUCH_HARDWARE, haveTouchHardware);
                    }
                }
                return false;
            });
        }
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        taskRecordBeans = new ArrayList<>();
        taskRecordAdapter = new TaskRecordAdapter(taskRecordBeans);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.shape_item_line));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(taskRecordAdapter);
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
    protected void onDestroy() {
        super.onDestroy();
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
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
     * 刷新失败
     *
     * @param isShowError 是否显示异常布局
     */
    @Override
    public void refreshFailed(boolean isShowError) {
        taskRecordAdapter.showErrorView(isShowError);
    }

    /**
     * 结束刷新状态
     */
    @Override
    public void finishRefresh() {
        refreshLayout.finishRefresh();
    }

    /**
     * 结束加载更多状态
     */
    @Override
    public void finishLoadMore() {
        refreshLayout.finishLoadMore();
    }

    /**
     * 刷新数据
     */
    @Override
    public void refreshData(@NonNull List<TaskRecordBean> questTaskRecord) {
        taskRecordBeans.clear();
        taskRecordBeans.addAll(questTaskRecord);
        taskRecordAdapter.notifyDataSetChanged();
    }

    /**
     * 加载更多数据
     */
    @Override
    public void loadMoreData(@NonNull List<TaskRecordBean> questTaskRecord) {
        if (questTaskRecord.size() == 0) {
            //无更多数据
            refreshLayout.setNoMoreData(true);
        } else {
            int index = taskRecordBeans.size();
            taskRecordBeans.addAll(questTaskRecord);
            taskRecordAdapter.notifyItemRangeInserted(index, questTaskRecord.size());
        }
    }
}
