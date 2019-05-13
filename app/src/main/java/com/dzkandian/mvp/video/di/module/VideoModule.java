package com.dzkandian.mvp.video.di.module;

import com.dzkandian.mvp.video.contract.VideoContract;
import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.video.model.VideoModel;


@Module
public class VideoModule {
    private VideoContract.View view;

    /**
     * 构建TestVideoModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public VideoModule(VideoContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    VideoContract.View provideTestVideoView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    VideoContract.Model provideTestVideoModel(VideoModel model) {
        return model;
    }
}