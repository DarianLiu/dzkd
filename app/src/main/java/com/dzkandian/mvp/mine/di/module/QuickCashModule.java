package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.QuickCashContract;
import com.dzkandian.mvp.mine.model.QuickCashModel;


@Module
public class QuickCashModule {
    private QuickCashContract.View view;

    /**
     * 构建QuickCashModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public QuickCashModule(QuickCashContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    QuickCashContract.View provideQuickCashView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    QuickCashContract.Model provideQuickCashModel(QuickCashModel model) {
        return model;
    }
}