package com.dzkandian.mvp.mine.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.mine.di.module.QuestionAllModule;

import com.dzkandian.mvp.mine.ui.activity.QuestionAllActivity;

@ActivityScope
@Component(modules = QuestionAllModule.class, dependencies = AppComponent.class)
public interface QuestionAllComponent {
    void inject(QuestionAllActivity activity);
}