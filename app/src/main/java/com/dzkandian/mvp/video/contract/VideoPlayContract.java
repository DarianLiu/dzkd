package com.dzkandian.mvp.video.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.news.NewBarrageBean;
import com.dzkandian.storage.bean.news.NewsOrVideoShareBean;
import com.dzkandian.storage.bean.video.VideoBean;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public interface VideoPlayContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        void timeProgressNext(int i);

        void finishRefresh();

        void finishLoadMore();

        void refreshData(List<VideoBean> videoList);

        void loadMoreData(List<VideoBean> videoList);

        void refreshFailed();

        void videoRewardInt(Integer integer);

        void errorVideoReward();

        void videoShare(NewsOrVideoShareBean newsOrVideoShareBean);

        void upView();

        void downloadCallBack(String filePath);

        void commentSuccess();

        void loadBarrage(NewBarrageBean barrageBean);

        void collectionValue(boolean value);//视频收藏，true收藏成功；false收藏取消；

        void thumbsUpSuccess(int count);

        void thumbsUpError(boolean type);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        @NonNull
        Observable<BaseResponse<List<VideoBean>>> getVideoList(String type, int num, String beforeId,String placeKeyword,
                                                               String version,
                                                               String versionCode,
                                                               String sys_name,
                                                               String deviceId,
                                                               String timestamp,
                                                               String supportSdk);


        @NonNull
        Observable<BaseResponse<Integer>> videoReward(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<NewsOrVideoShareBean>> videoShare(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<NewBarrageBean>> foundComment(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<NewBarrageBean>> getBarrage(String token, RequestBody requestBody);

        /**
         * 视频收藏
         */
        @NonNull
        Observable<BaseResponse<Integer>> videoCollection(String token, RequestBody requestBody);

        /**
         * 下载用户图片
         */
        @NonNull
        Observable<ResponseBody> update(String fileUrl);

        /**
         * 点赞评论接口
         */
        @NonNull
        Observable<BaseResponse<Integer>> commentThumbsUp(String token, RequestBody requestBody);

    }
}
