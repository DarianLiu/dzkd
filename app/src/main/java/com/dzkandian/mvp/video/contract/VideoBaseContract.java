package com.dzkandian.mvp.video.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.news.NewsBean;
import com.dzkandian.storage.bean.video.VideoBean;
import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import java.util.List;

import io.reactivex.Observable;


public interface VideoBaseContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void finishRefresh();

        void finishLoadMore();

        void refreshData(List<VideoBean> videoList);

        void loadMoreData(List<VideoBean> videoList);

        void refreshFailed();
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        @NonNull
        Observable<BaseResponse<List<VideoBean>>> getVideoList(String type, int num, String beforeId,
                                                               String version,
                                                               String versionCode,
                                                               String sys_name,
                                                               String deviceId,
                                                               String timestamp);
    }
}
