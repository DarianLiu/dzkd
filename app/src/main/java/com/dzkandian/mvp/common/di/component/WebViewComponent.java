package com.dzkandian.mvp.common.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.common.di.module.WebViewModule;

import com.dzkandian.mvp.common.ui.activity.WebViewActivity;

@ActivityScope
@Component(modules = WebViewModule.class, dependencies = AppComponent.class)
public interface WebViewComponent {
    void inject(WebViewActivity activity);
}