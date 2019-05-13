package com.dzkandian.mvp.mine.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.UserBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import io.reactivex.Observable;
import okhttp3.RequestBody;


public interface UpdatePhoneContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void SmsCountDown(long time);

        void showNotice(String message);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        @NonNull
        Observable<BaseResponse<UserBean>> sendSmsCode(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<UserBean>> bindPhone(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<UserBean>> changePhone(String token, RequestBody requestBody);


        // 有密码时的修改密码方式
        @NonNull
        Observable<BaseResponse<UserBean>> existPwdRevisePwd(String token, RequestBody requestBody);


    }
}
