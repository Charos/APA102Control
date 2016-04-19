//
// Created by student on 12.04.16.
//
#undef __cplusplus

#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>

#include <linux/i2c.h>
#include <memory.h>
#include <malloc.h>

#include <jni.h>

#include <android/log.h>
//#include <linux/spi.h>
#include "ch_bfh_ti_apa102control_SPI.h"


/* Define if we use the emulator */
#undef EMULATOR
#define JNIEXPORT __attribute__ ((visibility ("default")))

#define  LOG_TAG    "spi"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

/************************************************************************************************************************************/
/* Open the spi device
/************************************************************************************************************************************/

JNIEXPORT jint JNICALL Java_ch_bfh_ti_apa102control_SPI_open(JNIEnv *env,jobject obj, jstring file)
{
    #ifndef EMULATOR

    /* File descriptor to spi-dev */
    int spi_fd;

    char fileName[64];
    const jbyte *str;

    /* Get device file name */
    str = (*env)->GetStringUTFChars(env, file, NULL);
    if (str == NULL)
    {
        LOGE("Can't get file name!");
        return -1;
    }

    /* Get the device file name */
    sprintf(fileName, "%s", str);
    LOGI("Open spi device node %s", fileName);
    (*env)->ReleaseStringUTFChars(env, file, str);

    spi_fd = open(fileName, O_RDWR);
    LOGE("spi_fd: %d",spi_fd);
    return (jint) spi_fd;
#else
    return 0;
#endif
}

/************************************************************************************************************************************/
/* Read from the spi device																											*/
/************************************************************************************************************************************/

JNIEXPORT jint JNICALL Java_ch_bfh_ti_apa102control_SPI_read(JNIEnv * env, jobject obj, jint fileHander, jintArray bufArray, jint len)

{
#ifndef EMULATOR

    jint *bufInt;
    char *bufByte;
    char bytesRead = 0;
    int  i=0;

    /* Check for a valid array size */
    if (len <= 0)
    {
        LOGE("SPI: array size <= 0");
        return -1;
    }

    /* Allocate the necessary buffers */
    bufInt = (jint *) malloc(len * sizeof(int));
    if (bufInt == 0)
    {
        LOGE("SPI: Out of memory!");
        goto err0;
    }
    bufByte = (char*) malloc(len);
    if (bufByte == 0)
    {
        LOGE("SPI: Out of memory!");
        goto err1;
    }

    (*env)->GetIntArrayRegion(env, bufArray, 0, len, bufInt);

    /* Clear the spi buffer */
    memset(bufByte, '\0', len);
    if ((bytesRead = read(fileHander, bufByte, len)) != len)
    {
        LOGE("spi read failed!");
        goto err2;
    }
    else
    {
        /* Copy the spi data elements to the Java array */
        for (i=0; i<bytesRead ; i++)
            bufInt[i] = bufByte[i];
        (*env)->SetIntArrayRegion(env, bufArray, 0, len, bufInt);
    }
    /* Cleanup and return number of bytes read */
    free(bufByte);
    free(bufInt);
    return bytesRead;

    err2:
    free(bufByte);
    err1:
    free(bufInt);
    err0:
    return -1;

#else
    return 0;
#endif
}

/************************************************************************************************************************************/
/* Write to the spi device																											*/
/************************************************************************************************************************************/

JNIEXPORT jint JNICALL Java_ch_bfh_ti_apa102control_SPI_write(JNIEnv *env, jobject obj, jint fileHander, jintArray inJNIArray, jint len)
{
#ifndef EMULATOR

    jbyte *bytePtr;
    jbyte spiCommBuffer[255];
    jint  i, bytesWritten;

    /* Check for a valid array size */
    if ((len <= 0) || (len > 255))
    {
        LOGE("spi: array size <= 0 | > 255");
        return -1;
    }

    /* Convert the incoming JNI jbyteArray to C's jbyte[] */
    jint *inCArray = (*env)->GetIntArrayElements(env, inJNIArray, NULL);

    /* Return on error */
    if (NULL == inCArray)
        return (jint) NULL;

    /* Get the array length */
    jsize length = (*env)->GetArrayLength(env, inJNIArray);

    /* Get the spi data elements from the java array*/
    for (i=0; i<length; i++)
    {
        spiCommBuffer[i] = (jbyte) inCArray[i];
    }

    /* Release resources */
    (*env)->ReleaseIntArrayElements(env, inJNIArray, inCArray, 0);

    /* Write data to the spi device */
    bytesWritten = write(fileHander, spiCommBuffer, len);

    /* Inform user if not all bytes are written */
    if (bytesWritten != len)
    {
        LOGE("Write to the spi device failed!");
        LOGE("%d",bytesWritten);
        return -1;
    }
    return bytesWritten;

#else
    return 0;
#endif
}

/************************************************************************************************************************************/
/* Close the i2c interface																										    */
/************************************************************************************************************************************/

JNIEXPORT jint JNICALL Java_ch_bfh_ti_apa102control_SPI_close(JNIEnv *env, jobject obj, jint fileHander)
{
#ifndef EMULATOR
    close(fileHander);
#endif
    return (jint)NULL;
}