package com.example.inquallity.postracker.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.example.inquallity.postracker.R;
import com.example.inquallity.postracker.fragment.TrackCollect;
import com.example.inquallity.postracker.sqlite.TrackSql;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends Activity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient mGoogleApi;
    private LocationRequest mRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.frame, new TrackCollect())
                    .commit();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApi == null) {
            mGoogleApi = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mRequest = LocationRequest.create();
        mRequest.setInterval(5000);
        mRequest.setFastestInterval(1000);
        mGoogleApi.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApi != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApi, this);
            mGoogleApi.disconnect();

        }
    }

    private void retryConnecting() {
        if (!mGoogleApi.isConnecting()) {
            mGoogleApi.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("TAG", "GoogleApi connected");
        final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApi);
        if (location != null) {
            onLocationChanged(location);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApi, mRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        retryConnecting();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        retryConnecting();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("TAG", location.toString());
        ContentValues cv = new ContentValues();
        cv.put("position", "ROSTOV");
        cv.put("latitude", location.getLatitude());
        cv.put("longitude", location.getLongitude());
        getContentResolver().insert(TrackSql.URI, cv);
    }
}
