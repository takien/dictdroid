/*
http://progrnotes.blogspot.com/2010/09/c-android.html
http://www.eigo.co.uk/Threads-and-Progress-Dialogs-in-Android-Screen-Orientation-Rotations.aspx
	
package com.mobigain.util;

private class DownloadImageTask extends AsyncTask<String, Integer, Drawable> {

	  	private Drawable d; 
	     private HttpURLConnection conn;
	     private InputStream stream; //to read 
	     private ByteArrayOutputStream out; //to write
	     
	     private double fileSize;
	     private double downloaded; // number of bytes downloaded
	     private int status = DOWNLOADING; //status of current process
	     
	     private ProgressDialog progressDialog;     

	     private static final int MAX_BUFFER_SIZE = 1024; //1kb
	     public static final int DOWNLOADING = 0;
	     public static final int COMPLETE = 1;
	     
	     public  DownloadImageTask(){
	      d = null;
	      conn = null;
	      fileSize = 0;
	      downloaded = 0;
	      status = DOWNLOADING;
	     }
	   public boolean isOnline() {
	   try {
	    ConnectivityManager cm = (ConnectivityManager) 
	             getSystemService(Context.CONNECTIVITY_SERVICE);
	    return cm.getActiveNetworkInfo().isConnectedOrConnecting();
	   } catch (Exception e) {
	    return false;
	   }
	  }
	   @Override
	  protected Drawable doInBackground(String... url) {

	   try {
	    if (isOnline() == true) {

	     Log.v("Test", "Starting loading image by URL: ");
	     conn = (HttpURLConnection) new URL(url[0]).openConnection();
	     fileSize = conn.getContentLength();
	     out = new ByteArrayOutputStream((int) fileSize);
	     conn.connect();

	     stream = conn.getInputStream();
	     // loop with step 1kb
	     while (status == DOWNLOADING) {
	      byte buffer[];

	      if (fileSize - downloaded > MAX_BUFFER_SIZE) {
	       buffer = new byte[MAX_BUFFER_SIZE];
	      } else {
	       buffer = new byte[(int) (fileSize - downloaded)];
	      }
	      int read = stream.read(buffer);

	      if (read == -1) {
	       publishProgress(100);
	       break;
	      }
	      // writing to buffer
	      out.write(buffer, 0, read);
	      downloaded += read;
	      // update progress bar
	      publishProgress((int) ((downloaded / fileSize) * 100));
	     }// end of while

	     if (status == DOWNLOADING) {
	      status = COMPLETE;
	     }
	     d = Drawable.createFromStream(
	       (InputStream) new ByteArrayInputStream(out
	         .toByteArray()), "filename");
	     return d;

	    }// end of if isOnline
	    else {
	     return null;
	    }
	   } catch (Exception e) {
	    System.out.println("Exception: " + e);
	    return null;
	   }// end of catch
	  }// end of class DownloadImageTask()
	   
	  @Override
	  protected void onProgressUpdate(Integer... changed) {
	   progressDialog.setProgress(changed[0]);
	     }
	  
	  @Override
	  protected void onPreExecute() {
	   progressDialog = new ProgressDialog(ImageViewer.this);
	   progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	   progressDialog.setMessage("Loading...");
	   progressDialog.setCancelable(false);
	   progressDialog.show();
	  }
	  
	  @Override
	  protected void onPostExecute(Drawable result) {
	   if(result != null){
	   progressDialog.dismiss();
	   setImage(d);
	   }
	   else {
	    progressDialog.dismiss();
	    AlertDialog alertDialog;
	    alertDialog = new AlertDialog.Builder(ImageViewer.this).create();
	    alertDialog.setTitle(R.string.infoLabel);
	    alertDialog.setMessage(getString(R.string.loadErrorLabel));
	    alertDialog.setButton(getString(R.string.closeLabel),
	      new DialogInterface.OnClickListener() {
	       public void onClick(DialogInterface dlg, int sum) {
	        // do nothing, close
	       }
	      });
	    alertDialog.show();
	   }
	  }
	 }//end of class DownloadImageTask()
*/