package com.dzkandian.mvp.news.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.news.di.module.SearchModule;

import com.dzkandian.mvp.news.ui.activity.SearchActivity;

@ActivityScope
@Component(modules = SearchModule.class, dependencies = AppComponent.class)
public interface SearchComponent {
    void inject(SearchActivity activity);
}