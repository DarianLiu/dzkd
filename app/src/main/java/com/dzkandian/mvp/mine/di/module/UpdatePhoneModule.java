package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.UpdatePhoneContract;
import com.dzkandian.mvp.mine.model.UpdatePhoneModel;


@Module
public class UpdatePhoneModule {
    private UpdatePhoneContract.View view;

    /**
     * 构建UpdatePhoneModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public UpdatePhoneModule(UpdatePhoneContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    UpdatePhoneContract.View provideUpdatePhoneView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    UpdatePhoneContract.Model provideUpdatePhoneModel(UpdatePhoneModel model) {
        return model;
    }
}