package com.dzkandian.mvp.news.ui.activity;

import android.content.Context;
import android.content.Intent;
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
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.barrageview.KeyboardStateObserver;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.news.contract.NewsCommentContract;
import com.dzkandian.mvp.news.di.component.DaggerNewsCommentComponent;
import com.dzkandian.mvp.news.di.module.NewsCommentModule;
import com.dzkandian.mvp.news.presenter.NewsCommentPresenter;
import com.dzkandian.mvp.news.ui.adapter.CommentAdapter;
import com.dzkandian.mvp.news.ui.adapter.CommentViewHolder;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.bean.news.CommentBean;
import com.dzkandian.storage.bean.news.CommentRecordBean;
import com.dzkandian.storage.event.ThumbsUpEvent;
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


public class NewsCommentActivity extends BaseActivity<NewsCommentPresenter> implements NewsCommentContract.View {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.et_input_comment)
    EditText etInputComment;
    @BindView(R.id.bt_send)
    Button btSend;

    @Nullable
    private LoadingProgressDialog mLoadingProgressDialog;

    private CommentAdapter mCommentAdapter;
    private List<CommentBean> mCommentList;

    private String mId = "";     //	文章id
    private String mType = "";   //	文章栏目
    private String mLastId = "";  //最后一条评论id
    private String mTitile = ""; // 文章标题
    private String mUrl = "";    //文章地址
    private UserInfoBean mUserInfo;  //用户信息
    private String mInputString = "";
    private long mRefreshNewsCommentLastTimes;//新闻评论 刷新 的上一次时间；
    private long mLoadMoreNewsCommentLastTimes;//新闻评论 加载 的上一次时间；
    private long mOnClickbtSendTime;//上一次点击发送的时间
    private long mOnClickPraiseTime;//上一次点击点赞按钮的时间
    private String mCommitFrom = "";
    private long mParentId = 0;
    private int mReplyPosition = 0;
    private boolean mIsFirstRefresh = true;
    private boolean mIsThumbsUpFinish = true;
    private int movePosition;//移动到当前位置
    private int mThumbsUpPosition = 0; //上一次点赞的位置

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerNewsCommentComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .newsCommentModule(new NewsCommentModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_news_comment; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> killMyself());

        Intent intent = getIntent();
        mId = intent.getStringExtra("Id");
        mType = intent.getStringExtra("Type");
        mTitile = intent.getStringExtra("Title");
        mUrl = intent.getStringExtra("Url");
        mCommitFrom = intent.getStringExtra("commitFrom");

        if (TextUtils.equals("news", mCommitFrom)) {
            tvToolbarTitle.setText(R.string.title_news_comment);
        } else if (TextUtils.equals("wuli", mCommitFrom)) {
            tvToolbarTitle.setText(R.string.title_short_video_comment);
        } else {
            tvToolbarTitle.setText(R.string.title_video_comment);
        }
        queryUserInfo();//进入界面获取数据库的用户信息；
        initRecyclerView();

        mRefreshLayout.setEnableLoadMore(true); //开启加载更多
        mRefreshLayout.setDisableContentWhenRefresh(true); //刷新时禁止滑动
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isInternet()) {
                    if (mPresenter != null)
                        mPresenter.newsCommentRecord(false, mId, mType, mCommitFrom, 10, mLastId);
                } else {
                    if (System.currentTimeMillis() - mLoadMoreNewsCommentLastTimes > 2000) {
                        mLoadMoreNewsCommentLastTimes = System.currentTimeMillis();
                        showMessage("网络连接失败，请重试");
                    }
                    finishLoadMore();//隐藏加载更多
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (mIsFirstRefresh) {
                    mIsFirstRefresh = false;
                } else {
                    if (etInputComment != null) {
                        recoverEtCommentStatus();
                        etInputComment.setCursorVisible(false);
                        hideKeyboard();
                    }
                }
                if (isInternet()) {
                    if (mPresenter != null) {
                        refreshLayout.setNoMoreData(false);  //刷新时 把没有更新数据恢复到原始状态
                        mPresenter.newsCommentRecord(true, mId, mType, mCommitFrom, 10, "");
                    }
                } else {
                    if (System.currentTimeMillis() - mRefreshNewsCommentLastTimes > 2000) {
                        mRefreshNewsCommentLastTimes = System.currentTimeMillis();
                        showMessage("网络连接失败，请重试");
                        refreshFailed(true);
                    }
                    finishRefresh();//隐藏刷新
                }
            }
        });
        mRefreshLayout.autoRefresh();  //自动刷新

        etInputComment.addTextChangedListener(textWatcher);

        etInputComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //判断是否是“完成”键
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mInputString = etInputComment.getText().toString();
                    if (TextUtils.isEmpty(mInputString)) {
                        showMessage("评论不能为空");
                        return true;
                    }
                    if (isInternet()) {
                        //编码后的url
                        try {
                            mInputString = etInputComment.getText().toString();
                            mInputString = mInputString.replaceAll("\n", "  ");
                            String commentString = URLEncoder.encode(mInputString, "UTF-8");
                            String urlEncode = URLEncoder.encode(mUrl, "UTF-8");
                            String reqId = DateUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + (int) ((Math.random() * 9 + 1) * 100000);
                            assert mPresenter != null;
                            reqId = TextUtils.isEmpty(reqId) ? "" : reqId;
                            mPresenter.foundComment(commentString, mCommitFrom, 0, mId, mType, urlEncode, mTitile, reqId, mParentId, 0, "");

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        return true;
                    } else {
                        showMessage("网络连接失败，请重试");
                    }
                }
                return false;
            }
        });

        KeyboardStateObserver.getKeyboardStateObserver(this).setKeyboardVisibilityListener(new KeyboardStateObserver.OnKeyboardVisibilityListener() {
            @Override
            public void onKeyboardShow() {
                if (mRecyclerView != null) {
                    mRecyclerView.scrollToPosition(movePosition);
                    RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(mThumbsUpPosition);
                    if(viewHolder instanceof CommentViewHolder && viewHolder != null){
                       ((CommentViewHolder) viewHolder).cancelPlusAnimate();
                    }

                }
                if (etInputComment != null) {
                    etInputComment.setCursorVisible(true);
                }
            }

            @Override
            public void onKeyboardHide() {
                if (etInputComment != null) {
                    if (etInputComment.getText().toString().length() <= 0) {
                        recoverEtCommentStatus();
                        etInputComment.setCursorVisible(false);

                    }
                }
                if (mRecyclerView != null) {
                    RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(mThumbsUpPosition);
                    if(viewHolder instanceof CommentViewHolder && viewHolder != null){
                        ((CommentViewHolder) viewHolder).cancelPlusAnimate();
                    }
                }

            }
        });
    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().length() > 0 && (charSequence.toString().substring(0, 1).equals(" ")
                    || charSequence.toString().substring(0, 1).equals("\n"))) {
                if (etInputComment != null)
                    etInputComment.setText("");
            }
            if (etInputComment != null && btSend != null && !TextUtils.isEmpty(etInputComment.getText().toString())) {
                btSend.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else if (etInputComment != null && btSend != null && TextUtils.isEmpty(etInputComment.getText().toString())) {
                btSend.setTextColor(getResources().getColor(R.color.color_text_tip));
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
        mCommentList = new ArrayList<>();
        mCommentAdapter = new CommentAdapter(mCommentList,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.shape_item_line));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mCommentAdapter);

        mRecyclerView.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard();
            mParentId = 0;
            return false;
        });

        btSend.setOnClickListener(view -> {
            if (System.currentTimeMillis() - mOnClickbtSendTime > 2000) {
                mOnClickbtSendTime = System.currentTimeMillis();
                mInputString = etInputComment.getText().toString();
                if (TextUtils.isEmpty(mInputString)) {
                    showMessage("评论不能为空");
                } else {
                    if (isInternet()) {
                        //编码后的url
                        try {
                            mInputString = etInputComment.getText().toString();
                            mInputString = mInputString.replaceAll("\n", "  ");
                            String commentString = URLEncoder.encode(mInputString, "UTF-8");
                            String urlEncode = URLEncoder.encode(mUrl, "UTF-8");
                            String reqId = DateUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + (int) ((Math.random() * 9 + 1) * 100000);
                            assert mPresenter != null;
                            mPresenter.foundComment(commentString, mCommitFrom, 0, mId, mType, urlEncode, mTitile, reqId, mParentId, 0, "");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showMessage("网络连接失败，请重试");
                    }
                }
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            killMyself();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 隐藏软键盘
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
        if (mLoadingProgressDialog == null)
            mLoadingProgressDialog = new LoadingProgressDialog.Builder(this).create();
        mLoadingProgressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (mLoadingProgressDialog != null && mLoadingProgressDialog.isShowing())
            mLoadingProgressDialog.dismiss();
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
//        Intent data = new Intent();
//        data.putExtra("reText", etInputComment.getText().toString());
//        setResult(300, data); //设置返回数据
        finish();
    }

    @Override
    protected void onDestroy() {
        hideKeyboard(); //隐藏软键盘
        if (textWatcher != null && etInputComment != null) {
            etInputComment.removeTextChangedListener(textWatcher);
            textWatcher = null;
        }
        super.onDestroy();
        if (mLoadingProgressDialog != null && mLoadingProgressDialog.isShowing()) {
            mLoadingProgressDialog.dismiss();
            mLoadingProgressDialog = null;
            mRecyclerView.setOnTouchListener(null);
        }
    }

    /**
     * 刷新失败
     *
     * @param isShowError 是否显示异常布局
     */
    @Override
    public void refreshFailed(boolean isShowError) {
        mCommentAdapter.showErrorView(isShowError);
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
    @Override
    public void refreshData(@NonNull CommentRecordBean questCommentBean) {
        if (mCommentList.size() > 0) {
            mCommentList.clear();
            mCommentAdapter.notifyItemMoved(0, mCommentList.size());
        }

        if (questCommentBean.getCommentList().size() > 0) {
            mLastId = String.valueOf(questCommentBean.getLastId());
            mCommentList.addAll(questCommentBean.getCommentList());
            mCommentAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 加载更多数据
     */
    @Override
    public void loadMoreData(@NonNull CommentRecordBean questCommentBean) {
        if (questCommentBean.getCommentList().size() == 0) {
            //无更多数据
            mRefreshLayout.setNoMoreData(true);
        } else {
            if (mCommentList.size() > 0) {
                mLastId = String.valueOf(questCommentBean.getLastId());
                int size = mCommentList.size();
                mCommentList.addAll(questCommentBean.getCommentList());
                mCommentAdapter.notifyItemRangeInserted(size, questCommentBean.getCommentList().size());
            }
        }
    }


    /**
     * 获取数据库的用户信息
     */
    private void queryUserInfo() {
        List<UserInfoBean> list = MyApplication.get().getDaoSession().getUserInfoBeanDao().loadAll();
        if (list != null && list.size() > 0) {
            Timber.d("=db=    NewsCommentActivity - UserInfo - query 成功");
            dbUpdateUserInfo(list.get(0));
        } else {
            Timber.d("=db=    NewsCommentActivity - UserInfo - query 失败");
        }
    }

    private void dbUpdateUserInfo(UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            mUserInfo = userInfoBean;
        }
    }

    //点击回复图标拉起edittext
    @Subscriber(tag = EventBusTags.TAG_COMMENT_REPLY_SOMEONE)
    public void setInputTextHint(int position) {
        this.movePosition = position;
        if (mParentId != mCommentList.get(position).getId()) {
            mParentId = mCommentList.get(position).getId();
            mReplyPosition = position;
            if (null != mCommentList.get(position).getUserName()) {
                etInputComment.setHint("回复@" + mCommentList.get(position).getUserName() + ":");
                etInputComment.setText("");
                etInputComment.requestFocus();
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.showSoftInput(etInputComment, 0);
            }
        }
    }

    //点击回复内容框跳转至回复详情页面
    @Subscriber(tag = EventBusTags.TAG_COMMENT_DETAIL)
    public void launchCommentDetailActivity(int position) {
        Intent intent = new Intent(getApplicationContext(), ReplyDetailActivity.class);
        intent.putExtra("userId", String.valueOf(mUserInfo.getUserId()));
        intent.putExtra("userName", mUserInfo.getUsername());
        intent.putExtra("userAvatar", mUserInfo.getAvatar());
        intent.putExtra("aId", mId);
        intent.putExtra("aType", mType);
        intent.putExtra("aTitile", mTitile);
        intent.putExtra("aUrl", mUrl);
        intent.putExtra("commitFrom", mCommitFrom);
        intent.putExtra("parentId", String.valueOf(mCommentList.get(position).getId()));
        intent.putExtra("parentName", mCommentList.get(position).getUserName());
        intent.putExtra("lastReplyId", "");
        intent.putExtra("lastReplyName", "");
        ArmsUtils.startActivity(intent);
    }

    //点击点赞拉起接口
    @Subscriber(tag = EventBusTags.TAG_COMMENT_THUMBS_UP)
    public void sendThumbsUp(ThumbsUpEvent thumbsUpEvent) {
        if(mIsThumbsUpFinish) {
             mIsThumbsUpFinish = false;
            if (mPresenter != null) {
                mPresenter.commentThumbsUp(thumbsUpEvent.getPosition(), thumbsUpEvent.getId(), mCommitFrom);
            }
        }
    }

    //点赞接口回调
    @Override
    public void receiveThumbsUp(Integer validThumbsUp, int position) {
        mIsThumbsUpFinish = true;
        mThumbsUpPosition = position;
        if (validThumbsUp >= 0) {
            mCommentList.get(position).setStatus(false);
            int count = mCommentList.get(position).getThumbsUp();
            count++;
            mCommentList.get(position).setThumbsUp(count);
            mCommentList.get(position).setHasAnimate(true);
            if(validThumbsUp > 0) {
                mCommentList.get(position).setHasThumbs(true);
            }else{
                mCommentList.get(position).setHasThumbs(false);
            }
        } else {
            mCommentList.get(position).setHasThumbs(false);
            ArmsUtils.makeText(this,"点赞太多啦");
        }
        if (mRecyclerView != null) {
            CommentViewHolder viewHolder = (CommentViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
            if (viewHolder != null) {
                viewHolder.updateView(mCommentList.get(position));
            }
        }
        mCommentList.get(position).setHasAnimate(false);
    }


    //评论框恢复原来状态
    private void recoverEtCommentStatus() {
        etInputComment.setHint("快来说说你的看法");
        etInputComment.setText("");
        mParentId = 0;
    }

    /**
     * 评论失败
     */
    @Override
    public void commitFail() {
        showMessage("网络连接失败，请重试");
    }

    /**
     * 评论成功
     */
    @Override
    public void commentSuccess() {
        //回复文章，parentId为0
        if (mParentId == 0) {
            CommentBean commentBean = new CommentBean();
            commentBean.setContent(mInputString);
            commentBean.setCreateTime("刚刚");
            if (mUserInfo != null) {
                if (!TextUtils.isEmpty(mUserInfo.getUsername())) {
                    commentBean.setUserName(mUserInfo.getUsername());
                } else {
                    commentBean.setUserName("我");
                }
                if (!TextUtils.isEmpty(mUserInfo.getAvatar()))
                    commentBean.setUserImg(mUserInfo.getAvatar());
            }
            commentBean.setHasReply(false);
            mCommentList.add(0, commentBean);
            mCommentAdapter.notifyDataSetChanged();
            showMessage("发送成功，优质评论将被优先展示");
        } else {
            mCommentAdapter.updateReplyStatus(mReplyPosition, mInputString, mUserInfo);
        }
        if (etInputComment != null) {
            recoverEtCommentStatus();
        }
        hideKeyboard();
    }
}
