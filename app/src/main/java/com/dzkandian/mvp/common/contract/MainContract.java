package com.dzkandian.mvp.common.contract;

import android.support.annotation.NonNull;

import com.dzkandian.common.uitls.update.UpdateAppBean;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.bean.VersionBean;
import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import io.reactivex.Observable;
import me.jessyan.progressmanager.body.ProgressInfo;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public interface MainContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void timeRewardCountdown(Long aLong);//时段奖励倒计时操作

        void showUpdateDialog(UpdateAppBean updateAppBean);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        @NonNull
        Observable<BaseResponse<VersionBean>> checkUpdate(String token, RequestBody requestBody);

        @NonNull
        Observable<ResponseBody> update(String fileUrl);
    }
}
