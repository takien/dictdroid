package com.mobigain.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.util.Log;


public class FileUtil
{
	public static boolean WriteFile(String strFilePath, String strFileContents)
	{
		File file = new File(strFilePath);
		try
		{
			BufferedWriter bf = new BufferedWriter(new FileWriter(file, false));
			bf.write(strFileContents);
			bf.close();
			return true;
		}
		catch (Exception ex)
		{
			Log.d("FileUtil - WriteFile", ex.getMessage());
			return false;
		}
	}

}
