package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.RevisePasswordContract;
import com.dzkandian.mvp.mine.model.RevisePasswordModel;


@Module
public class RevisePasswordModule {
    private RevisePasswordContract.View view;

    /**
     * 构建RevisePasswordModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public RevisePasswordModule(RevisePasswordContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    RevisePasswordContract.View provideRevisePasswordView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    RevisePasswordContract.Model provideRevisePasswordModel(RevisePasswordModel model) {
        return model;
    }
}