package com.dzkandian.mvp.news.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.news.di.module.NewsModule;

import com.dzkandian.mvp.news.ui.fragment.NewsFragment;

@ActivityScope
@Component(modules = NewsModule.class, dependencies = AppComponent.class)
public interface NewsComponent {
    void inject(NewsFragment fragment);
}