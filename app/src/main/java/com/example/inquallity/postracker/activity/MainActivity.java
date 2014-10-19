package com.example.inquallity.postracker.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.inquallity.postracker.R;
import com.example.inquallity.postracker.fragment.TrackCollect;
import com.example.inquallity.postracker.service.TrackService;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        startService(new Intent(getApplicationContext(), TrackService.class));
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.frame, new TrackCollect())
                    .commit();
        }
        startService(new Intent(getApplicationContext(), TrackService.class));
    }

 }

