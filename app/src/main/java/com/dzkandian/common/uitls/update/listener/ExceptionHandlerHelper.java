package com.dzkandian.common.uitls.update.listener;

/**
 * Created by Administrator on 2018/8/22.
 */

public class ExceptionHandlerHelper {
    private static ExceptionHandler instance;
    public static void init(ExceptionHandler exceptionHandler) {
        ExceptionHandler temp = instance;
        if (temp == null) {
            synchronized (ExceptionHandlerHelper.class) {
                temp = instance;
                if (temp == null) {
                    temp = exceptionHandler;
                    instance = temp;
                }
            }
        }
    }
    public static ExceptionHandler getInstance() {
        return instance;
    }
}