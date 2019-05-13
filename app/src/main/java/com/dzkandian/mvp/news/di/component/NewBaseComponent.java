package com.dzkandian.mvp.news.di.component;

import com.dzkandian.mvp.news.di.module.NewBaseModule;
import com.dzkandian.mvp.news.ui.fragment.NewBaseFragment;
import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

@ActivityScope
@Component(modules = NewBaseModule.class, dependencies = AppComponent.class)
public interface NewBaseComponent {
    void inject(NewBaseFragment fragment);
}