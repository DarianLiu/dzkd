package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.MineOrderModule;

import com.dzkandian.mvp.mine.ui.activity.MineOrderActivity;

@ActivityScope
@Component(modules = MineOrderModule.class, dependencies = AppComponent.class)
public interface MineOrderComponent {
    void inject(MineOrderActivity activity);
}