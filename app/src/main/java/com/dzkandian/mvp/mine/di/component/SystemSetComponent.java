package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.SystemSetModule;

import com.dzkandian.mvp.mine.ui.activity.SystemSetActivity;

@ActivityScope
@Component(modules = SystemSetModule.class, dependencies = AppComponent.class)
public interface SystemSetComponent {
    void inject(SystemSetActivity activity);
}