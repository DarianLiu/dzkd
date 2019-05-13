package com.dzkandian.mvp.mine.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.CoinBean;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.dzkandian.storage.bean.MarqueeBean;
import com.dzkandian.storage.bean.RandomAdBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public interface MineContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void updateCoin(CoinBean coinBean);

        void updateUserInfo(UserInfoBean userInfo);

        void finishRefresh();

        void downloadCallBack(String filePath);

        void SetMarquee(MarqueeBean marqueeBean);

        void successSelfAd(RandomAdBean randomAdBean);//
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        Observable<BaseResponse<UserInfoBean>> userInfo(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<CoinBean>> getCoin(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<MarqueeBean>> getMarquee();

        @NonNull
        Observable<ResponseBody> update(String fileUrl);

        Observable<BaseResponse<DeviceInfoBean>> getEssentialParameter(String token, RequestBody requestBody);

        /**
         * 获取DSP接口
         */
        @NonNull
        Observable<BaseResponse<RandomAdBean>> getRandomAd(String version,
                                                           String deviceId,
                                                           String supportSdk,
                                                           String placeKeyword);
    }
}
