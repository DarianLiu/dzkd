package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.InvitationModule;

import com.dzkandian.mvp.mine.ui.activity.InvitationActivity;

@ActivityScope
@Component(modules = InvitationModule.class, dependencies = AppComponent.class)
public interface InvitationComponent {
    void inject(InvitationActivity activity);
}