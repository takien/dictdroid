LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := dictdroid

LOCAL_SRC_FILES := \
    BranchX86.c \
    LzmaDecode_Ansi.c \
    LzmaRamDecode.c \
    native_lzma.c \

LOCAL_LDLIBS := -lm -llog

include $(BUILD_SHARED_LIBRARY)
