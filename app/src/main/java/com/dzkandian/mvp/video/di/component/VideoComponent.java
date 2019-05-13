package com.dzkandian.mvp.video.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.video.di.module.VideoModule;

import com.dzkandian.mvp.video.ui.fragment.VideoFragment;

@ActivityScope
@Component(modules = VideoModule.class, dependencies = AppComponent.class)
public interface VideoComponent {
    void inject(VideoFragment fragment);
}