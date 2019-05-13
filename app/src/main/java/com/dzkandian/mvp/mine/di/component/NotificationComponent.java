package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.NotificationModule;

import com.dzkandian.mvp.mine.ui.fragment.NotificationFragment;

@ActivityScope
@Component(modules = NotificationModule.class, dependencies = AppComponent.class)
public interface NotificationComponent {
    void inject(NotificationFragment fragment);
}