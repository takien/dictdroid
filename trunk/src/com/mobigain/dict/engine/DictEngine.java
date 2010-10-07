package com.mobigain.dict.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.RandomAccessFile;

import com.mobigain.dict.compare.*;
import SevenZip.Compression.LZMA.*;

public class DictEngine
{
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
	private int indexInBlock;
	private int currentblockID;
	
	RandomAccessFile fDic = null;
	Decoder decoder = new Decoder();
	public DictEngine()
	{
		
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
			dicHeader.numEntry = fDic.readInt();
			dicHeader.numBlock = fDic.readInt();
			fDic.read(dicHeader.bRes, 0, 56);
			
			numblockInDic = dicHeader.numBlock;
		
		
		}
		catch (Exception ex)
		{
			
		}
	}
	
	public void OnEditSearch(String editText)
	{
		
	}
	
	public String GetWord(int index)
	{
		return null;
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
	
	void ReadDataAtBlock(int block)
	{
		try
		{
			int posInFile = dataBlockInfo[block].blockAdress;
			int dataSize = dataBlockInfo[block].blockSize;
			byte[] data = new byte[dataSize];
			
			fDic.seek(posInFile);
			fDic.read(data, 0, dataSize);
			
			ByteArrayInputStream inStream = new ByteArrayInputStream(data);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream(BLOCK_DATA_SIZE);
			int outSize = 0;
			boolean bDecode = decoder.Code(inStream, outStream, outSize);			
		}
		catch (Exception ex)
		{
			
		}
		
		
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

