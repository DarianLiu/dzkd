package com.dzkandian.mvp.mine.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.UserBean;
import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import java.util.Observable;

import okhttp3.RequestBody;


public interface FeedBackContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        @NonNull
        io.reactivex.Observable<BaseResponse<UserBean>> feedBack(String token, RequestBody requestBody);
    }
}
