package com.dzkandian.mvp.mine.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.mine.MyOrderBean;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;


public interface MineOrderContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void finishRefresh();

        void finishLoadMore();

        void refreshFailed(boolean isRefresh);

        void refreshData(List<MyOrderBean> myOrderBeans);

        void loadMoreData(List<MyOrderBean> myOrderBeans);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        @NonNull
        Observable<BaseResponse<List<MyOrderBean>>> questAllOrderList(String token, RequestBody requestBody);
    }
}
