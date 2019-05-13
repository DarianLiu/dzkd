package com.dzkandian.mvp.task_center.presenter;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dzkandian.app.exception.ApiException;
import com.dzkandian.app.http.utils.DownLoadManager;
import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.mvp.task_center.contract.TaskCenterContract;
import com.dzkandian.storage.bean.WeChatShareBean;
import com.dzkandian.storage.bean.mine.BannerBean;
import com.dzkandian.storage.bean.task.SignRecordBean;
import com.dzkandian.storage.bean.task.TaskListBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.PermissionUtil;
import com.jess.arms.utils.RxLifecycleUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;


@ActivityScope
public class TaskCenterPresenter extends BasePresenter<TaskCenterContract.Model, TaskCenterContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public TaskCenterPresenter(@NonNull TaskCenterContract.Model model, @NonNull TaskCenterContract.View rootView) {
        super(model, rootView);
    }

    /**
     * 获取token
     */
    public String getToken() {
        return DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
    }

    /**
     * 签到列表
     */
    public void signRecord() {
        RequestBody requestBody = RequestParamUtils.buildRequestBody(mApplication, new HashMap<>());
        mModel.signRecord(getToken(), requestBody)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    mRootView.finishRefresh();//隐藏刷新
                }).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<SignRecordBean>(mErrorHandler) {
                    @Override
                    public void onNext(SignRecordBean signRecordBean) {
                        mRootView.updateSignRecord(signRecordBean);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.showErrorView();
                    }
                });
    }

    /**
     * 获取轮播图图片
     */
    public void getBanner() {
        String token = DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);

        mModel.banner(token, RequestParamUtils.banner(mApplication, "taskCenter"))
                .compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<List<BannerBean>>(mErrorHandler) {
                    @Override
                    public void onNext(List<BannerBean> imgsBean) {
                        mRootView.banner(imgsBean);
                    }
                });
    }


    /**
     * 任务列表
     */
    public void taskList() {
        RequestBody requestBody = RequestParamUtils.buildRequestBody(mApplication, new HashMap<>());
        mModel.taskList(getToken(), requestBody)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<TaskListBean>(mErrorHandler) {
                    @Override
                    public void onNext(TaskListBean taskListBean) {
                        mRootView.updateTaskList(taskListBean);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.showErrorView();
                    }
                });
    }


    /**
     * 每日签到
     */
    public void sign() {
        if (TextUtils.isEmpty(getToken())) {
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            RequestBody requestBody = RequestParamUtils.buildRequestBody(mApplication, new HashMap<>());
            mModel.sign(getToken(), requestBody).compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<Integer>(mErrorHandler) {
                        @Override
                        public void onNext(@NonNull Integer reward) {
                            mRootView.updateTodaySign(reward);
                        }

                        @Override
                        public void onError(Throwable t) {
                            super.onError(t);
                            if (t instanceof ApiException) {
                                if (((ApiException) t).getCode() == 525) {
                                    mRootView.updateTodaySign(0);
                                }
                            }
                        }
                    });
        }
    }

    /**
     * 完成任务领取奖励
     *
     * @param position 任务列表中的位置
     * @param id       时间戳
     * @param typeId   任务ID
     */
    public void taskFinish(int position, long id, int typeId) {
        if (TextUtils.isEmpty(getToken())) {
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            RequestBody requestBody = RequestParamUtils.buildTaskFinish(mApplication, id, typeId);
            mModel.taskFinish(getToken(), requestBody).compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<Integer>(mErrorHandler) {
                        @Override
                        public void onNext(@NonNull Integer reward) {
                            mRootView.receiveRewardSuccess(position, reward);
                        }

                        @Override
                        public void onError(Throwable t) {
                            super.onError(t);
                            if (t instanceof ApiException) {
                                if (((ApiException) t).getCode() == 526) {
                                    mRootView.receiveRewardSuccess(position, 0);
                                }
                            }
                        }
                    });
        }

    }

    /**
     * 微信绑定
     *
     * @param code     微信返回数据
     * @param position 任务列表中的位置
     */
    public void wxBinding(String code, int position) {
        if (TextUtils.isEmpty(getToken())) {
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            RequestBody requestBody = RequestParamUtils.buildWXLogin(mApplication, code);
            mModel.wxBinding(getToken(), requestBody).compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                        @Override
                        public void onNext(String userInfo) {
                            mRootView.taskFinished(position);
                        }
                    });
        }

    }

    /**
     * 支付宝绑定
     *
     * @param code 支付宝返回数据
     */
    public void alipayBinding(String code) {
        if (TextUtils.isEmpty(getToken())) {
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            RequestBody requestBody = RequestParamUtils.buildWXLogin(mApplication, code);
            mModel.alipayBinding(getToken(), requestBody).compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<TaskListBean>(mErrorHandler) {
                        @Override
                        public void onNext(TaskListBean taskListBean) {

                        }
                    });
        }

    }

    /**
     * 获取收徒分享的必需数据
     */
    public void inviteShare() {
        if (TextUtils.isEmpty(getToken())) {
            mRootView.launchActivity(new Intent(mAppManager.getTopActivity(), LoginActivity.class));
        } else {
            mModel.inviteShare(getToken(), RequestParamUtils.buildRequestBody(mApplication, new HashMap<>()))
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<WeChatShareBean>(mErrorHandler) {
                        @Override
                        public void onNext(WeChatShareBean weChatShareBean) {
                            mRootView.updateShareData(weChatShareBean);
                        }
                    });
        }
    }

    /**
     * 请求权限
     *
     * @param shareImageUrl 分享图片地址
     */
    public void requestPermission(String shareImageUrl) {
        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
                downLoad(shareImageUrl);
            }

            @Override
            public void onRequestPermissionFailure(List<String> permissions) {
                mRootView.showMessage("权限获取失败");
            }

            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                mRootView.showMessage("权限获取失败，将无法正常使用该应用！");
            }

        }, new RxPermissions(mAppManager.getTopActivity()), mErrorHandler);
    }

    /**
     * 下载图片
     *
     * @param shareImageUrl 分享图片地址
     */
    private void downLoad(String shareImageUrl) {
        mModel.update(shareImageUrl).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .concatMap(responseBody -> {
                    try {
                        String file_path = DownLoadManager.writeResponseBodyToDisk(responseBody);
                        return RxUtils.createData(file_path);
                    } catch (Exception e) {
                        return Observable.error(e);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                    @Override
                    public void onNext(String s) {
                        mRootView.downloadCallBack(s);
                    }
                });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }
}
