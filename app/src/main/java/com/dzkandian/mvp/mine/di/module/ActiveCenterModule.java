package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.ActiveCenterContract;
import com.dzkandian.mvp.mine.model.ActiveCenterModel;


@Module
public class ActiveCenterModule {
    private ActiveCenterContract.View view;

    /**
     * 构建ActiveCenterModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public ActiveCenterModule(ActiveCenterContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    ActiveCenterContract.View provideActiveCenterView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    ActiveCenterContract.Model provideActiveCenterModel(ActiveCenterModel model) {
        return model;
    }
}