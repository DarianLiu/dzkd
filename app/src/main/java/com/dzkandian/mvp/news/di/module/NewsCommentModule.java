package com.dzkandian.mvp.news.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.news.contract.NewsCommentContract;
import com.dzkandian.mvp.news.model.NewsCommentModel;


@Module
public class NewsCommentModule {
    private NewsCommentContract.View view;

    /**
     * 构建NewsCommentModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public NewsCommentModule(NewsCommentContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    NewsCommentContract.View provideNewsCommentView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    NewsCommentContract.Model provideNewsCommentModel(NewsCommentModel model) {
        return model;
    }
}