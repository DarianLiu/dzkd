package com.dzkandian.mvp.news.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;


public interface NewsContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void updateView(List<String> allColumn, List<String> viewColumn );

        void showErrorNetwork();//没有获取到资讯分类列表显示的fragment

        void timeRewardInt(int number);//时段奖励领取成功，number金币数

        void timeRewardError();//时段奖励领取错误

        void showNormalDialog();

    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        @NonNull
        Observable<BaseResponse<List<String>>> getNewsTitleList(String version,
                                                                String versionCode,
                                                                String sys_name,
                                                                String deviceId,
                                                                String timestamp);

        @NonNull
        Observable<BaseResponse<Integer>> timeReward(String token, RequestBody requestBody);

    }
}
