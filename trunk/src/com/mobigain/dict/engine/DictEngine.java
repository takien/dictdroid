package com.mobigain.dict.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.graphics.Bitmap;
import android.os.SystemClock;

import com.mobigain.dict.compare.*;
import SevenZip.Compression.LZMA.*;

public class DictEngine
{
	public static int SIZEOF_BLOCK_INFO = 8;
	public static int SIZEOF_DIC_HEADER = 64;
	public static int SIZEOF_WORD_ITEM = 4;
	
	public static int WORD_LENGTH = 256;
	public static int WORD_SIZE = 2*WORD_LENGTH;
	
	public static int BLOCK_INDEX_SIZE = 15000;
	public static int DICT_BLOCK_SIZE = 50000;
	public static int MAX_MEAN_SIZE = 10000;
	public static int BLOCK_DATA_SIZE = DICT_BLOCK_SIZE + MAX_MEAN_SIZE;	
	
	EN_Compare en_compare = new EN_Compare();	
	KR_Compare kr_compare = new KR_Compare();
	VN_Compare vn_compare = new VN_Compare();
	
	BLOCK_INFO[] dataBlockInfo = null;
	
	private byte[] dataBlock = new byte[BLOCK_DATA_SIZE];
	private byte[] indexData = new byte[BLOCK_DATA_SIZE];
	private byte[] meaning = new byte[MAX_MEAN_SIZE];
	
	private int totalWord;
	private int numblockInDic;
	private int numWordInDic;
	private int indexInBlock;
	private int currentblockID;
	
	RandomAccessFile fDic = null;	
	
	private static native void DecoderLzma(byte[] data, int dataSize, byte[] outData, int outDataSize);
	
	
	public DictEngine()
	{
		
	}
	
	// 2-byte number
	static short ShortC2Java(int i)
	{
	    return (short)(((i>>8)&0xff)|((i << 8)&0xff00));
	}

	// 4-byte number
	static int IntC2Java(int i)
	{
	    return((i&0xff)<<24)|((i&0xff00)<<8)|((i&0xff0000)>>8)|((i>>24)&0xff);
	}
	
	public void OpenDict(String path)
	{
		try
		{
			if (fDic != null)
			{
				fDic.close();
				fDic = null;
			}
			
			fDic = new RandomAccessFile(path, "r");
			
			DIC_HEADER dicHeader = new DIC_HEADER();
			fDic.seek(0);
			dicHeader.numEntry = IntC2Java(fDic.readInt());
			dicHeader.numBlock = IntC2Java(fDic.readInt());
			
			fDic.read(dicHeader.bRes, 0, 56);
			
			numWordInDic = dicHeader.numEntry;
			numblockInDic = dicHeader.numBlock;
			dataBlockInfo = new BLOCK_INFO[numblockInDic];
			
			for(int i = 0; i < numblockInDic; i++)
			{
				dataBlockInfo[i] = new BLOCK_INFO();
				dataBlockInfo[i].blockAdress = IntC2Java(fDic.readInt());
				dataBlockInfo[i].blockSize = ShortC2Java(fDic.readShort());
				dataBlockInfo[i].numWord = ShortC2Java(fDic.readShort());
			}
			
			ReadDataAtBlock(indexData, 0);
			ReadDataAtBlock(dataBlock, 1);
		}
		catch (Exception ex)
		{
			String exStr = ex.getMessage();
			int a = 0;
		}
	}
	
	public int GetNumWordInDic()
	{
		return numWordInDic;
	}
	
	public void OnEditSearch(String editText)
	{
		
		
	}
	
