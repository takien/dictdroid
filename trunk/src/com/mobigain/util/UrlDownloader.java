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
				   Log.d("UrlDownloader - DownloadFromUrl", "download begining");
				   Log.d("UrlDownloader - DownloadFromUrl", "download url:" + url);
				   Log.d("UrlDownloader - DownloadFromUrl", "downloaded file name:" + pathFile);

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
				   //while ( (bufferLength = bis.read(buffer)) > 0 ) 
				   while ( (bufferLength = bis.read(buffer)) != -1 )
				   {  
					   baf.append(buffer, 0, bufferLength);
				   } 

				   /* Convert the Bytes read to a String. */

				   FileOutputStream fos = new FileOutputStream(file);
				   fos.write(baf.toByteArray());
				   fos.close();

				   Log.d("UrlDownloader - DownloadFromUrl", "download ready in"
								   + ((System.currentTimeMillis() - startTime) / 1000)
								   + " sec");
		   } 
		   catch (IOException e) 
		   {
				   Log.d("UrlDownloader - DownloadFromUrl", "Error: " + e);
		   }
	}

}


/*
package net.daleroy.fungifieldguide.activities;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Toast;
import net.daleroy.fungifieldguide.R;
import net.daleroy.fungifieldguide.fungifieldguideapplication;

public class FungiFieldGuide extends Activity {
    //static final int PROGRESS_DIALOG = 0;
    //ProgressThread progressThread;
    private final static String LOG_TAG = FungiFieldGuide.class.getSimpleName(); 
    fungifieldguideapplication appState;
    private DownloadFile mTask;
    public boolean mShownDialog;
    ProgressDialog progressDialog;
    private final static int DIALOG_ID = 1; 

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);

        if ( id == DIALOG_ID ) {
            mShownDialog = true;
        }
    }

    private void onTaskCompleted() {
        Log.i(LOG_TAG, "Activity " + this + " has been notified the task is complete.");

        //Check added because dismissDialog throws an exception if the current
        //activity hasn't shown it. This Happens if task finishes early enough
        //before an orientation change that the dialog is already gone when
        //the previous activity bundles up the dialogs to reshow.
        if ( mShownDialog ) {
            dismissDialog(DIALOG_ID);
            Toast.makeText(this, "Finished..", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case DIALOG_ID:
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Loading Database (only first run)...");
            return progressDialog;
        default:
            return super.onCreateDialog(id);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        appState = ((fungifieldguideapplication)this.getApplication());

        Object retained = getLastNonConfigurationInstance();
        if ( retained instanceof DownloadFile ) {
            Log.i(LOG_TAG, "Reclaiming previous background task.");
            mTask = (DownloadFile) retained;
            mTask.setActivity(this);
            //showDialog(DIALOG_ID);
        } 
        else {
            if(!appState.service.createDataBase())
            {
                Log.i(LOG_TAG, "Creating new background task.");
                //showDialog(DIALOG_ID);
                mTask = new DownloadFile(this);
                mTask.execute("http://www.codemarshall.com/Home/Download");
            }
        }
            //showDialog(PROGRESS_DIALOG);

        View btn_Catalog = findViewById(R.id.btn_Catalog);
        btn_Catalog.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {   
                Intent i = new Intent(getBaseContext(), Cat_Genus.class);//new Intent(this, Total.class);
                startActivity(i);
            }
        });

        View btn_Search = findViewById(R.id.btn_Search);
        btn_Search.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {   
                Intent i = new Intent(getBaseContext(), Search.class);//new Intent(this, Total.class);
                startActivity(i);
            }
        });
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
            mTask.setActivity(null);
            return mTask;
    } 

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //progressDialog.dismiss();
        //progressDialog = null;
        appState.service.ClearSearchParameters();
    }

    private class DownloadFile extends AsyncTask<String, Integer, Boolean>{
        private FungiFieldGuide activity;
        private boolean completed;
        private String Error = null;
        private String Content;

        private DownloadFile(FungiFieldGuide activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute()
        {
            showDialog(DIALOG_ID);
        }


        @Override
        protected Boolean doInBackground(String... urlarg) {
            int count;

            try {
                URL url = new URL(urlarg[0]);
                URLConnection conexion = url.openConnection();
                conexion.setDoInput(true);
                conexion.setUseCaches(false);

                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conexion.getContentLength();

                // downlod the file
                InputStream input = new BufferedInputStream(conexion.getInputStream());
                OutputStream output = new FileOutputStream("/data/data/net.daleroy.fungifieldguide/databases/Mushrooms.db");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    publishProgress((int)total*100/lenghtOfFile);
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.i(LOG_TAG, e.getMessage());
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Integer... args){
            progressDialog.setProgress(args[0]);
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            completed = true;
            notifyActivityTaskCompleted();
        }

        private void notifyActivityTaskCompleted() {
            if ( null != activity ) {
                activity.onTaskCompleted();
            }
        }

        private void setActivity(FungiFieldGuide activity) {
            this.activity = activity;
            if ( completed ) {
                    notifyActivityTaskCompleted();
            }
        }
    }
}

 */
