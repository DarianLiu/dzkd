package com.dzkandian.mvp.common.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import io.reactivex.Observable;
import okhttp3.RequestBody;


public interface SplashContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        void showNormalDialog();

        void updateCountDown(long time);

        void adsNativeSplash();

        void getPhoneInfo();
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        @NonNull
        Observable<BaseResponse<DeviceInfoBean>> uploadDeviceInfo(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<String>> isRealization(RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<Object>> isRealizationYouMi(RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<DeviceInfoBean>> getEssentialParameter(String token, RequestBody requestBody);
    }
}