	private String GetIndexData(int numEntry)
	{
		try
		{
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(indexData, numEntry*SIZEOF_WORD_ITEM, SIZEOF_WORD_ITEM);
			DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
			
			WORD_ITEM wordItem = new WORD_ITEM();
			wordItem.pos =  ShortC2Java(dataInputStream.readShort());
			wordItem.len =  ShortC2Java(dataInputStream.readShort());
			
			//US-ASCII  	Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode character set
			//ISO-8859-1   	ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1
			//UTF-8 	Eight-bit UCS Transformation Format
			//UTF-16BE 	Sixteen-bit UCS Transformation Format, big-endian byte order
			//UTF-16LE 	Sixteen-bit UCS Transformation Format, little-endian byte order
			//UTF-16 	Sixteen-bit UCS Transformation Format, byte order identified by an optional byte-order mark
			
			String wordString = new String(indexData, wordItem.pos, wordItem.len, "UTF-16BE");
			
			dataInputStream.close();
			byteArrayInputStream.close();
			
			return wordString;
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	private String GetWordData(int numEntry)
	{
		try
		{
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataBlock, numEntry*SIZEOF_WORD_ITEM, SIZEOF_WORD_ITEM);
			DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
			
			WORD_ITEM wordItem = new WORD_ITEM();
			wordItem.pos =  ShortC2Java(dataInputStream.readShort());
			wordItem.len =  ShortC2Java(dataInputStream.readShort());
			
			byte wordSize = dataBlock[wordItem.pos];
			
			String wordString = new String(dataBlock, wordItem.pos + 1, wordSize, "UTF-16BE");
			
			dataInputStream.close();
			byteArrayInputStream.close();
			
			return wordString;
			
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	public String GetMeanWord(int index)
	{
		return null;
	}
	
	private int CompareString(String text1, String text2)
	{
		return 0;
	}
	
	public int NumWordInDict()
	{
		return totalWord;
	}
	
	void ReadDataAtBlock(byte[] blockData, int block)
	{
		try
		{
			int posInFile = dataBlockInfo[block].blockAdress;
			int dataSize = dataBlockInfo[block].blockSize;
			byte[] data = new byte[dataSize];
			
			fDic.seek(posInFile);
			fDic.read(data, 0, dataSize);
			
			//long time = System.currentTimeMillis();
			DecoderLzma(data, dataSize, blockData, BLOCK_DATA_SIZE);
			//time = SystemClock.uptimeMillis() - time;
			//int size = 0;
		}
		catch (Exception ex)
		{
			String exStr = ex.getMessage();
			int a = 0;			
		}
	}
	
	int OnEditSearch(String editText)
	{
		int l1 = 1;
		int l2 = indexWordNum;
		int maxBlock = l2;
		int l3;
		int lCmp;

		short * editTextPtr = (short*)editText->GetPointer();
		int editTextPtrLength = editText->GetLength();
		while (l2>=l1)
		{
			l3 = (l1 + l2)/2;
			if (l3 == maxBlock)
				break;
			memset(word, 0, WORD_SIZE);
			GetIndexData(l3, word);
			lCmp = CompareString((const unsigned short*) editTextPtr, editTextPtrLength,
								  (const unsigned short*)word, word_size);
			if (lCmp == 0)
				break;
			else if (lCmp<0)
				l2 = l3 - 1;
			else
				l1 = l3 + 1;
		}

		if (l1 > l2)
			l3++;
		if (l3 > l2)
			l3 = l2 + 1;

		currentblockID = l3;
		//ReadDataAtBlock(dataBlock1, currentblockID);
		lastblockID = l3;
		ReadDataAtBlock(data_BB_Block, currentblockID);

		l1 = 0;
		l2 = dataBlockInfo[currentblockID].numWord - 1;
		dataWordNum = l2;

		while (l2>=l1)
		{
			l3 = (l1 + l2)/2;
			memset(word, 0, WORD_SIZE);

			GetWordData(l3, word);
			lCmp = CompareString((const unsigned short*) editTextPtr, editTextPtrLength,
										  (const unsigned short*)word, word_size);
			if (lCmp == 0)
				break;
			else if (lCmp<0)
				l2 = l3 - 1;
			else
				l1 = l3 + 1;
		}

		if (l1 > l2)
			l3++;
		if (l3 > l2)
			l3 = l2 + 1;

		indexInBlock = l3;
		/* ko can thiet
		listCurSel = 0;
		if (indexInBlock > dataWordNum - NUM_WORD_INLIST)
		{
			int nextBlock = currentblockID + 1;
			if (nextBlock>= numblockInDic)
				nextBlock = 1;
			ReadDataAtBlock(dataBlock2, nextBlock);
		}
		GetWordList();
		*/
		return GetWordIndex();
	}
}
	/*
class DictEngine {
public:
	DictEngine(String path, int compareVal);
	virtual ~DictEngine();

	//
	void OnEditSearch(short* editText);
	//short* GetMeanWord();
	String* GetMeanWord();
	void OnSearchListUp();
	void OnSearchListDown();
	ArrayList* wordList; //item = String*

private:
	File* fDic;
	char* dataBlock2;
	char* dataBlock1;
	char* data_BB_Block;
	char* indexData;
	short* word;
	int word_size;
	BLOCK_INFO* dataBlockInfo;
	char* meaning;

	unsigned int indexWordNum;
	unsigned int dataWordNum;
	unsigned short sizeMean;
	int currentblockID;
	int lastblockID;
	int indexInList;
	int indexInBlock;
	int numblockInDic;
	int listCurSel;

	int compareValue;
	CKrCharCompare* kcompare;
	//CVCharCompare* vcompare;
	CVExCharCompare* vcompare;
	CECharCompare* ecompare;

	void InitDictionary();
	void SwapData(char* &data1, char* &data2);
	void GetMeanData(char* data, int numEntry);
	void GetWordList();
	inline void ReadDataAtBlock(char* blockData, unsigned int block);
	inline void GetIndexData(int numEntry, short* word);
	inline int CompareString(const unsigned short* text1, const unsigned short* text2);
	inline int CompareString(const unsigned short* text1, int text1Length, const unsigned short* text2, int text2Length);
	inline void GetWordData(int numEntry, short* word);
public:
	String* GetWord(int index);
	String* GetMeanWord(int index);
	int GetWordIndex();
	int OnEditSearch(String* editText);
	int totalWord;
	int **transindex;
	void CreateTransIndex();
	void CreateListIndex();
	int **__ListIndex;
	int totalIndex;
};

#endif /* DICTENGINE_H_ */

