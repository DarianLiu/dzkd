package com.dzkandian.mvp.video.di.component;

import com.dzkandian.mvp.video.ui.fragment.VideoBaseFragment;
import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.dzkandian.mvp.video.di.module.VideoBaseModule;

@ActivityScope
@Component(modules = VideoBaseModule.class, dependencies = AppComponent.class)
public interface VideoBaseComponent {
    void inject(VideoBaseFragment fragment);
}