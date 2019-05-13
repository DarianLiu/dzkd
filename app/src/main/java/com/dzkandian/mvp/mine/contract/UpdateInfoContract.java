package com.dzkandian.mvp.mine.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.UserBindBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.bean.mine.AlipayInfoBean;
import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import io.reactivex.Observable;
import okhttp3.RequestBody;


public interface UpdateInfoContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void updateWeChatPayBindInfo(String name, String headImageUrl);


    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        @NonNull
        Observable<BaseResponse<UserInfoBean>> updateInfo(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<UserBindBean>> weixinPayBind(String token, RequestBody requestBody);

        Observable<BaseResponse<AlipayInfoBean>> aLiPayLoginParam(String token, RequestBody requestBody);

        Observable<BaseResponse<String>> aLiPayBind(String token, RequestBody requestBody);
    }
}
