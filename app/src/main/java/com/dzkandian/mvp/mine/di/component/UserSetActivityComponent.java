package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.UserSetActivityModule;

import com.dzkandian.mvp.mine.ui.activity.UserSetActivity;

@ActivityScope
@Component(modules = UserSetActivityModule.class, dependencies = AppComponent.class)
public interface UserSetActivityComponent {
    void inject(UserSetActivity activity);
}