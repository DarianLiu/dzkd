package com.dzkandian.mvp.common.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.common.di.module.RegisterModule;

import com.dzkandian.mvp.common.ui.activity.RegisterActivity;

@ActivityScope
@Component(modules = RegisterModule.class, dependencies = AppComponent.class)
public interface RegisterComponent {
    void inject(RegisterActivity activity);
}