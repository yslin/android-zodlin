package com.example.zodlin.handlerexample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;


/**
 * Handler處理資料,更新UI可以使用的方法：
 * 1) post(Runnable)
 * 2) postAtTime(Runnable, long)
 * 3) postDelayed(Runnable, long)
 * 4) sendEmptyMessage(int)
 * 5) sendMessage(Message)
 * 6) sendMessageAtTime(Message, long)
 * 7) sendMessageDelayed(Message, long)
 * 傳遞物件用msg.obj
 */
public class HandlerActivity extends Activity {
    private static final String TAG = "HandlerActivity";
    private static final int MSG_INIT_PROGRESS_BAR = 1;
    private static final int MSG_INCREASE_PROGRESS_BAR = 2;
    private ProgressBar mProgressBar;
    private Button mStartButton, mInitButton;
    private Button mFirstButton, mSecondButton, mThirdButton;
    private int mProgress = 0;
    private Handler mHandle = new Handler() {
        /**
         * Register to system.
         */
        @Override
        public void handleMessage(Message msg) {
            printMsg(msg);
            switch(msg.what){
                case MSG_INIT_PROGRESS_BAR:
                    mProgress = 0;
                    break;
                case MSG_INCREASE_PROGRESS_BAR:
                    mProgress = mProgress + 10;
                    break;
                default:
                    Log.d(TAG, "no match message");
            }
            mProgressBar.setProgress(mProgress);
        }
    };
    // 特約工人的經紀人
    private HandlerThread mHandlerThread;
    // 特約工人
    private Handler mThreadHandler;
    private Handler mUiHandler;
    private Runnable r1 = new Runnable() {
        @Override
        public void run() {
            mUiHandler.post(r2);
        }
    };
    // wrong: can't update UI in thread
    private Runnable r2 = new Runnable() {
        @Override
        public void run() {
            while (mProgress < 100) {
                mProgress = mProgress + 10;
                mProgressBar.setProgress(mProgress);
                try {
                    Thread.sleep(1000);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    };
    private void printMsg(Message msg) {
        int arg1 = msg.arg1;
        int arg2 = msg.arg2;
        int what = msg.what;
        Object obj = msg.obj;
        Object result = msg.obj;
        Log.d(TAG, " arg1: " + arg1);
        Log.d(TAG, " arg2: " + arg2);
        Log.d(TAG, " what: " + what);
        Log.d(TAG, " obj: " + obj);
        Log.d(TAG, " result: " + result);
        Log.d(TAG, "----------------------");
        Bundle bundle = msg.getData();
        Log.d(TAG, " bundle: " + bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);
        mHandlerThread = new HandlerThread("name");
        mHandlerThread.start();
        mThreadHandler = new Handler(mHandlerThread.getLooper());
        mUiHandler = new Handler();
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mProgressBar.setProgress(mProgress);
        mStartButton = (Button) findViewById(R.id.startbutton);
        mFirstButton = (Button) findViewById(R.id.firstButton);
        mSecondButton = (Button) findViewById(R.id.secondButton);
        mThirdButton = (Button) findViewById(R.id.thirdButton);
        mInitButton = (Button) findViewById(R.id.initbutton);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (mProgress<100) {
                            try {
                                // 5) sendMessage(Message)
                                Message msg = Message.obtain(mHandle, MSG_INCREASE_PROGRESS_BAR);
                                msg.obj = "String Object";
                                mHandle.sendMessage(msg);
                                Thread.sleep(1000);
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });

        mFirstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (mProgress<100) {
                            try {
                                // 4) sendEmptyMessage(int)
                                mHandle.sendEmptyMessage(MSG_INCREASE_PROGRESS_BAR);
                                Thread.sleep(1000);
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });

        mSecondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (mProgress<100) {
                            try {
                                // 7) sendMessageDelayed(Message, long)
                                Message msg = Message.obtain();
                                msg.what = MSG_INCREASE_PROGRESS_BAR;
                                mHandle.sendMessageDelayed(msg, 1000); // delay in message queue
//                                Thread.sleep(1000); // delay in main thread
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });

        mThirdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadHandler.post(r1);
            }
        });

        mInitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandle.sendMessage(Message.obtain(mHandle, MSG_INIT_PROGRESS_BAR));
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
