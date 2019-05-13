package com.dzkandian.mvp.news.di.module;

import com.dzkandian.mvp.news.contract.NewsContract;
import com.dzkandian.mvp.news.model.NewsModel;
import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;


@Module
public class NewsModule {
    private NewsContract.View view;

    /**
     * 构建TestNewsModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public NewsModule(NewsContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    NewsContract.View provideTestNewsView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    NewsContract.Model provideTestNewsModel(NewsModel model) {
        return model;
    }
}