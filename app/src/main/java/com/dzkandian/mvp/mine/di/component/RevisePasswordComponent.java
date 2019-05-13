package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.RevisePasswordModule;

import com.dzkandian.mvp.mine.ui.activity.RevisePasswordActivity;

@ActivityScope
@Component(modules = RevisePasswordModule.class, dependencies = AppComponent.class)
public interface RevisePasswordComponent {
    void inject(RevisePasswordActivity activity);
}