package com.dzkandian.mvp.video.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.RandomAdBean;
import com.dzkandian.storage.bean.news.NewBarrageBean;
import com.dzkandian.storage.bean.news.NewsOrVideoShareBean;
import com.dzkandian.storage.bean.video.VideoBean;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public interface ShortVideoPlayContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void finishRefresh();

        void finishLoadMore();

        void refreshData(List<VideoBean> videoList);

        void loadMoreData(List<VideoBean> videoList);

        void refreshFailed();

        void videoRewardProgress(Long progress);

        void videoRewardSuccess(Integer rewardCoin);

        void videoRewardFail();

        void setVideoShareContent(NewsOrVideoShareBean shareContent);

        void downloadCallBack(String filePath);

        void loadBarrage(NewBarrageBean shareContent);

        void commentSuccess();

        void collectionValue(boolean value);//小视频收藏，true收藏成功；false收藏取消；

        void thumbsUpSuccess(int count);

        void thumbsUpError(boolean type);

        void randomDSPAd(RandomAdBean randomAdBean);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<BaseResponse<List<VideoBean>>> getVideoList(String type, int num, String beforeId,
                                                               String version,
                                                               String versionCode,
                                                               String sys_name,
                                                               String deviceId,
                                                               String timestamp);

        Observable<BaseResponse<Integer>> videoReward(String token, RequestBody requestBody);

        Observable<BaseResponse<NewsOrVideoShareBean>> videoShare(String token, RequestBody requestBody);

        Observable<BaseResponse<NewBarrageBean>> getBarrage(String token, RequestBody requestBody);

        Observable<BaseResponse<NewBarrageBean>> foundComment(String token, RequestBody requestBody);

        /**
         * 小视频收藏
         */
        @NonNull
        Observable<BaseResponse<Integer>> shortCollection(String token, RequestBody requestBody);

        /**
         * 下载用户图片
         */
        Observable<ResponseBody> update(String fileUrl);

        /**
         * 点赞评论接口
         */
        @NonNull
        Observable<BaseResponse<Integer>> commentThumbsUp(String token, RequestBody requestBody);

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
