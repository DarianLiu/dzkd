package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.ActiveCenterModule;

import com.dzkandian.mvp.mine.ui.fragment.ActiveCenterFragment;

@ActivityScope
@Component(modules = ActiveCenterModule.class, dependencies = AppComponent.class)
public interface ActiveCenterComponent {
    void inject(ActiveCenterFragment activity);
}