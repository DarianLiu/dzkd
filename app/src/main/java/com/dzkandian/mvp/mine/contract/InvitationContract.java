package com.dzkandian.mvp.mine.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.WeChatShareBean;
import com.dzkandian.storage.bean.mine.BannerBean;
import com.dzkandian.storage.bean.mine.InvitePageBean;
import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public interface InvitationContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void updateView(InvitePageBean invitePageBean);

        void updateShareData(WeChatShareBean  weChatShareBean);

        void downloadCallBack(String filePath);

        void banner(List<BannerBean> imgsBean);

        void setErrorLayout();
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        @NonNull
        Observable<BaseResponse<InvitePageBean>> invitePageData(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<WeChatShareBean>> inviteShare(String token, RequestBody requestBody);

        @NonNull
        Observable<ResponseBody> update(String fileUrl);

        @NonNull
        Observable<BaseResponse<List<BannerBean>>> banner(String token, RequestBody requestBody);
    }
}
