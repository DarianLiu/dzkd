package com.dzkandian.mvp.common.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.common.contract.AgreementWebContract;
import com.dzkandian.mvp.common.model.AgreementWebModel;


@Module
public class AgreementWebModule {
    private AgreementWebContract.View view;

    /**
     * 构建AgreementWebModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public AgreementWebModule(AgreementWebContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    AgreementWebContract.View provideAgreementWebView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    AgreementWebContract.Model provideAgreementWebModel(AgreementWebModel model) {
        return model;
    }
}