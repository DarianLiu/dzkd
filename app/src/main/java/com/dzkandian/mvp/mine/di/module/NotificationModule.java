package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.NotificationContract;
import com.dzkandian.mvp.mine.model.NotificationModel;


@Module
public class NotificationModule {
    private NotificationContract.View view;

    /**
     * 构建NotificationModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public NotificationModule(NotificationContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    NotificationContract.View provideNotificationView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    NotificationContract.Model provideNotificationModel(NotificationModel model) {
        return model;
    }
}