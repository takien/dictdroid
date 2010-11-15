package com.mobigain.dict;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class MyActivity extends Activity
{
	private final static String LOG_TAG = MyActivity.class.getSimpleName();

	    private final static int DIALOG_ID = 1;

	    private Task mTask;

	    private boolean mShownDialog;
	    
	    ProgressDialog dialog; 

	    protected void onPrepareDialog(int id, Dialog dialog) 
	    {
	        super.onPrepareDialog(id, dialog);

	        if ( id == DIALOG_ID ) {
	            mShownDialog = true;
	        }
	    }

	    private void onTaskCompleted() {
	        Log.i(LOG_TAG, "Activity "  + this +  " has been notified the task is	complete.");

	        //Check added because dismissDialog throws an exception if the current
	        //activity hasn't shown it. This Happens if task finishes early	enough
	        //before an orientation change that the dialog is already gone when
	        //the previous activity bundles up the dialogs to reshow.
	        if ( mShownDialog ) 
	        {
	            dismissDialog(DIALOG_ID);
	            Toast.makeText(this, "Finished..", Toast.LENGTH_LONG).show();
	        }
	    }

	    @Override	    
	    protected void onCreate(android.os.Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(com.mobigain.dict.R.layout.main);

	        Object retained = getLastNonConfigurationInstance();
	        if ( retained instanceof Task ) {
	            Log.i(LOG_TAG, "Reclaiming previous background task.");
	            mTask = (Task) retained;
	            mTask.setActivity(this);
	        } else {
	            Log.i(LOG_TAG, "Creating new background task.");
	            mTask = new Task(this);
	            mTask.execute();
	        }
	    }

	    @Override
	    public Object onRetainNonConfigurationInstance() {
	        mTask.setActivity(null);
	        return mTask;
	    }

	    @Override
	    protected Dialog onCreateDialog(int id) {
	        switch (id) {
	            case DIALOG_ID:
	                //ProgressDialog dialog = new ProgressDialog(this);
	            	dialog = new ProgressDialog(this);
	                dialog.setMessage("Loading stuff..");
	                dialog.setCancelable(true);
	                return dialog;
	        }
	        return super.onCreateDialog(id);
	    }

	    private static class Task extends AsyncTask<Void, Integer, Void> {

	        private MyActivity activity;

	        private boolean completed;

	        private Task(MyActivity activity) {
	            this.activity = activity;
	        }

	        @Override
	        protected void onPreExecute() {
	            activity.showDialog(DIALOG_ID);
	        }

	        @Override
	        protected Void doInBackground(Void... unused) {
	        	
	        	/*
	            try {
	                Log.i(LOG_TAG, "Background thread starting sleep.");
	                Thread.sleep(15 * 1000);
	            } catch (InterruptedException e) {
	                Log.e(LOG_TAG, "Thread interrupted:", e);
	            }
	            Log.i(LOG_TAG, "Background thread finished sleep.");
	            */
	        	try
	        	{
		        	for(int i = 0; i < 100; i++)
		        	{
		        		Thread.sleep(100);
		        		publishProgress(i);
		        	}	        		
	        	}
	        	catch (InterruptedException e) {
	                Log.e(LOG_TAG, "Thread interrupted:", e);
	            }	        	
	            return null;
	        }

	        @Override
	        protected void onProgressUpdate(Integer... values)
	        {
	        	// TODO Auto-generated method stub
	        	activity.dialog.setMessage("Loading: " + values[0].toString() + "%");

	        	super.onProgressUpdate(values);
	        }
	        @Override
	        protected void onPostExecute(Void unused) {
	            completed = true;
	            notifyActivityTaskCompleted();
	        }

	        private void notifyActivityTaskCompleted() {
	            if ( null != activity ) {
	                activity.onTaskCompleted();
	            }
	        }

	        private void setActivity(MyActivity activity) {
	            this.activity = activity;
	            if ( completed ) {
	                notifyActivityTaskCompleted();
	            }
	        }

	    }
}
