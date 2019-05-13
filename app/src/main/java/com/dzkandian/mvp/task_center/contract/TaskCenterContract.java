package com.dzkandian.mvp.task_center.contract;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.bean.WeChatShareBean;
import com.dzkandian.storage.bean.mine.BannerBean;
import com.dzkandian.storage.bean.task.SignRecordBean;
import com.dzkandian.storage.bean.task.TaskListBean;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public interface TaskCenterContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void updateSignRecord(SignRecordBean signRecordBean);

        void updateTaskList(TaskListBean taskListBean);

        void updateTodaySign(int reward);

        void showErrorView();

        void taskFinished(int position);

        void receiveRewardSuccess(int position, int reward);

        void downloadCallBack(String filePath);

        void finishRefresh();

        void updateShareData(WeChatShareBean  weChatShareBean);

        void banner(List<BannerBean> bannerBeans);

    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        /**
         * 签到列表
         */
        @NonNull
        Observable<BaseResponse<SignRecordBean>> signRecord(String token, RequestBody requestBody);

        /**
         * 任务列表
         */
        @NonNull
        Observable<BaseResponse<TaskListBean>> taskList(String token, RequestBody requestBody);

        /**
         * 每日签到
         */
        @NonNull
        Observable<BaseResponse<Integer>> sign(String token, RequestBody requestBody);

        /**
         * 完成列表中的任务,领取奖励
         */
        @NonNull
        Observable<BaseResponse<Integer>> taskFinish(String token, RequestBody requestBody);

        /**
         * 微信绑定
         */
        @NonNull
        Observable<BaseResponse<String>> wxBinding(String token, RequestBody requestBody);

        /**
         * 支付宝绑定
         */
        @NonNull
        Observable<BaseResponse<TaskListBean>> alipayBinding(String token, RequestBody requestBody);

        /**
         * 下载分享朋友圈的图片
         */
        @NonNull
        Observable<ResponseBody> update(String fileUrl);

        /**
         * 获取邀请分享数据
         */
        @NonNull
        Observable<BaseResponse<WeChatShareBean>> inviteShare(String token, RequestBody requestBody);

        /**
         * 获取轮播图图片
         * @param token
         * @param requestBody
         * @return
         */
        @NonNull
        Observable<BaseResponse<List<BannerBean>>> banner(String token, RequestBody requestBody);
    }
}
