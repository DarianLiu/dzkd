package com.dzkandian.mvp.common.di.component;

import com.dzkandian.mvp.common.di.module.LoginModule;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

@ActivityScope
@Component(modules = LoginModule.class, dependencies = AppComponent.class)
public interface LoginComponent {
    void inject(LoginActivity activity);
}