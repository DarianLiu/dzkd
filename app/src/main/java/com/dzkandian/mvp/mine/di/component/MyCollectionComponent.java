package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.MyCollectionModule;

import com.dzkandian.mvp.mine.ui.activity.MyCollectionActivity;

@ActivityScope
@Component(modules = MyCollectionModule.class, dependencies = AppComponent.class)
public interface MyCollectionComponent {
    void inject(MyCollectionActivity activity);
}