//
// Created by liupeng on 2018/3/13.
//

#include <stdint.h>
#include <string>
#include "EncodeUtils.h"
#include "md5.h"
#include "Utils.h"

#ifdef __cplusplus
extern "C" {
#endif
#include "aes.h"
void AES128_CBC_encrypt_buffer(uint8_t* output, uint8_t* input, uint32_t length, const uint8_t* key, const uint8_t* iv);

#ifdef __cplusplus
}
#endif

using namespace std;

static void initKey(uint8_t* pKey) {
    string oriKey = "appKey" + appEnv.deviceID + "appKey";
    const byte* md5 = MD5(oriKey).getDigest();
    memcpy(pKey, md5, KEY_LEN);
}

static string getSalt() {
  string salt = "&app_secret=85c5-c50b17b-894e-59d100c9e3-44e52ae";//公网
//   string salt = "&app_secret=3d0bffb0-6f-4fd09950fc76-ac5c9ac8-16";//内网

    return salt;
}

static void initIv(uint8_t* pIv) {
    uint8_t iv[]  = { 49, 50, 51, 52, 49, 50, 51, 52, 49, 50, 51, 52, 49, 50, 51, 52 }; // {1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4}
    for (int i = 0; i < KEY_LEN; i++) {
        pIv[i] = iv[i];
    }
}

jstring EncodeUtils::geneSign(JNIEnv *env, jstring &data) {
    string strData = jstring2String(env, data);
    string salt = getSalt();
    string oriSign = strData + salt;
    string strSign = MD5(oriSign).toStr();

//    logV(("originData: " + strData).c_str());
//    logV(("salt: " + salt).c_str());
//    logV(("oriSign: " + oriSign).c_str());

    return env->NewStringUTF(strSign.c_str());
}


