package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.CollectionVideoContract;
import com.dzkandian.mvp.mine.model.CollectionVideoModel;


@Module
public class CollectionVideoModule {
    private CollectionVideoContract.View view;

    /**
     * 构建CollectionVideoModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CollectionVideoModule(CollectionVideoContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    CollectionVideoContract.View provideCollectionVideoView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    CollectionVideoContract.Model provideCollectionVideoModel(CollectionVideoModel model) {
        return model;
    }
}