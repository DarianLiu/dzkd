package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.AboutModule;

import com.dzkandian.mvp.mine.ui.activity.AboutActivity;

@ActivityScope
@Component(modules = AboutModule.class, dependencies = AppComponent.class)
public interface AboutComponent {
    void inject(AboutActivity activity);
}