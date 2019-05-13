package com.dzkandian.mvp.mine.di.component;

import com.dzkandian.mvp.mine.di.module.UpdateInfoModule;
import com.dzkandian.mvp.mine.ui.activity.UpdateALiPayActivity;
import com.dzkandian.mvp.mine.ui.activity.UpdateNicknameActivity;
import com.dzkandian.mvp.mine.ui.activity.UpdateWeChatPayActivity;
import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

@ActivityScope
@Component(modules = UpdateInfoModule.class, dependencies = AppComponent.class)
public interface UpdateInfoComponent {
    void inject(UpdateNicknameActivity activity);

    void inject(UpdateALiPayActivity activity);

    void inject(UpdateWeChatPayActivity activity);
}