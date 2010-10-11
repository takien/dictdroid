package com.mobigain.dict;

import com.mobigain.dict.engine.DictEngine;

import android.app.Activity;
import android.os.Bundle;

public class MainSearch extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        DictEngine dictEngine = new DictEngine();
        dictEngine.OpenDict("/sdcard/envn.mdo");
    }
}