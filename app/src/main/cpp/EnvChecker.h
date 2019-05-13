//
// Created by 12 on 2018/3/13.
//

#include <jni.h>
#include "md5.h"
#include "Utils.h"

#ifndef DZKANDIAN_ENVCHECKER_H
#define DZKANDIAN_ENVCHECKER_H

#endif //DZKANDIAN_ENVCHECKER_H

class EnvChecker{
    public:
        static bool isValid(JNIEnv* env,jobject context);  // 调用是否合法

    private:
        static jstring getPackageName(JNIEnv* env,jobject context,BaseClasses classes);  //获取包名
        static jstring getSign(JNIEnv* env,jobject context,BaseClasses classes);  //获得签名
        static bool checkSign(JNIEnv* env,jobject context,BaseClasses classes);  //检查签名
        static bool checkPkg(JNIEnv* env,jobject context,BaseClasses classes);  //检查包名

};
