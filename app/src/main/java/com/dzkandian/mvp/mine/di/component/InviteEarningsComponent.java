package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.InviteEarningsModule;

import com.dzkandian.mvp.mine.ui.activity.InviteEarningsActivity;

@ActivityScope
@Component(modules = InviteEarningsModule.class, dependencies = AppComponent.class)
public interface InviteEarningsComponent {
    void inject(InviteEarningsActivity activity);
}