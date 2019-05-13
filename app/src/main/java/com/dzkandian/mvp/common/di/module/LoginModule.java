package com.dzkandian.mvp.common.di.module;

import com.dzkandian.mvp.common.contract.LoginContract;
import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.common.model.LoginModel;


@Module
public class LoginModule {
    private LoginContract.View view;

    /**
     * 构建TextLoginModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public LoginModule(LoginContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    LoginContract.View provideTextLoginView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    LoginContract.Model provideTextLoginModel(LoginModel model) {
        return model;
    }
}