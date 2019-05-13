package com.dzkandian.mvp.common.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.common.di.module.PlayWebViewModule;

import com.dzkandian.mvp.common.ui.activity.PlayWebViewActivity;

@ActivityScope
@Component(modules = PlayWebViewModule.class, dependencies = AppComponent.class)
public interface PlayWebViewComponent {
    void inject(PlayWebViewActivity activity);
}