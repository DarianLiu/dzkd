package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.CollectionNewsContract;
import com.dzkandian.mvp.mine.model.CollectionNewsModel;


@Module
public class CollectionNewsModule {
    private CollectionNewsContract.View view;

    /**
     * 构建CollectionNewsModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CollectionNewsModule(CollectionNewsContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    CollectionNewsContract.View provideCollectionNewsView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    CollectionNewsContract.Model provideCollectionNewsModel(CollectionNewsModel model) {
        return model;
    }
}