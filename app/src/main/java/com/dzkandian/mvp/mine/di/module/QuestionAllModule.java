package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.QuestionAllContract;
import com.dzkandian.mvp.mine.model.QuestionAllModel;


@Module
public class QuestionAllModule {
    private QuestionAllContract.View view;

    /**
     * 构建QuestionAllModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public QuestionAllModule(QuestionAllContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    QuestionAllContract.View provideQuestionAllView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    QuestionAllContract.Model provideQuestionAllModel(QuestionAllModel model) {
        return model;
    }
}