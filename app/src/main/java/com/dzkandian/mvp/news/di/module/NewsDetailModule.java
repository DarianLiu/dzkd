package com.dzkandian.mvp.news.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.news.contract.NewsDetailContract;
import com.dzkandian.mvp.news.model.NewsDetailModel;


@Module
public class NewsDetailModule {
    private NewsDetailContract.View view;

    /**
     * 构建NewsDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public NewsDetailModule(NewsDetailContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    NewsDetailContract.View provideNewsDetailView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    NewsDetailContract.Model provideNewsDetailModel(NewsDetailModel model) {
        return model;
    }
}