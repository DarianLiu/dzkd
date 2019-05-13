package com.dzkandian.wxapi.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.wxapi.di.module.WXEntryModule;

import com.dzkandian.wxapi.WXEntryActivity;

@ActivityScope
@Component(modules = WXEntryModule.class, dependencies = AppComponent.class)
public interface WXEntryComponent {
    void inject(WXEntryActivity activity);
}