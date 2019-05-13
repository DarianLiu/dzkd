package com.dzkandian.mvp.common.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.common.di.module.MainModule;

import com.dzkandian.mvp.common.ui.activity.MainActivity;

@ActivityScope
@Component(modules = MainModule.class, dependencies = AppComponent.class)
public interface MainComponent {
    void inject(MainActivity activity);
}