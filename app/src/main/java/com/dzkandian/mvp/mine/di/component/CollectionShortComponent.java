package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.CollectionShortModule;

import com.dzkandian.mvp.mine.ui.fragment.CollectionShortFragment;

@ActivityScope
@Component(modules = CollectionShortModule.class, dependencies = AppComponent.class)
public interface CollectionShortComponent {
    void inject(CollectionShortFragment fragment);
}