package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.ProfitDetailContract;
import com.dzkandian.mvp.mine.model.ProfitDetailModel;


@Module
public class ProfitDetailModule {
    private ProfitDetailContract.View view;

    /**
     * 构建ProfitDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public ProfitDetailModule(ProfitDetailContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    ProfitDetailContract.View provideProfitDetailView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    ProfitDetailContract.Model provideProfitDetailModel(ProfitDetailModel model) {
        return model;
    }
}