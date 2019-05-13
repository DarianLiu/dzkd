package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.CollectionShortContract;
import com.dzkandian.mvp.mine.model.CollectionShortModel;


@Module
public class CollectionShortModule {
    private CollectionShortContract.View view;

    /**
     * 构建CollectionShortModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CollectionShortModule(CollectionShortContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    CollectionShortContract.View provideCollectionShortView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    CollectionShortContract.Model provideCollectionShortModel(CollectionShortModel model) {
        return model;
    }
}