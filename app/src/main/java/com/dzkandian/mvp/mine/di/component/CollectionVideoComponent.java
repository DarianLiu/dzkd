package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.CollectionVideoModule;

import com.dzkandian.mvp.mine.ui.fragment.CollectionVideoFragment;

@ActivityScope
@Component(modules = CollectionVideoModule.class, dependencies = AppComponent.class)
public interface CollectionVideoComponent {
    void inject(CollectionVideoFragment fragment);
}