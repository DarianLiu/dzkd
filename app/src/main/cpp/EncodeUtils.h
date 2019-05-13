//
// Created by liupeng on 2018/3/13.
//
#include <jni.h>

#ifndef WINANDROID_ENCODEUTILS_H
#define WINANDROID_ENCODEUTILS_H

class EncodeUtils {

public:
    static jstring geneSign(JNIEnv *env, jstring& data);
};


#endif //WINANDROID_ENCODEUTILS_H
