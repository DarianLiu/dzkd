package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.MessageCenterContract;
import com.dzkandian.mvp.mine.model.MessageCenterModel;


@Module
public class MessageCenterModule {
    private MessageCenterContract.View view;

    /**
     * 构建MessageCenterModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public MessageCenterModule(MessageCenterContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    MessageCenterContract.View provideMessageCenterView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    MessageCenterContract.Model provideMessageCenterModel(MessageCenterModel model) {
        return model;
    }
}