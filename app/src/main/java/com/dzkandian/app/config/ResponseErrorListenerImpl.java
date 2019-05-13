/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dzkandian.app.config;

import android.content.Context;
import android.net.ParseException;
import android.support.annotation.NonNull;

import com.dzkandian.app.exception.ApiException;
import com.google.gson.JsonParseException;
import com.jess.arms.utils.ArmsUtils;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener;
import retrofit2.HttpException;
import timber.log.Timber;

/**
 * 异常统一处理
 * ================================================
 * 展示 {@link ResponseErrorListener} 的用法
 * Created by LiuLi on 2018/4/9.
 * ================================================
 */
public class ResponseErrorListenerImpl implements ResponseErrorListener {

    @Override
    public void handleResponseError(Context context, @NonNull Throwable t) {
        Timber.tag("Catch-Error").w(t.getMessage());
        //这里不光是只能打印错误,还可以根据不同的错误作出不同的逻辑处理
        String msg = "未知错误";
        if (t instanceof UnknownHostException || t instanceof SocketTimeoutException || t instanceof ConnectException) {
            msg = "网络请求失败，请连网后重试";
        } else if (t instanceof HttpException) {
            HttpException httpException = (HttpException) t;
            msg = convertStatusCode(httpException);
        } else if (t instanceof JsonParseException || t instanceof ParseException || t instanceof JSONException) {
//            msg = "数据解析错误";
            return;
        } else if (t instanceof ApiException) {
            ApiException apiException = (ApiException) t;
            if (apiException.getCode() == 401) {
                return;
            }
            msg = apiException.getMessage();
        }

        ArmsUtils.makeText(context, msg);

    }

    private String convertStatusCode(@NonNull HttpException httpException) {
        String msg;
        if (httpException.code() == 500) {
            msg = "系统繁忙";
        } else if (httpException.code() == 404) {
            msg = "请求地址不存在";
        } else if (httpException.code() == 403) {
            msg = "请求被服务器拒绝";
        } else if (httpException.code() == 307) {
            msg = "请求被重定向到其他页面";
        } else {
            msg = httpException.message();
        }
        return msg;
    }

}
