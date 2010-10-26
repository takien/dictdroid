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
	final static int ENGLISH_COMAPRE = 0;
	final static int VIETNAMESE_COMPARE = 1;
	final static int KOREAN_COMPARE = 2;
	
	final static int SIZEOF_BLOCK_INFO = 8;
	final static int SIZEOF_DIC_HEADER = 64;
	final static int SIZEOF_WORD_ITEM = 4;
	
	final static int WORD_LENGTH = 256;
	final static int WORD_SIZE = 2*WORD_LENGTH;
	
	final static int BLOCK_INDEX_SIZE = 15000;
	final static int DICT_BLOCK_SIZE = 50000;
	final static int MAX_MEAN_SIZE = 10000;
	final static int BLOCK_DATA_SIZE = DICT_BLOCK_SIZE + MAX_MEAN_SIZE;	
	
	EN_Compare en_compare = new EN_Compare();	
	KR_Compare kr_compare = new KR_Compare();
	VN_Compare vn_compare = new VN_Compare();
	
	BLOCK_INFO[] dataBlockInfo = null;
	
	byte[] dataBlock = new byte[BLOCK_DATA_SIZE];
	byte[] indexData = new byte[BLOCK_DATA_SIZE];
	byte[] meaning = new byte[MAX_MEAN_SIZE];
	
	int totalWord;
	int numblockInDic;
	int numWordInDic;
	int indexInBlock;
	int currentblockID;
	int compareType;
	int indexWordNum;
	int lastblockID;
	int dataWordNum;
	
	RandomAccessFile fDic = null;	
	
	private static native void DecoderLzma(byte[] data, int dataSize, byte[] outData, int outDataSize);
	
	
	public DictEngine()
	{
		
	}
	
	// 2-byte number
	/*
	static short ShortC2Java(int i)
	{
	    return (short)(((i>>8)&0xff)|((i << 8)&0xff00));
	}
	*/
	static char ShortC2Java(int i)
	{
	    return (char)(((i>>8)&0xff)|((i << 8)&0xff00));
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
			
			indexInBlock = 0;
			currentblockID = 1;
			
			compareType = ENGLISH_COMAPRE;
			indexWordNum = dataBlockInfo[0].numWord;
			lastblockID = 1;
			
			ReadDataAtBlock(indexData, 0);
			ReadDataAtBlock(dataBlock, 1);
			
			//OnEditSearch("hello");
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
			
			String wordString = new String(indexData, wordItem.pos, wordItem.len, "UTF-16LE");
			
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
			
			String wordString = new String(dataBlock, wordItem.pos + 1, wordSize, "UTF-16LE");
			
			dataInputStream.close();
			byteArrayInputStream.close();
			
			return wordString;
			
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	public String GetWord(int index)
	{
		int wordNum = 0;
		int i = 1;
		String word = "NULL";
		while (i < numblockInDic)
		{
			wordNum += dataBlockInfo[i].numWord;
			if(wordNum > index)
			{				
				int wordID = (dataBlockInfo[i].numWord) - (wordNum - index);

				if(lastblockID != i)
				{
					lastblockID = i;
					ReadDataAtBlock(dataBlock, i);
				}
				
				try
				{
					ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataBlock, wordID*SIZEOF_WORD_ITEM, SIZEOF_WORD_ITEM);
					DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
					
					WORD_ITEM wordItem = new WORD_ITEM();
					wordItem.pos =  ShortC2Java(dataInputStream.readShort());
					wordItem.len =  ShortC2Java(dataInputStream.readShort());
					
					byte wordSize = dataBlock[wordItem.pos];
					word = new String(dataBlock, wordItem.pos + 1, wordSize, "UTF-16LE");
	
					dataInputStream.close();
					byteArrayInputStream.close();
				}
				catch (Exception ex)
				{
					String exStr = ex.getMessage();
				}
				
				break;
			}
			i++;
		}
		return word;
	}
	public String GetMeanWord(int index)
	{
		return null;
	}
	
	private int CompareString(String text1, String text2)
	{
		switch (compareType)
		{
			case ENGLISH_COMAPRE:
				return en_compare.compare(text1, text2);

			case VIETNAMESE_COMPARE:
				return vn_compare.compare(text1, text2);
				
			case KOREAN_COMPARE:
				return kr_compare.compare(text1, text2);
			
			default:
				return 0;
		}
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
	
	public int OnEditSearch(String editText)
	{		
		int l1 = 1;
		int l2 = indexWordNum;
		int maxBlock = l2;
		int l3 = 0;
		int lCmp;
		String word = null;
		while (l2>=l1)
		{
			l3 = (l1 + l2)/2;
			if (l3 == maxBlock)
				break;			
			
			word = GetIndexData(l3);
			lCmp = CompareString(editText, word);
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
		lastblockID = l3;
		ReadDataAtBlock(dataBlock, currentblockID);

		l1 = 0;
		l2 = dataBlockInfo[currentblockID].numWord - 1;
		dataWordNum = l2;

		while (l2>=l1)
		{
			l3 = (l1 + l2)/2;
			
			word = GetWordData(l3);
			lCmp = CompareString(editText, word);
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
		return GetWordIndex();		
	}


	int GetWordIndex()
	{
		int wordIndex = 0;
		int i = 1;
		while (i < currentblockID)
		{
			wordIndex += dataBlockInfo[i].numWord;
			i++;
		}
		wordIndex = wordIndex + indexInBlock;
		return wordIndex;
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

