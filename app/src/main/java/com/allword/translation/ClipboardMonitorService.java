package com.allword.translation;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Date;

/**
 * Monitors the {@link ClipboardManager} for changes and logs the text to a file.
 */
public class ClipboardMonitorService extends Service {
    public static final String TAG = "123321";
    public static String Text = "";
    public static String Text2 = "";

    private File mHistoryFile;
    private ExecutorService mThreadPool = Executors.newSingleThreadExecutor();
    private ClipboardManager mClipboardManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO: Show an ongoing notification when this service is running.
        mClipboardManager =
                (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(
                mOnPrimaryClipChangedListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mClipboardManager != null) {
            mClipboardManager.removePrimaryClipChangedListener(
                    mOnPrimaryClipChangedListener);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    Log.d(TAG, "onPrimaryClipChanged");
                    ClipData clip = mClipboardManager.getPrimaryClip();
                    mThreadPool.execute(new WriteHistoryRunnable(
                            clip.getItemAt(0).getText()));
                }
            };

    private class WriteHistoryRunnable implements Runnable {
        private final Date mNow;
        private CharSequence mTextToWrite;

        public WriteHistoryRunnable(CharSequence text) {
            mNow = new Date(System.currentTimeMillis());
            mTextToWrite = text;
        }

        @Override
        public void run() {
            if (TextUtils.isEmpty(mTextToWrite)) {
                // Don't write empty text to the file
            }
                else
                {mTextToWrite=mTextToWrite+"";
                    Text= (String) mTextToWrite+"";
                    Log.i(TAG, "Writing new clip to history:");
                    Intent intent = new Intent(getBaseContext(), DialogActivity.class);
                  intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                  intent.putExtra("text",mTextToWrite+"");
                  startActivity(intent);
                    Log.i(TAG, mTextToWrite.toString());

                }
            }
        }
    }
