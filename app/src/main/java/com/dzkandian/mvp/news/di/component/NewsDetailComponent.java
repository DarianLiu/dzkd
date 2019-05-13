package com.dzkandian.mvp.news.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.news.di.module.NewsDetailModule;

import com.dzkandian.mvp.news.ui.activity.NewsDetailActivity;

@ActivityScope
@Component(modules = NewsDetailModule.class, dependencies = AppComponent.class)
public interface NewsDetailComponent {
    void inject(NewsDetailActivity activity);
}