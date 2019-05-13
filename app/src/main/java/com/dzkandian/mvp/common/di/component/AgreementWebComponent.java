package com.dzkandian.mvp.common.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.common.di.module.AgreementWebModule;

import com.dzkandian.mvp.common.ui.activity.AgreementWebActivity;

@ActivityScope
@Component(modules = AgreementWebModule.class, dependencies = AppComponent.class)
public interface AgreementWebComponent {
    void inject(AgreementWebActivity activity);
}