package com.dzkandian.mvp.video.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.video.contract.ShortVideoPlayContract;
import com.dzkandian.mvp.video.model.ShortVideoPlayModel;


@Module
public class ShortVideoPlayModule {
    private ShortVideoPlayContract.View view;

    /**
     * 构建ShortVideoPlayModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public ShortVideoPlayModule(ShortVideoPlayContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    ShortVideoPlayContract.View provideShortVideoPlayView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    ShortVideoPlayContract.Model provideShortVideoPlayModel(ShortVideoPlayModel model) {
        return model;
    }
}