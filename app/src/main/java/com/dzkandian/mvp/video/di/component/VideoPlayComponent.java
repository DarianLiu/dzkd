package com.dzkandian.mvp.video.di.component;

import com.dzkandian.mvp.video.di.module.VideoPlayModule;
import com.dzkandian.mvp.video.ui.activity.VideoDetailActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

@ActivityScope
@Component(modules = VideoPlayModule.class, dependencies = AppComponent.class)
public interface VideoPlayComponent {
    void inject(VideoDetailActivity activity);
}