package com.dzkandian.mvp.common.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.WeChatShareBean;
import com.dzkandian.storage.bean.mine.ApprenticeMessageBean;
import com.dzkandian.storage.bean.mine.RedPacketBean;
import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public interface WebViewContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void redPacketList(String data);

        void messageToApprentice(String messageBean);

        void messageToError();

        void openRedPacket(String redPacketBean);

        void updateShareData(WeChatShareBean weChatShareBean);

        void downloadCallBack(String filePath);

        void isfoucs(String fouce);

        void binding(String binding);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        @NonNull
        Observable<BaseResponse<String>> invitationRedPacket(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<String>> messageToApprentice(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<String>> openRedPacket(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<WeChatShareBean>> inviteShare(String token, RequestBody requestBody);

        @NonNull
        Observable<ResponseBody> update(String fileUrl);

        @NonNull
        Observable<BaseResponse<String>> isFoucs(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<String>> binding(String token, RequestBody requestBody);
    }
}
