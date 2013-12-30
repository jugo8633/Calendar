LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-subdir-java-files)\
			src/com/hp/ij/common/service/baseservice/IRemoteServiceCallback.aidl\
			src/com/hp/ij/common/service/baseservice/IBaseService.aidl

LOCAL_PACKAGE_NAME := CalendarUI
LOCAL_MODULE_TAGS := samples
#LOCAL_MODULE_PATH :=  $(TARGET_HP_PRIVATE_APPS)

include $(BUILD_PACKAGE)
