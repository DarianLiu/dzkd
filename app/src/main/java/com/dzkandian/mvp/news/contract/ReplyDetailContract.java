package com.dzkandian.mvp.news.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.news.ReplyBean;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;


public interface ReplyDetailContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void refreshFailed(boolean isShowError);

        void finishRefresh();

        void finishLoadMore();

        void refreshData(List<ReplyBean> replyList);

        void loadMoreData(List<ReplyBean> replyList);

        void replySuccess(String inputString, String content, String parentId, String replyId, String replyName);

        void praiseResult(boolean isSuccess, int position, int validThumbsUp);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        /**
         * 获取回复列表
         */
        @NonNull
        Observable<BaseResponse<List<ReplyBean>>> getReplyList(String token, RequestBody requestBody);

        /**
         * 创建回复
         */
        @NonNull
        Observable<BaseResponse<Object>> foundReply(String token, RequestBody requestBody);

        /**
         * 创建点赞
         */
        @NonNull
        Observable<BaseResponse<Integer>> foundPraise(String token, RequestBody requestBody);
    }
}
