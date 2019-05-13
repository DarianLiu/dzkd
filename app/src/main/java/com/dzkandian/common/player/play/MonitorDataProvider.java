package com.dzkandian.common.player.play;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.dzkandian.storage.bean.video.VideoBean;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.BundlePool;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.provider.BaseDataProvider;

import java.util.List;

/**
 * 视频数据提供者
 * Created by Taurus on 2018/4/15.
 */

public class MonitorDataProvider extends BaseDataProvider {

    private DataSource mDataSource;

    private List<VideoBean> mVideos;

    public MonitorDataProvider() {

    }

    private void setVideos(List<VideoBean> videos) {
        this.mVideos = videos;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    @Override
    public void handleSourceData(DataSource sourceData) {
        this.mDataSource = sourceData;
        onProviderDataStart();
        mHandler.removeCallbacks(mLoadDataRunnable);
        mHandler.postDelayed(mLoadDataRunnable, 2000);
    }

    private Runnable mLoadDataRunnable = new Runnable() {
        @Override
        public void run() {
            long id = mDataSource.getId();
            int index = (int) (id % mVideos.size());
            VideoBean bean = mVideos.get(index);
            mDataSource.setData(bean.getUrl());
            mDataSource.setTitle(bean.getTitle());
            Bundle bundle = BundlePool.obtain();
            bundle.putSerializable(EventKey.SERIALIZABLE_DATA, mDataSource);
            onProviderMediaDataSuccess(bundle);
        }
    };

    @Override
    public void cancel() {
        mHandler.removeCallbacks(mLoadDataRunnable);
    }

    @Override
    public void destroy() {
        cancel();
    }
}
