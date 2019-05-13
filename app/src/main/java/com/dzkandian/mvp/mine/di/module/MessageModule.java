package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.MessageContract;
import com.dzkandian.mvp.mine.model.MessageModel;


@Module
public class MessageModule {
    private MessageContract.View view;

    /**
     * 构建MessageModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public MessageModule(MessageContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    MessageContract.View provideMessageView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    MessageContract.Model provideMessageModel(MessageModel model) {
        return model;
    }
}