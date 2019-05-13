package com.dzkandian.mvp.common.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.common.di.module.AdWebModule;

import com.dzkandian.mvp.common.ui.activity.AdWebActivity;

@ActivityScope
@Component(modules = AdWebModule.class, dependencies = AppComponent.class)
public interface AdWebComponent {
    void inject(AdWebActivity activity);
}