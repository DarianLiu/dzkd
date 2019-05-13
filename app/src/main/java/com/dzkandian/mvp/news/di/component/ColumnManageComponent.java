package com.dzkandian.mvp.news.di.component;

import com.dzkandian.mvp.news.di.module.ColumnManageModule;
import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.news.ui.activity.ColumnManageActivity;

@ActivityScope
@Component(modules = ColumnManageModule.class, dependencies = AppComponent.class)
public interface ColumnManageComponent {
    void inject(ColumnManageActivity activity);
}