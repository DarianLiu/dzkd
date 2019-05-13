package com.dzkandian.mvp.video.di.module;

import com.dzkandian.mvp.video.model.VideoBaseModel;
import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.video.contract.VideoBaseContract;


@Module
public class VideoBaseModule {
    private VideoBaseContract.View view;

    /**
     * 构建TestVideoBaseModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public VideoBaseModule(VideoBaseContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    VideoBaseContract.View provideTestVideoBaseView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    VideoBaseContract.Model provideTestVideoBaseModel(VideoBaseModel model) {
        return model;
    }
}