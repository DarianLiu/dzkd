package com.dzkandian.mvp.task_center.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.task_center.di.module.TaskCenterModule;

import com.dzkandian.mvp.task_center.ui.fragment.TaskCenterFragment;

@ActivityScope
@Component(modules = TaskCenterModule.class, dependencies = AppComponent.class)
public interface TaskCenterComponent {
    void inject(TaskCenterFragment fragment);
}