package com.example.zodlin.handlerexample;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;


/**
 * Android UI is not thread-safe, use Handler to update UI.
 *  |----------------- Handler ---------------------|
 *  |  -------------------------------------------  |
 *  |--> |Message| |Message| |Message| |Message| -->|     Looper
 *     -------------------------------------------
 *                  Message Queue
 * Handler處理資料,更新UI可以使用的方法：
 * ) post(Runnable)
 * ) postAtTime(Runnable, long)
 * ) postDelayed(Runnable, long)
 * ) sendEmptyMessage(int)
 * ) sendMessage(Message)
 * ) sendMessageAtTime(Message, long)
 * ) sendMessageDelayed(Message, long)
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.main_listview);
        String[] item = new String[] {
                HandlerActivity.class.getSimpleName(),
                };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, item);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String value = (String) parent.getItemAtPosition(position);
                    Log.d(TAG, "value: " + value);
                    Intent intent = new Intent();
                    intent.setClassName(getPackageName(), getPackageName() + "." + value);
                    startActivity(intent);
                } catch (ActivityNotFoundException e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
