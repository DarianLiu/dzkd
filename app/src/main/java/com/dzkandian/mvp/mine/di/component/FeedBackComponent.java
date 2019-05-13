package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.FeedBackModule;

import com.dzkandian.mvp.mine.ui.activity.FeedBackActivity;

@ActivityScope
@Component(modules = FeedBackModule.class, dependencies = AppComponent.class)
public interface FeedBackComponent {
    void inject(FeedBackActivity activity);
}