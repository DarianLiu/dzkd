
#include <jni.h>
#include "EncodeUtils.h"


extern "C"
JNIEXPORT jstring JNICALL
Java_com_dzkandian_app_http_utils_JniInterface_getSign(JNIEnv *env, jclass type,
                                                       jobject context, jstring str) {
//    if (isValid){  //判断是否合法
    const char *data = env->GetStringUTFChars(str, 0);
    jstring temp = EncodeUtils::geneSign(env, str);
    env->ReleaseStringUTFChars(str, data);
    return temp;
//    } else{
//       showToast(env,env->NewStringUTF("非法调用"));
//        return str;
//    }

}
