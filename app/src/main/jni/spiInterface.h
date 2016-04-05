/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class bfh_ti_i2c_jni_template_I2C */

#ifndef _Included_bfh_ti_i2c_jni_template_I2C
#define _Included_bfh_ti_i2c_jni_template_I2C
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     bfh_ti_i2c_jni_template_I2C
 * Method:    open
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_bfh_ti_i2c_1jni_1template_I2C_open
        (JNIEnv *, jobject, jstring);

/*
 * Class:     bfh_ti_i2c_jni_template_I2C
 * Method:    SetSlaveAddress
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_bfh_ti_i2c_1jni_1template_I2C_SetSlaveAddress
        (JNIEnv *, jobject, jint, jint);

/*
 * Class:     bfh_ti_i2c_jni_template_I2C
 * Method:    read
 * Signature: (I[II)I
 */
JNIEXPORT jint JNICALL Java_bfh_ti_i2c_1jni_1template_I2C_read
        (JNIEnv *, jobject, jint, jintArray, jint);

/*
 * Class:     bfh_ti_i2c_jni_template_I2C
 * Method:    write
 * Signature: (I[II)I
 */
JNIEXPORT jint JNICALL Java_bfh_ti_i2c_1jni_1template_I2C_write
        (JNIEnv *, jobject, jint, jintArray, jint);

/*
 * Class:     bfh_ti_i2c_jni_template_I2C
 * Method:    close
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_bfh_ti_i2c_1jni_1template_I2C_close
        (JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif
