package com.dzkandian.mvp.video.di.component;

import com.dzkandian.mvp.video.ui.activity.ShortDetailActivity;
import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.video.di.module.ShortVideoPlayModule;

@ActivityScope
@Component(modules = ShortVideoPlayModule.class, dependencies = AppComponent.class)
public interface ShortVideoPlayComponent {
    void inject(ShortDetailActivity activity);
}