#include <jni.h>
#include <android/log.h>

#include <LzmaTypes.h>
#include <LzmaRamDecode.h>
/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/HelloJni/HelloJni.java
 */
//jbyte*
#define  LOG_TAG    "libgl2jni"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

void Java_com_mobigain_dict_engine_DictEngine_DecoderLzma( JNIEnv* env, jobject thiz, jbyte* data, jint dataSize, jbyte* outData)
{
	size_t outSize;
	LOGI("Hello----------------------------------");
	if (LzmaRamGetUncompressedSize((unsigned char*)data, dataSize, &outSize) != 0)
		return;

	LOGI("Come here %d----------------------------------", outSize);

	unsigned int outSizeProcessed;
	jbyte* oData = (jbyte*)malloc(outSize);
	//int res = LzmaRamDecompress((unsigned char*)data,  dataSize, outData, outSize, &outSizeProcessed, malloc, free);

	int res = LzmaRamDecompress((unsigned char*)data,  dataSize, (unsigned char*)oData, outSize, &outSizeProcessed, malloc, free);
	if (res != 0)
		LOGI("Fail %d----------------------------------", res);
	else
		LOGI("Success----------------------------------");

	//jbyte* outData = new jbyte[(int)outSize];
	//int res = LzmaRamDecompress((unsigned char*)data,  dataSize, outData, outSize, &outSizeProcessed, malloc, free);
	//if (res != 0)
//		return 0;
//	delete outData;
}
