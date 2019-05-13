package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.InvitationContract;
import com.dzkandian.mvp.mine.model.InvitationModel;


@Module
public class InvitationModule {
    private InvitationContract.View view;

    /**
     * 构建InvitationModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public InvitationModule(InvitationContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    InvitationContract.View provideInvitationView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    InvitationContract.Model provideInvitationModel(InvitationModel model) {
        return model;
    }
}