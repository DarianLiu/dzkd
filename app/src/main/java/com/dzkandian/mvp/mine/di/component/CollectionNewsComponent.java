package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.CollectionNewsModule;

import com.dzkandian.mvp.mine.ui.fragment.CollectionNewsFragment;

@ActivityScope
@Component(modules = CollectionNewsModule.class, dependencies = AppComponent.class)
public interface CollectionNewsComponent {
    void inject(CollectionNewsFragment fragment);
}