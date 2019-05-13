package com.dzkandian.mvp.news.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.news.contract.ReplyDetailContract;
import com.dzkandian.mvp.news.model.ReplyDetailModel;


@Module
public class ReplyDetailModule {
    private ReplyDetailContract.View view;

    /**
     * 构建ReplyDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public ReplyDetailModule(ReplyDetailContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    ReplyDetailContract.View provideReplyDetailView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    ReplyDetailContract.Model provideReplyDetailModel(ReplyDetailModel model) {
        return model;
    }
}