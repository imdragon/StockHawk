package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.TaskParams;


public class StockIntentService extends IntentService {
Handler toastHandler;
    public StockIntentService() {
        super(StockIntentService.class.getName());
    }

    public StockIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();
        if (intent.getStringExtra("tag").equals("add")) {
            args.putString("symbol", intent.getStringExtra("symbol"));
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
//        stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));

        // check to see if we get a failer so we can show a toast
        // idea from niteshgarg_06 on udacity forums
        if (stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args)) ==
        GcmNetworkManager.RESULT_FAILURE){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Stock does not exist!", Toast.LENGTH_LONG).show();
                }
            });
        };
    }
}
