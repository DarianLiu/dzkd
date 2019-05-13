package com.dzkandian.mvp.news.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.news.NewBarrageBean;
import com.dzkandian.storage.bean.news.NewsOrVideoShareBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public interface NewsDetailContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void updateArcProgress(boolean complete);

        void readingRewardInt(Integer integer);

        void newsShare(NewsOrVideoShareBean newsOrVideoShareBean);

        void upView();

        void downloadCallBack(String filePath);

        void loadBarrage(NewBarrageBean newBarrageBean);

        void commentSuccess();

        void collectionValue(boolean value);//资讯收藏，true收藏成功；false收藏取消；

        void thumbsUpSuccess(int count);

        void thumbsUpError(boolean type);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        @NonNull
        Observable<BaseResponse<Integer>> readingReward(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<NewsOrVideoShareBean>> newsShare(String token, RequestBody requestBody);

        /**
         * 下载用户图片
         */
        @NonNull
        Observable<ResponseBody> update(String fileUrl);

        /**
         * 获取弹幕条数
         */
        @NonNull
        Observable<BaseResponse<NewBarrageBean>> getBarrage(String token, RequestBody requestBody);

        /**
         * 创建评论接口
         */
        @NonNull
        Observable<BaseResponse<NewBarrageBean>> foundComment(String token, RequestBody requestBody);

        /**
         * 新闻收藏
         */
        @NonNull
        Observable<BaseResponse<Integer>> newsCollection(String token, RequestBody requestBody);

        /**
         * 点赞评论接口
         */
        @NonNull
        Observable<BaseResponse<Integer>> commentThumbsUp(String token, RequestBody requestBody);

//        @NonNull
//        Observable<BaseResponse<Integer>> x5web(String token, RequestBody requestBody);

    }
}
