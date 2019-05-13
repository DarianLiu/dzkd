package com.dzkandian.mvp.mine.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.CollectionVideoBean;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;


public interface CollectionVideoContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void refreshFailed(boolean isShowError);

        void finishRefresh();

        void finishLoadMore();

        void refreshData(List<CollectionVideoBean> list);

        void loadMoreData(List<CollectionVideoBean> list);

        void removeVideoCollection(int position);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        @NonNull
        Observable<BaseResponse<List<CollectionVideoBean>>> collectionVideoList(String token, RequestBody requestBody);

        @NonNull
        Observable<BaseResponse<Integer>> removeVideoCollection(String token, RequestBody requestBody);
    }
}
