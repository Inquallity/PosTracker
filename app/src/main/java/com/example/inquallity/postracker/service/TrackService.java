package com.example.inquallity.postracker.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class TrackService extends Service{

    private static final String TAG = "TAG_OF_SERVICE";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service has been started");
        Toast.makeText(getApplicationContext(), "Service has been started", Toast.LENGTH_LONG).show();

    }

    //Обработка некорректного завершения сервиса
    //Чаще всего используют, чтобы создать и запустить отдельный поток для сервиса
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG", "On starting...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "I am a service and i doing that shit into Log.d");
        /*stopSelf(startId);*/

            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service has been stopped");
        Toast.makeText(getApplicationContext(), "Service has been stopped", Toast.LENGTH_LONG).show();

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
