package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.UpdatePhoneModule;

import com.dzkandian.mvp.mine.ui.activity.UpdatePhoneActivity;

@ActivityScope
@Component(modules = UpdatePhoneModule.class, dependencies = AppComponent.class)
public interface UpdatePhoneComponent {
    void inject(UpdatePhoneActivity activity);
}