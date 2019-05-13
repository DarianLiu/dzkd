package com.dzkandian.mvp.common.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.common.di.module.ForgetPwdModule;

import com.dzkandian.mvp.common.ui.activity.ForgetPwdActivity;

@ActivityScope
@Component(modules = ForgetPwdModule.class, dependencies = AppComponent.class)
public interface ForgetPwdComponent {
    void inject(ForgetPwdActivity activity);
}