package com.dzkandian.mvp.task_center.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.task_center.contract.TaskCenterContract;
import com.dzkandian.mvp.task_center.model.TaskCenterModel;


@Module
public class TaskCenterModule {
    private TaskCenterContract.View view;

    /**
     * 构建TaskCenterModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public TaskCenterModule(TaskCenterContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    TaskCenterContract.View provideTaskCenterView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    TaskCenterContract.Model provideTaskCenterModel(TaskCenterModel model) {
        return model;
    }
}