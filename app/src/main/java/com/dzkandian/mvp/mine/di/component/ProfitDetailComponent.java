package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.ProfitDetailModule;

import com.dzkandian.mvp.mine.ui.activity.ProfitDetailActivity;

@ActivityScope
@Component(modules = ProfitDetailModule.class, dependencies = AppComponent.class)
public interface ProfitDetailComponent {
    void inject(ProfitDetailActivity activity);
}