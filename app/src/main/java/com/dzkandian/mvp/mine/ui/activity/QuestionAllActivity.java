package com.dzkandian.mvp.mine.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.mine.contract.QuestionAllContract;
import com.dzkandian.mvp.mine.di.component.DaggerQuestionAllComponent;
import com.dzkandian.mvp.mine.di.module.QuestionAllModule;
import com.dzkandian.mvp.mine.presenter.QuestionAllPresenter;
import com.dzkandian.mvp.mine.ui.adapter.QuestionAllAdapter;
import com.dzkandian.storage.bean.mine.QuestionAllBean;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 常见问题
 * 一次性获取所有问题列表Activity
 */
public class QuestionAllActivity extends BaseActivity<QuestionAllPresenter> implements QuestionAllContract.View {

    @Nullable
    @BindView(R.id.expand_list)
    ExpandableListView expandListView;
    @Nullable
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Nullable
    @BindView(R.id.tv_error)
    TextView tvError;
    @Nullable
    @BindView(R.id.ll_error_view)
    LinearLayout llErrorView;
    @Nullable
    private LoadingProgressDialog loadingProgressDialog;
    private long updateQuestionAllLastTimes;//常见问题 刷新的上一次时间；

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerQuestionAllComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .questionAllModule(new QuestionAllModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_question_all; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(R.string.mine_question);
        toolbar.setNavigationOnClickListener(v -> killMyself());

        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setDisableContentWhenRefresh(true);
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            if (isInternet()) {
                if (mPresenter != null) {
                    mPresenter.questionAll();
                }
            } else {
                if (System.currentTimeMillis() - updateQuestionAllLastTimes > 2000) {
                    updateQuestionAllLastTimes = System.currentTimeMillis();
                    showMessage("网络请求失败，请连网后重试");
                    refreshFailed();
                }
                finishRefresh();//隐藏刷新
            }
        });
        refreshLayout.autoRefresh();
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
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
     * 刷新失败（网络异常）
     */
    @Override
    public void refreshFailed() {
        llErrorView.setVisibility(View.VISIBLE);
        tvError.setText(R.string.error_network);
        tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                tvError.getContext().getResources().getDrawable(R.drawable.icon_error_network),
                null, null);
        expandListView.setVisibility(View.GONE);
    }

    /**
     * 结束刷新
     */
    @Override
    public void finishRefresh() {
        refreshLayout.finishRefresh();
    }

    /**
     * 更新列表
     *
     * @param list 所有常见问题
     */
    @Override
    public void updateListView(List<QuestionAllBean> list) {
        llErrorView.setVisibility(View.GONE);
        expandListView.setVisibility(View.VISIBLE);
        QuestionAllAdapter questionAllAdapter = new QuestionAllAdapter(QuestionAllActivity.this, list);
        expandListView.setAdapter(questionAllAdapter);
        expandListView.setGroupIndicator(null);
        expandListView.setOnGroupClickListener((expandableListView, view, i, l) -> true);
        for (int i = 0; i < expandListView.getCount(); i++) {
            expandListView.expandGroup(i);
        }
    }

}
