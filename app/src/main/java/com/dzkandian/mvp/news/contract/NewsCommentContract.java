package com.dzkandian.mvp.news.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.news.CommentRecordBean;
import com.dzkandian.storage.bean.news.NewBarrageBean;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import io.reactivex.Observable;
import okhttp3.RequestBody;


public interface NewsCommentContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void refreshFailed(boolean isShowError);

        void finishRefresh();

        void finishLoadMore();

        void refreshData(CommentRecordBean commentBeans);

        void loadMoreData(CommentRecordBean commentBeans);

        void commentSuccess();

        void commitFail();

        void receiveThumbsUp(Integer integer,int position);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        /**
         * 获取评论列表接口
         */
        @NonNull
        Observable<BaseResponse<CommentRecordBean>> commentRecord(String token, RequestBody requestBody);

        /**
         * 创建评论接口
         */
        @NonNull
        Observable<BaseResponse<NewBarrageBean>> foundComment(String token, RequestBody requestBody);

        /**
         * 点赞接口
         */
        @NonNull
        Observable<BaseResponse<Integer>> commentThumbsUp(String token, RequestBody requestBody);
    }
}
