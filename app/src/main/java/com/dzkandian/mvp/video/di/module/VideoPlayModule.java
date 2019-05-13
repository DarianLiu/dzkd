package com.dzkandian.mvp.video.di.module;

import com.dzkandian.mvp.video.contract.VideoPlayContract;
import com.dzkandian.mvp.video.model.VideoPlayModel;
import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;


@Module
public class VideoPlayModule {
    private VideoPlayContract.View view;

    /**
     * 构建TestVideoModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public VideoPlayModule(VideoPlayContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    VideoPlayContract.View provideTestVideoPlayView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    VideoPlayContract.Model provideTestVideoPlayModel(VideoPlayModel model) {
        return model;
    }
}