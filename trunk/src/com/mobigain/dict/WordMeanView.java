package com.mobigain.dict;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WordMeanView extends Activity
{
	 @Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.wordmean);
		
		WebView web = (WebView) findViewById(R.id.wordmean);
        web.getSettings().setJavaScriptEnabled(true);
        /*
        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        web.getSettings().setPluginsEnabled(false);
        web.getSettings().setSupportMultipleWindows(false);
        web.getSettings().setSupportZoom(false);
        web.setVerticalScrollBarEnabled(false);
        web.setHorizontalScrollBarEnabled(false);
 		*/
        //Our application's main page will be loaded
        web.loadUrl("file://sdcard/mean.html");	 
        //web.loadUrl("http://www.google.com");
        
        web.setWebViewClient(new WebViewClient() 
        {
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) 
            {
                return false;
            }
        });

	}
}
