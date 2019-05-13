package com.dzkandian.mvp.common.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.common.contract.WebViewContract;
import com.dzkandian.mvp.common.model.WebViewModel;


@Module
public class WebViewModule {
    private WebViewContract.View view;

    /**
     * 构建WebViewModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public WebViewModule(WebViewContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    WebViewContract.View provideWebViewView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    WebViewContract.Model provideWebViewModel(WebViewModel model) {
        return model;
    }
}