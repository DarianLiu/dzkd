package com.dzkandian.mvp.common.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.dzkandian.storage.bean.UserBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import io.reactivex.Observable;
import okhttp3.RequestBody;


public interface LoginContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void showNormalDialog();

        void startCountDown(int time);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        @NonNull
        Observable<BaseResponse<UserBean>> sendSmsCode(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<UserBean>> smsLogin(RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<UserBean>> login(RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<UserBean>> wxLogin(RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<DeviceInfoBean>> uploadDeviceInfo(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<DeviceInfoBean>> getEssentialParameter(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<UserInfoBean>> userInfo(String token, RequestBody requestBody);
    }
}
