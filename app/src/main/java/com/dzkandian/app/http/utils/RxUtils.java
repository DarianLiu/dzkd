package com.dzkandian.app.http.utils;

import android.content.Context;
import android.content.Intent;

import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.exception.ApiException;
import com.dzkandian.common.JPush.TagAliasOperatorHelper;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.UserBean;
import com.jess.arms.mvp.IView;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.RxLifecycleUtils;

import org.simple.eventbus.EventBus;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.dzkandian.common.JPush.TagAliasOperatorHelper.ACTION_CLEAN;
import static com.dzkandian.common.JPush.TagAliasOperatorHelper.sequence;

/**
 * ================================================
 * 放置便于使用 RxJava 的一些工具类
 * Created by LiuLi on 2018/4/9.
 * ================================================
 */
public class RxUtils {

    private RxUtils() {
    }

    /**
     * 线程调度
     *
     * @param view view
     * @param <T>  泛型
     */
    public static <T> ObservableTransformer<T, T> applySchedulers(@android.support.annotation.NonNull final IView view) {
        return new ObservableTransformer<T, T>() {
            @Override
            public Observable<T> apply(@android.support.annotation.NonNull Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(@NonNull Disposable disposable) throws Exception {
                                view.showLoading();//显示进度条
//                                disposable.dispose();
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(new Action() {
                            @Override
                            public void run() {
                                view.hideLoading();//隐藏进度条
                            }
                        }).compose(RxLifecycleUtils.bindToLifecycle(view));
            }
        };
    }

    /**
     * 线程调度
     *
     * @param view view
     * @param <T>  泛型
     */
    public static <T> ObservableTransformer<T, T> applyhideSchedulers(@android.support.annotation.NonNull final IView view) {
        return new ObservableTransformer<T, T>() {
            @Override
            public Observable<T> apply(@android.support.annotation.NonNull Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(@NonNull Disposable disposable) throws Exception {
//                                view.showLoading();//显示进度条
//                                disposable.dispose();
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(new Action() {
                            @Override
                            public void run() {
//                                view.hideLoading();//隐藏进度条
                            }
                        }).compose(RxLifecycleUtils.bindToLifecycle(view));
            }
        };
    }


    /**
     * 返回结果统一处理（此处保存时间戳，使用场景资讯和视频领取时段奖励）
     *
     * @param <T> 泛型
     * @return ObservableTransformer
     */
    public static <T> ObservableTransformer<BaseResponse<T>, T> handleBaseResultSaveTimeStamp(Context context) {
        return observable -> observable.flatMap((Function<BaseResponse<T>, ObservableSource<T>>) tBaseResponse -> {
            if (tBaseResponse.getTimestamp() != 0) {
                DataHelper.setStringSF(context, Constant.SP_KEY_TIME_STAMP, String.valueOf(tBaseResponse.getTimestamp()));
//                Timber.d("==timenews  返回结果统一处理获得时间：  "+tBaseResponse.getTimestamp());
            }
            if (tBaseResponse.isStatus()) {
                return createData(tBaseResponse.getData());
            } else {
                if (tBaseResponse.getCode() == 401) {
                    DataHelper.removeSF(context, Constant.SP_KEY_TOKEN);
                    DataHelper.removeSF(context, Constant.SP_KEY_EXPIRE);
                    EventBus.getDefault().post(false, EventBusTags.TAG_LOGIN_STATE);

//                    EventBus.getDefault().post(false, EventBusTags.TAG_PUSH_STATE);
                    TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
                    tagAliasBean.alias = "";
                    tagAliasBean.isAliasAction = true;
                    tagAliasBean.action = ACTION_CLEAN;
                    sequence++;
                    TagAliasOperatorHelper.getInstance().handleAction(context, sequence, tagAliasBean);

                    ArmsUtils.startActivity(new Intent(context, LoginActivity.class));
                    //登陆操作
                    return Observable.error(new ApiException(tBaseResponse.getMsg(), tBaseResponse.getCode()));
                } else if (tBaseResponse.getCode() == 513) {
                    return Observable.error(new ApiException(context.getString(R.string.reward_time_already), tBaseResponse.getCode()));
                } else {
                    return Observable.error(new ApiException(tBaseResponse.getMsg(), tBaseResponse.getCode()));
                }
            }
        });
    }

    /**
     * 统一返回结果处理
     *
     * @param <T> 泛型
     * @return ObservableTransformer
     */
    public static <T> ObservableTransformer<BaseResponse<T>, T> handleBaseResult(Context context) {
        return observable -> observable.flatMap((Function<BaseResponse<T>, ObservableSource<T>>) tBaseResponse -> {
            if (tBaseResponse.isStatus()) {
                if (tBaseResponse.getCode() == 524 && tBaseResponse.getData() instanceof UserBean) {
                    UserBean userBean = (UserBean) tBaseResponse.getData();
                    userBean.setMessage(tBaseResponse.getMsg());
                    return createData((T)userBean);
                }else {
                    return createData(tBaseResponse.getData());
                }
            } else {
//                if (tBaseResponse.getCode() == 401) {
//                    DataHelper.removeSF(context, Constant.SP_KEY_TOKEN);
//                    DataHelper.removeSF(context, Constant.SP_KEY_EXPIRE);
//                    DataHelper.removeSF(context, Constant.SP_KEY_USER_INFO);
//                    EventBus.getDefault().post(false, EventBusTags.TAG_LOGIN_STATE);
//                    ArmsUtils.startActivity(new Intent(context, LoginActivity.class));
                //登陆操作
                return Observable.error(new ApiException(tBaseResponse.getMsg(), tBaseResponse.getCode()));
//                }
//                else {
//                return Observable.error(new ApiException(tBaseResponse.getMsg(), tBaseResponse.getCode()));
//                }
            }
        });
    }

    public static <T> Observable<T> createData(final T t) {
        return Observable.create(emitter -> {
            try {
                emitter.onNext(t);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }


}
