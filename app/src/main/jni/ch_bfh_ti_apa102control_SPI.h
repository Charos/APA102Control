/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class ch_bfh_ti_apa102control_SPI */

#ifndef _Included_ch_bfh_ti_apa102control_SPI
#define _Included_ch_bfh_ti_apa102control_SPI
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     ch_bfh_ti_apa102control_SPI
 * Method:    open
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_ch_bfh_ti_apa102control_SPI_open
  (JNIEnv *, jobject, jstring);

/*
 * Class:     ch_bfh_ti_apa102control_SPI
 * Method:    read
 * Signature: (I[II)I
 */
JNIEXPORT jint JNICALL Java_ch_bfh_ti_apa102control_SPI_read
  (JNIEnv *, jobject, jint, jintArray, jint);

/*
 * Class:     ch_bfh_ti_apa102control_SPI
 * Method:    write
 * Signature: (I[I)I
 */
JNIEXPORT jint JNICALL Java_ch_bfh_ti_apa102control_SPI_write
  (JNIEnv *, jobject, jint, jintArray);

/*
 * Class:     ch_bfh_ti_apa102control_SPI
 * Method:    close
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_ch_bfh_ti_apa102control_SPI_close
  (JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif
