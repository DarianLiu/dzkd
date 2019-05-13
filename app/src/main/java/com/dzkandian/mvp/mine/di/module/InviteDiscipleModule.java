package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.InviteDiscipleContract;
import com.dzkandian.mvp.mine.model.InviteDiscipleModel;


@Module
public class InviteDiscipleModule {
    private InviteDiscipleContract.View view;

    /**
     * 构建InviteDiscipleModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public InviteDiscipleModule(InviteDiscipleContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    InviteDiscipleContract.View provideInviteDiscipleView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    InviteDiscipleContract.Model provideInviteDiscipleModel(InviteDiscipleModel model) {
        return model;
    }
}