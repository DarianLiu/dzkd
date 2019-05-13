package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.InviteDiscipleModule;

import com.dzkandian.mvp.mine.ui.activity.InviteDiscipleActivity;

@ActivityScope
@Component(modules = InviteDiscipleModule.class, dependencies = AppComponent.class)
public interface InviteDiscipleComponent {
    void inject(InviteDiscipleActivity activity);
}