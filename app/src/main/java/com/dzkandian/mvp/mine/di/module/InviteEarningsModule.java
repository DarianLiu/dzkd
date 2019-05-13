package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.InviteEarningsContract;
import com.dzkandian.mvp.mine.model.InviteEarningsModel;


@Module
public class InviteEarningsModule {
    private InviteEarningsContract.View view;

    /**
     * 构建InviteEarningsModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public InviteEarningsModule(InviteEarningsContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    InviteEarningsContract.View provideInviteEarningsView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    InviteEarningsContract.Model provideInviteEarningsModel(InviteEarningsModel model) {
        return model;
    }
}