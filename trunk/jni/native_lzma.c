#include <jni.h>

#include <LzmaTypes.h>
#include <LzmaRamDecode.h>
/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/HelloJni/HelloJni.java
 */
//jbyte*
void Java_com_mobigain_dict_engine_DictEngine_DecoderLzma( JNIEnv* env, jobject thiz, jbyte* data, jint dataSize, jbyte* outData)
{
	size_t outSize;
	if (LzmaRamGetUncompressedSize((unsigned char*)data, dataSize, &outSize) != 0)
		return;

	unsigned int outSizeProcessed;
	LzmaRamDecompress((unsigned char*)data,  dataSize, outData, outSize, &outSizeProcessed, malloc, free);
	//jbyte* outData = new jbyte[(int)outSize];
	//int res = LzmaRamDecompress((unsigned char*)data,  dataSize, outData, outSize, &outSizeProcessed, malloc, free);
	//if (res != 0)
//		return 0;
//	delete outData;
}
