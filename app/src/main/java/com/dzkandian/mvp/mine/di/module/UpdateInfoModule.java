package com.dzkandian.mvp.mine.di.module;

import com.dzkandian.mvp.mine.contract.UpdateInfoContract;
import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.model.UpdateInfoModel;


@Module
public class UpdateInfoModule {
    private UpdateInfoContract.View view;

    /**
     * 构建ChangeNameModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public UpdateInfoModule(UpdateInfoContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    UpdateInfoContract.View provideChangeNameView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    UpdateInfoContract.Model provideChangeNameModel(UpdateInfoModel model) {
        return model;
    }
}