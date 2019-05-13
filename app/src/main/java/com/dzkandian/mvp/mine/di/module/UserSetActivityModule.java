package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.UserSetActivityContract;
import com.dzkandian.mvp.mine.model.UserSetActivityModel;


@Module
public class UserSetActivityModule {
    private UserSetActivityContract.View view;

    /**
     * 构建UserSetActivityModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public UserSetActivityModule(UserSetActivityContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    UserSetActivityContract.View provideUserSetActivityView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    UserSetActivityContract.Model provideUserSetActivityModel(UserSetActivityModel model) {
        return model;
    }
}