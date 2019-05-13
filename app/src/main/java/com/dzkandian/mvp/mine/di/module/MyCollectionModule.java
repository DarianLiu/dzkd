package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.MyCollectionContract;
import com.dzkandian.mvp.mine.model.MyCollectionModel;


@Module
public class MyCollectionModule {
    private MyCollectionContract.View view;

    /**
     * 构建MyCollectionModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public MyCollectionModule(MyCollectionContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    MyCollectionContract.View provideMyCollectionView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    MyCollectionContract.Model provideMyCollectionModel(MyCollectionModel model) {
        return model;
    }
}