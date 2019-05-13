package com.dzkandian.mvp.news.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.news.contract.SearchContract;
import com.dzkandian.mvp.news.di.component.DaggerSearchComponent;
import com.dzkandian.mvp.news.di.module.SearchModule;
import com.dzkandian.mvp.news.presenter.SearchPresenter;
import com.dzkandian.mvp.news.ui.adapter.SearchAdapter;
import com.dzkandian.mvp.news.ui.adapter.SearchHistoryAdapter;
import com.dzkandian.storage.bean.SearchBean;
import com.dzkandian.storage.bean.SearchHistoryBean;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class SearchActivity extends BaseActivity<SearchPresenter> implements SearchContract.View {

    @BindView(R.id.iv_back_search)
    ImageView ivBackSearch;
    @BindView(R.id.et_title_search)
    EditText etTitleSearch;
    @BindView(R.id.iv_clean_search)
    ImageView ivCleanSearch;
    @BindView(R.id.tv_title_search)
    TextView tvTitleSearch;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private SearchHistoryAdapter mSearchHistoryAdapter;//搜索历史适配器；
    private SearchAdapter mSearchAdapter;//搜索数据适配器；
    List<SearchBean> mSearchList;//搜索数据集合
    private String mSearchKey;//搜索关键字；

    private long clickSearchLastTimes;//点击搜索 的上一次时间；
    private long refreshSearchLastTimes;//搜索列表 刷新 的上一次时间；
    private long loadMoreSearchLastTimes;//搜索列表 加载 的上一次时间；
    private LoadingProgressDialog loadingProgressDialog;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerSearchComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .searchModule(new SearchModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_search; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    /**
     * 搜索框文字变化监听
     */
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (etTitleSearch != null && TextUtils.isEmpty(etTitleSearch.getText().toString())) {
                ivCleanSearch.setVisibility(View.GONE);
            } else {
                ivCleanSearch.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        //得到当前界面的装饰视图
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //设置让应用主题内容占据状态栏和导航栏
            int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            //        SYSTEM_UI_FLAG_FULLSCREEN表示全屏的意思，也就是会将状态栏隐藏
            //设置系统UI元素的可见性
            decorView.setSystemUiVisibility(option);

            //设置状态栏颜色
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            //设置导航栏颜色
            getWindow().setNavigationBarColor(Color.BLACK);
        }

        mSearchList = new ArrayList<>();
        initRecyclerView();

        mRefreshLayout.setEnableRefresh(false);//禁止下拉刷新
        mRefreshLayout.setEnableLoadMore(false); //禁止上拉加载
        mRefreshLayout.setDisableContentWhenRefresh(true);//刷新时禁止滑动
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!TextUtils.isEmpty(mSearchKey)) {
                    if (mSearchList != null && mSearchList.size() > 0) {
                        long num = mSearchList.get(mSearchList.size() - 1).getBeforeTime();
                        String beforeId = mSearchList.get(mSearchList.size() - 1).getId();
                        Timber.d("==search   onLoadMore   beforeId:" + beforeId);
                        if (isInternet()) {
                            if (mPresenter != null) {
                                mPresenter.getSearchList(false, mSearchKey, num, beforeId);
                            }
                        } else {
                            if (System.currentTimeMillis() - loadMoreSearchLastTimes > 2000) {
                                loadMoreSearchLastTimes = System.currentTimeMillis();
                                showMessage("网络请求失败，请连网后重试");
                            }
                            finishLoadMore();//隐藏加载更多
                        }
                    }
                } else {
                    finishLoadMore();//隐藏加载更多
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!TextUtils.isEmpty(mSearchKey)) {
                    if (isInternet()) {
                        if (mPresenter != null) {
                            mPresenter.getSearchList(true, mSearchKey, 0, "");
                        }
                    } else {
                        if (System.currentTimeMillis() - refreshSearchLastTimes > 2000) {
                            refreshSearchLastTimes = System.currentTimeMillis();
                            showMessage("网络请求失败，请连网后重试");
                            refreshFailed();//onRefresh()
                        }
                        finishRefresh();//隐藏刷新
                    }
                } else {
                    if (System.currentTimeMillis() - refreshSearchLastTimes > 2000) {
                        refreshSearchLastTimes = System.currentTimeMillis();
                        showMessage("请输入搜索内容");
                        finishRefresh();//隐藏刷新
                    }
                }
            }
        });

        //左上角退出按钮
        ivBackSearch.setOnClickListener(v -> {
            hideKeyboard();
            killMyself();
        });

        //搜索点击事件
        tvTitleSearch.setOnClickListener(v -> {
            startSearch();//搜索按钮点击事件
        });

        //清除输入框的关键字
        ivCleanSearch.setOnClickListener(v -> {
            if (etTitleSearch != null) {
                etTitleSearch.setText("");
                etTitleSearch.setSelection(0);
            }
        });

        etTitleSearch.addTextChangedListener(textWatcher);
    }

    /**
     * 初始化RecyclerView
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initRecyclerView() {
        //搜索历史列表
        List<SearchHistoryBean> searchHistory = MyApplication.get().getDaoSession().getSearchHistoryBeanDao().loadAll();
        mSearchHistoryAdapter = new SearchHistoryAdapter(this);
        mSearchHistoryAdapter.setSearchList(searchHistory);

        //搜索列表
        mSearchAdapter = new SearchAdapter(this);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.shape_item_search_line));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mSearchHistoryAdapter);

        mRecyclerView.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard();//mRecyclerView.setOnTouchListener
            return false;
        });
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }

    /**
     * 开始搜索
     */
    private void startSearch() {
        mSearchKey = etTitleSearch.getText().toString();
        if (!TextUtils.isEmpty(mSearchKey) && !mSearchKey.trim().isEmpty()) {
            hideKeyboard();//startSearch()
            mRefreshLayout.setEnableRefresh(true);//打开下拉刷新
            saveSearchKey(mSearchKey);//保存关键字
            //到时这里加上隐藏软键盘
            mRefreshLayout.autoRefresh();
        } else {
            if (etTitleSearch != null) {
                etTitleSearch.setText("");
                etTitleSearch.setSelection(0);
            }
            if (System.currentTimeMillis() - clickSearchLastTimes > 2000) {
                clickSearchLastTimes = System.currentTimeMillis();
                showMessage("请输入搜索内容");
            }
        }
    }

    /**
     * 保存关键字
     *
     * @param searchKey 关键字
     */
    private void saveSearchKey(String searchKey) {
        SearchHistoryBean historyBean = new SearchHistoryBean();
        historyBean.setSearchKey(searchKey);
        List<SearchHistoryBean> searchHistoryBeans = MyApplication.get().getDaoSession().getSearchHistoryBeanDao().loadAll();
        for (int i = 0; i < searchHistoryBeans.size(); i++) {//关键字去重
            if (searchHistoryBeans.get(i).getSearchKey().equals(searchKey)) {
                MyApplication.get().getDaoSession().getSearchHistoryBeanDao().delete(searchHistoryBeans.get(i));
            }
        }
        if (searchHistoryBeans.size() >= 10) {
            MyApplication.get().getDaoSession().getSearchHistoryBeanDao().delete(searchHistoryBeans.get(0));
            MyApplication.get().getDaoSession().getSearchHistoryBeanDao().saveInTx(historyBean);
        } else {
            MyApplication.get().getDaoSession().getSearchHistoryBeanDao().saveInTx(historyBean);
        }
//        mSearchHistoryAdapter.changeSearchList(searchHistoryBeans);
    }

    /**
     * 点击 某条搜索历史
     */
    @Subscriber(tag = EventBusTags.TAG_CLICK_SEARCH_HISTORY)
    public void clickSearchHistory(String searchKey) {
        if (etTitleSearch != null) {
            etTitleSearch.setText(searchKey);
            etTitleSearch.setSelection(TextUtils.isEmpty(searchKey) ? 0 : searchKey.length());
        }
        startSearch();//点击 某条搜索历史
    }

    /**
     * 滑动列表 -- 隐藏软键盘
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            if (this.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
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

    @Override
    protected void onDestroy() {
        etTitleSearch.removeTextChangedListener(textWatcher);
        if (mSearchHistoryAdapter != null) {
            mSearchHistoryAdapter.recycle();
            mSearchHistoryAdapter = null;
        }
        if (mSearchAdapter != null) {
            mSearchAdapter.recycle();
            mSearchAdapter = null;
        }
        if (mSearchList != null) {
            mSearchList.clear();
        }
        super.onDestroy();
    }

    /**
     * 结束刷新
     */
    @Override
    public void finishRefresh() {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh();
        }
    }

    /**
     * 结束加载更多
     */
    @Override
    public void finishLoadMore() {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadMore();
        }
    }

    /**
     * 刷新数据
     *
     * @param list
     */
    @Override
    public void refreshData(List<SearchBean> list) {
        if (mRecyclerView.getAdapter() instanceof SearchHistoryAdapter) {
            mRecyclerView.setAdapter(mSearchAdapter);
        }
        if (list != null && list.size() > 0) {
            mSearchList.clear();
            mSearchList.addAll(list);
            mSearchAdapter.refreshSearch(list);
            if (mRefreshLayout != null) {
                mRefreshLayout.setEnableLoadMore(true); //刷新成功 启用上拉加载
            }
        } else {
            mSearchAdapter.showEmptyView(false);
            if (mRefreshLayout != null) {
                mRefreshLayout.setEnableLoadMore(false);  //显示异常布局 禁用上拉加载
            }
        }
    }

    /**
     * 加载更多添加数据
     *
     * @param list
     */
    @Override
    public void loadMoreData(List<SearchBean> list) {
        if (list.size() == 0) {
            mRefreshLayout.setNoMoreData(true);
        } else {
            mSearchList.addAll(list);
            mSearchAdapter.loadMoreSearch(list);
        }
    }

    /**
     * 刷新失败
     */
    @Override
    public void refreshFailed() {
        if (mRecyclerView.getAdapter() instanceof SearchHistoryAdapter) {
            mRecyclerView.setAdapter(mSearchAdapter);
        }
        mSearchAdapter.showEmptyView(true);
        if (mRefreshLayout != null) {
            mRefreshLayout.setEnableLoadMore(false);  //显示异常布局 禁用上拉加载
        }
    }
}
