package com.mobigain.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.os.Environment;
import android.util.Log;

public class UrlDownloader
{
	public static void DownloadFromUrl(String fileUrl, String pathFile) 
	{  //this is the downloader method

		   try 
		   {
				   URL url = new URL(fileUrl); //you can write here any link
				   
				   //File SDCardRoot = Environment.getExternalStorageDirectory();  
				   //File file = new File(SDCardRoot,pathFile);  
				   File file = new File(pathFile);

				   long startTime = System.currentTimeMillis();
				   Log.d("ImageManager", "download begining");
				   Log.d("ImageManager", "download url:" + url);
				   Log.d("ImageManager", "downloaded file name:" + pathFile);

				   /* Open a connection to that URL. */
				   URLConnection ucon = url.openConnection();

				   /*

					* Define InputStreams to read from the URLConnection.

					*/
				   InputStream is = ucon.getInputStream();		
				   int fileSize = ucon.getContentLength();
				   
				   BufferedInputStream bis = new BufferedInputStream(is);				   
				   /*

					* Read bytes to the Buffer until there is nothing more to read(-1).

					*/

				   
				   ByteArrayBuffer baf = new ByteArrayBuffer(fileSize);
				   /*
				   int current = 0;
				   while ((current = bis.read()) != -1) 
				   {
					   baf.append((byte) current);
				   }
				   */
				   byte[] buffer = new byte[1024];  
				   int bufferLength = 0;   
				   while ( (bufferLength = bis.read(buffer)) > 0 ) 
				   {  
					   baf.append(buffer, 0, bufferLength);
				   } 

				   /* Convert the Bytes read to a String. */

				   FileOutputStream fos = new FileOutputStream(file);
				   fos.write(baf.toByteArray());
				   fos.close();

				   Log.d("ImageManager", "download ready in"
								   + ((System.currentTimeMillis() - startTime) / 1000)
								   + " sec");
		   } 
		   catch (IOException e) 
		   {
				   Log.d("ImageManager", "Error: " + e);
		   }
	}

}
