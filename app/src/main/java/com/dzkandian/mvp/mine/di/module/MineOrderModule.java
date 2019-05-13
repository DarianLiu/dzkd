package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.MineOrderContract;
import com.dzkandian.mvp.mine.model.MineOrderModel;


@Module
public class MineOrderModule {
    private MineOrderContract.View view;

    /**
     * 构建MineOrderModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public MineOrderModule(MineOrderContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    MineOrderContract.View provideMineOrderView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    MineOrderContract.Model provideMineOrderModel(MineOrderModel model) {
        return model;
    }
}