LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS    := -llog
LOCAL_MODULE    := spi
LOCAL_SRC_FILES := spiLib.c

include $(BUILD_SHARED_LIBRARY)