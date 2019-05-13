package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.QuickCashModule;

import com.dzkandian.mvp.mine.ui.activity.QuickCashActivity;

@ActivityScope
@Component(modules = QuickCashModule.class, dependencies = AppComponent.class)
public interface QuickCashComponent {
    void inject(QuickCashActivity activity);
}