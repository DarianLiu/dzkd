package com.dzkandian.mvp.news.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.news.di.module.NewsCommentModule;

import com.dzkandian.mvp.news.ui.activity.NewsCommentActivity;

@ActivityScope
@Component(modules = NewsCommentModule.class, dependencies = AppComponent.class)
public interface NewsCommentComponent {
    void inject(NewsCommentActivity activity);
}