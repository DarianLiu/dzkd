package com.dzkandian.mvp.news.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.barrageview.KeyboardStateObserver;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.news.contract.ReplyDetailContract;
import com.dzkandian.mvp.news.di.component.DaggerReplyDetailComponent;
import com.dzkandian.mvp.news.di.module.ReplyDetailModule;
import com.dzkandian.mvp.news.presenter.ReplyDetailPresenter;
import com.dzkandian.mvp.news.ui.adapter.ReplyDetailAdapter;
import com.dzkandian.mvp.news.ui.adapter.ReplyDetailHolder;
import com.dzkandian.storage.bean.news.ReplyBean;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.apache.http.impl.cookie.DateUtils;
import org.simple.eventbus.Subscriber;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class ReplyDetailActivity extends BaseActivity<ReplyDetailPresenter> implements ReplyDetailContract.View {
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.et_input_reply)
    EditText etInputReply;
    @BindView(R.id.bt_reply_send)
    Button btReplySend;
    @Nullable
    private LoadingProgressDialog loadingProgressDialog;

    private ReplyDetailAdapter mReplyDetailAdapter;
    private List<ReplyBean> mReplyList;
    private String userId;            //当前用户Id     必要条件
    private String userAvatar;        //当前用户头像   必要条件
    private String userName;          //当前用户名     必要条件

    private String inputString = "";
    private String aId = "";          //文章id                                              固定值 由intent传入
    private String aType = "";        //文章栏目                                            固定值 由intent传入
    private String aUrl = "";         //文章地址                                            固定值 由intent传入
    private String aTitile = "";      //文章标题                                            固定值 由intent传入
    private String commitFrom = "";   //评论来源：news, video, wuli                         固定值 由intent传入
    private String mParentId = "";    //根评论ID                                            固定值 由intent传入
    private String mParentName = "";  //根评论用户名                                        固定值 由intent传入
    private String lastReplyId = "";  //从我的消息过来的   回复ID   要去回复人的信息        固定值 由intent传入
    private String lastReplyName = "";//从我的消息过来的   回复用户名  要去回复人的信息     固定值 由intent传入

    private String mReplyId = "";//回复某个    评论ID                通用参数
    private String mReplyName = "";//回复某个  评论昵称              通用参数
    private boolean isFirstKeyboar = true;//第一次进入软键盘监听事件
    private boolean isFirstRefresh = true;//第一次刷新列表
    private boolean isPraiseFinish = true;//是否点赞请求结束；
    private boolean noListRefresh;//是否是无网络进行的刷新

    private String lastId;  //加载回复列表时的    最后一条回复的id
    private long loadMoreReplyLastTimes;//回复列表 加载 的上一次时间；
    private long onClickbtSendTime;//上一次点击发送的时间
    private long onClickPraiseTime;//上一次点击点赞按钮的时间
    private long onReplySuccess;//上一次回复成功的时间
    private long onPraiseFull;//上一次点赞满的时间
    private int movePosition; //移动到当前位置

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerReplyDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .replyDetailModule(new ReplyDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_reply_detail;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> {
            hideKeyboard(); //隐藏软键盘
            killMyself();
        });


        getIntent(getIntent());

        initRecyclerView();

        mRefreshLayout.setEnableLoadMore(true); //开启加载更多
        mRefreshLayout.setDisableContentWhenRefresh(true); //刷新时禁止滑动
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isInternet()) {
                    if (mPresenter != null)
                        mPresenter.getReplyList(false, mParentId, commitFrom, lastId);//加载
                } else {
                    if (System.currentTimeMillis() - loadMoreReplyLastTimes > 2000) {
                        loadMoreReplyLastTimes = System.currentTimeMillis();
                        showMessage("网络连接失败，请重试");
                    }
                    finishLoadMore();//隐藏加载更多
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (isFirstRefresh) {
//                    Timber.d("==reply  onRefresh刷新方法    第一次进入");
                    isFirstRefresh = false;
                } else {
//                    Timber.d("==reply  onRefresh刷新方法 noListRefresh:" + noListRefresh);
                    if (etInputReply != null && !noListRefresh) {
//                        Timber.d("==reply  onRefresh刷新方法    不是第一次进入");
                        etInputReply.setText("");
                        recoveryEditText();
                        etInputReply.setCursorVisible(false);
                        hideKeyboard();
                    }
                }
                if (mPresenter != null) {
                    noListRefresh = false;
                    refreshLayout.setNoMoreData(false);  //刷新时 把没有更新数据恢复到原始状态
                    mPresenter.getReplyList(true, mParentId, commitFrom, "0");//刷新
                }
            }
        });
        mRefreshLayout.autoRefresh();  //自动刷新

        btReplySend.setOnClickListener(view -> {
            if (System.currentTimeMillis() - onClickbtSendTime > 2000) {
                onClickbtSendTime = System.currentTimeMillis();
                inputString = etInputReply.getText().toString();
                if (TextUtils.isEmpty(inputString)) {
                    showMessage("回复不能为空");
                } else {
                    if (isInternet()) {
                        if (mReplyList != null && mReplyList.size() > 0) {
                            try {
                                inputString = etInputReply.getText().toString();
                                inputString = inputString.replaceAll("\n", "  ");
                                String commentString = URLEncoder.encode(inputString, "UTF-8");
                                String urlEncode = URLEncoder.encode(aUrl, "UTF-8");
                                String reqId = DateUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + (int) ((Math.random() * 9 + 1) * 100000);
                                mReplyId = TextUtils.isEmpty(mReplyId) ? "" : mReplyId;
                                mReplyName = TextUtils.isEmpty(mReplyName) ? "" : mReplyName;
                                if (mPresenter != null)
                                    mPresenter.foundReply(inputString, commentString, commitFrom, "0", aId, aType, urlEncode, aTitile, reqId, mParentId, mReplyId, mReplyName);//回复
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            noListRefresh = true;
                            hideKeyboard();
                            mRefreshLayout.autoRefresh();
                        }
                    } else {
                        showMessage("网络连接失败，请重试");
                    }
                }
            }
        });

        etInputReply.addTextChangedListener(textWatcher);

        KeyboardStateObserver.getKeyboardStateObserver(this).setKeyboardVisibilityListener(new KeyboardStateObserver.OnKeyboardVisibilityListener() {
            @Override
            public void onKeyboardShow() {
                if (mRecyclerView != null)
                    mRecyclerView.scrollToPosition(movePosition);
//                Timber.d("==reply  监听方法  软件盘弹起");
                if (etInputReply != null) {
                    etInputReply.setCursorVisible(true);
                }
            }

            @Override
            public void onKeyboardHide() {
                if (isFirstKeyboar) {
                    isFirstKeyboar = false;
                    if (etInputReply != null) {
                        if (TextUtils.isEmpty(lastReplyId)) {
//                            Timber.d("==reply  软件盘缩回监听方法    第一次进入   lastReplyId  == null");
                            recoveryEditText();
                        } else {
//                            Timber.d("==reply  软件盘缩回监听方法    第一次进入   lastReplyId  != null");
                            mReplyId = lastReplyId;
                            mReplyName = lastReplyName;
                            etInputReply.setHint("回复@" + mReplyName);
                        }
                        etInputReply.setCursorVisible(false);
                    }
                } else {
                    if (etInputReply != null && etInputReply.getText().length() == 0) {
//                        Timber.d("==reply  软件盘缩回监听方法    不是第一次进入");
                        recoveryEditText();
                        etInputReply.setCursorVisible(false);
                    }
                }
            }
        });
    }

    /**
     * @param intent 传输数据
     */
    private void getIntent(Intent intent) {
        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("userName");
        userAvatar = intent.getStringExtra("userAvatar");
        aId = intent.getStringExtra("aId");
        aType = intent.getStringExtra("aType");
        aTitile = intent.getStringExtra("aTitile");
        aUrl = intent.getStringExtra("aUrl");
        commitFrom = intent.getStringExtra("commitFrom");
        mParentId = intent.getStringExtra("parentId");
        mParentName = intent.getStringExtra("parentName");
        lastReplyId = intent.getStringExtra("lastReplyId");
        lastReplyName = intent.getStringExtra("lastReplyName");
        Timber.d("==reply  intent"
                + "\n  userId:" + userId
                + "\n  userName:" + userName
                + "\n  userAvatar:" + userAvatar
                + "\n  aId:" + aId
                + "\n  aType:" + aType
                + "\n  aTitile:" + aTitile
                + "\n  aUrl:" + aUrl
                + "\n  commitFrom:" + commitFrom
                + "\n  parentId:" + mParentId
                + "\n  parentName:" + mParentName
                + "\n  lastReplyId:" + lastReplyId
                + "\n  lastReplyName:" + lastReplyName
        );
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().length() > 0 && (charSequence.toString().substring(0, 1).equals(" ")
                    || charSequence.toString().substring(0, 1).equals("\n"))) {
                if (etInputReply != null)
                    etInputReply.setText("");
            }
            if (etInputReply != null && btReplySend != null && !TextUtils.isEmpty(etInputReply.getText().toString())) {
                btReplySend.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else if (etInputReply != null && btReplySend != null && TextUtils.isEmpty(etInputReply.getText().toString())) {
                btReplySend.setTextColor(getResources().getColor(R.color.color_text_tip));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        mReplyList = new ArrayList<>();
        mReplyDetailAdapter = new ReplyDetailAdapter(mReplyList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mReplyDetailAdapter);

        mRecyclerView.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard();
            return false;
        });
    }

    /**
     * 滑动列表 -- 隐藏软键盘
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            if (this.getCurrentFocus() != null) {
//                Timber.d("==reply  滑动列表  hideKeyboard：隐藏软键盘");
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 恢复输入框状态；
     */
    private void recoveryEditText() {
        mReplyId = "0";
        mReplyName = "";
        etInputReply.setHint("快来说说你的看法");
    }

    /**
     * 点击 某条回复
     */
    @Subscriber(tag = EventBusTags.TAG_REPLY_CLICK_POSITION)
    public void clickReply(int position) {
        this.movePosition = position;
//        Timber.d("==reply  事件接收 点击了第  " + position + "  条回复：");
        mReplyId = position == 0 ? "0" : mReplyList.get(position).getId();
        mReplyName = mReplyList.get(position).getUserName();
        if (etInputReply != null) {
            etInputReply.setText("");
            etInputReply.setHint("回复@" + mReplyName);
            etInputReply.requestFocus();
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.showSoftInput(etInputReply, 0);
        }
    }

    /**
     * 点击 某条的点赞按钮
     */
    @Subscriber(tag = EventBusTags.TAG_REPLY_CLICK_PRAISE)
    public void clickPraise(int position) {
        if (mReplyList.get(position).isStatus()) {
//        Timber.d("==reply  事件接收 点赞第  " + position + "  条回复：");
            if (isPraiseFinish) {
                isPraiseFinish = false;
//            Timber.d("==reply  事件接收 点赞第  " + position + "  条回复：已响应");
                if (isInternet()) {
                    String commId = mReplyList.get(position).getId();
                    if (mPresenter != null) {
                        mPresenter.foundPraise(position, commId, commitFrom);
                    }
                } else {
                    isPraiseFinish = true;
                    if (System.currentTimeMillis() - onClickPraiseTime > 2000) {
                        onClickPraiseTime = System.currentTimeMillis();
                        showMessage("网络连接失败，请重试");
                    }
                }
            }
        } else {
            if (System.currentTimeMillis() - onPraiseFull > 2000) {
                onPraiseFull = System.currentTimeMillis();
                showMessage("点赞太多啦");
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
        hideKeyboard(); //隐藏软键盘
        if (textWatcher != null && etInputReply != null) {
            etInputReply.removeTextChangedListener(textWatcher);
            textWatcher = null;
        }
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
            mRecyclerView.setOnTouchListener(null);
        }
        super.onDestroy();
    }

    /**
     * 刷新失败
     */
    @Override
    public void refreshFailed(boolean isShowError) {
        mReplyDetailAdapter.showErrorView(isShowError);
    }

    /**
     * 结束刷新状态
     */
    @Override
    public void finishRefresh() {
        mRefreshLayout.finishRefresh();
    }

    /**
     * 结束加载更多状态
     */
    @Override
    public void finishLoadMore() {
        mRefreshLayout.finishLoadMore();
    }

    /**
     * 刷新数据
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void refreshData(List<ReplyBean> replyList) {
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(replyList.get(0).getSubReplyCount() + " 条回复");
        }
        lastId = replyList.get(replyList.size() - 1).getId();
        mReplyList.clear();
        mReplyList.addAll(replyList);
        mReplyDetailAdapter.notifyDataSetChanged();
    }

    /**
     * 加载更多数据
     */
    @Override
    public void loadMoreData(List<ReplyBean> replyList) {
        if (replyList.size() == 0) {
            mRefreshLayout.setNoMoreData(true);
        } else {
            lastId = replyList.get(replyList.size() - 1).getId();
            int size = mReplyList.size();
            mReplyList.addAll(replyList);
            mReplyDetailAdapter.notifyItemRangeInserted(size, replyList.size());
        }
    }

    /**
     * 回复成功
     *
     * @param input     回复内容(未编码的)直接用于展示
     * @param content   回复内容(有编码的)
     * @param parentId  根评论ID，
     * @param replyId   回复评论ID，
     * @param replyName 回复评论昵称
     */
    @Override
    public void replySuccess(String input, String content, String parentId, String replyId, String replyName) {
        if (System.currentTimeMillis() - onReplySuccess > 2000) {
            onReplySuccess = System.currentTimeMillis();
            showMessage("回复成功");
        }
        mReplyId = "0";
        mReplyName = "";
        if (etInputReply != null) {
            etInputReply.setText("");
            etInputReply.setHint("快来说说你的看法");
            etInputReply.setCursorVisible(false);
        }
        ReplyBean replyBean = new ReplyBean();
        replyBean.setBogusData(true);
        replyBean.setContent(input);
        replyBean.setUserId(userId);
        replyBean.setUserName(userName);
        replyBean.setUserImg(userAvatar);
        replyBean.setCreateTime("刚刚");
        replyBean.setThumbsUp(0);
        replyBean.setSubReplyCount(0);
        replyBean.setParentId(parentId);
        replyBean.setReplyId(replyId);
        replyBean.setReplyName(replyName);
        mReplyList.add(1, replyBean);
        mReplyDetailAdapter.notifyDataSetChanged();
    }


    /**
     * 点赞成功
     *
     * @param isSuccess     请求是否成功
     * @param position      第几条
     * @param validThumbsUp 这一条还有几次点赞
     */
    @Override
    public void praiseResult(boolean isSuccess, int position, int validThumbsUp) {
        isPraiseFinish = true;
        int thumbsUp = mReplyList.get(position).getThumbsUp();//点赞数量
        boolean isStatus;                                     //是否可以再点赞
        boolean isAnimated = false;                           //是否有点赞动画效果
        boolean isHasPraise = false;                          //是否有点赞成功过
        if (isSuccess && validThumbsUp >= 0) {
            if (validThumbsUp == 0) {
                isStatus = false;
            } else {
                isStatus = true;
            }
            thumbsUp++;
            isAnimated = true;
            isHasPraise = true;
        } else if (isSuccess && validThumbsUp == -1) {
            if (System.currentTimeMillis() - onPraiseFull > 2000) {
                onPraiseFull = System.currentTimeMillis();
                showMessage("点赞太多啦");
            }
            isStatus = false;
        } else {
            isStatus = true;
        }
        mReplyList.get(position).setThumbsUp(thumbsUp);
        mReplyList.get(position).setStatus(isStatus);
        mReplyList.get(position).setAnimated(isAnimated);
        mReplyList.get(position).setHasPraise(isHasPraise);
        if (mRecyclerView != null) {
            ReplyDetailHolder holder = (ReplyDetailHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                holder.updateView(mReplyList.get(position));
            }
        }
        mReplyList.get(position).setAnimated(false);
    }
}
