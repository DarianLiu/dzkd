package com.dzkandian.mvp.news.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.news.di.module.ReplyDetailModule;

import com.dzkandian.mvp.news.ui.activity.ReplyDetailActivity;

@ActivityScope
@Component(modules = ReplyDetailModule.class, dependencies = AppComponent.class)
public interface ReplyDetailComponent {
    void inject(ReplyDetailActivity activity);
}