package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.MineModule;

import com.dzkandian.mvp.mine.ui.fragment.MineFragment;

@ActivityScope
@Component(modules = MineModule.class, dependencies = AppComponent.class)
public interface MineComponent {
    void inject(MineFragment fragment);
}