package com.mobigain.dict;

import com.mobigain.dict.engine.DictEngine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainSearch extends Activity {
    /* load our native library */
    static {
        System.loadLibrary("dictdroid");
    }
    
    private static class WordEfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private DictEngine mDictEngine = null;
        public WordEfficientAdapter(Context context, DictEngine dictEngine) 
        {
            mInflater = LayoutInflater.from(context);
            mDictEngine = dictEngine;
        }

        /**
         * The number of items in the list is determined by the number of speeches
         * in our array.
         *
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() 
        {
            return mDictEngine.GetNumWordInDic();
        }

        /**
         * Since the data comes from an array, just returning the index is
         * sufficent to get at the data. If we were using a more complex data
         * structure, we would return whatever object represents one row in the
         * list.
         *
         * @see android.widget.ListAdapter#getItem(int)
         */
        public Object getItem(int position) 
        {
            return position;
        }

        /**
         * Use the array index as a unique id.
         *
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position) 
        {
            return position;
        }

        /**
         * Make a view to hold each row.
         *
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) 
        {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is no need
            // to reinflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) 
            {
                convertView = mInflater.inflate(R.layout.word_list_item, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.word = (TextView) convertView.findViewById(R.id.word);
                //holder.arrow = (ImageView) convertView.findViewById(R.id.arrow);

                convertView.setTag(holder);
            } 
            else 
            {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.word.setText(mDictEngine.GetWord(position));
            //holder.word.setImageBitmap(R.drawable.arrow);

            return convertView;
        }

        static class ViewHolder 
        {
            TextView word;
            ImageView arrow;
        }
    }
    private ListView _listViewWord;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        DictEngine dictEngine = new DictEngine();
        dictEngine.OpenDict("/sdcard/envn.mdo");
        
        _listViewWord = (ListView)findViewById(R.id.listWord);
        _listViewWord.setAdapter(new WordEfficientAdapter(this, dictEngine));
    }
}