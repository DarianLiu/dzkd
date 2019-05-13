package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.MessageModule;

import com.dzkandian.mvp.mine.ui.fragment.MessageFragment;

@ActivityScope
@Component(modules = MessageModule.class, dependencies = AppComponent.class)
public interface MessageComponent {
    void inject(MessageFragment fragment);
}