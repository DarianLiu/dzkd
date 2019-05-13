package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.AboutContract;
import com.dzkandian.mvp.mine.model.AboutModel;


@Module
public class AboutModule {
    private AboutContract.View view;

    /**
     * 构建AboutModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public AboutModule(AboutContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    AboutContract.View provideAboutView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    AboutContract.Model provideAboutModel(AboutModel model) {
        return model;
    }
}