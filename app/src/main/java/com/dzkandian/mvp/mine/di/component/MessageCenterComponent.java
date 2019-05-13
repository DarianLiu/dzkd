package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.MessageCenterModule;

import com.dzkandian.mvp.mine.ui.activity.MessageCenterActivity;

@ActivityScope
@Component(modules = MessageCenterModule.class, dependencies = AppComponent.class)
public interface MessageCenterComponent {
    void inject(MessageCenterActivity activity);
}