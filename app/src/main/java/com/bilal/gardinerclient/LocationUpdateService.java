package com.bilal.gardinerclient;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import org.json.JSONObject;

public class LocationUpdateService extends Service implements NetworkActivity, GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener
{

    private static final Boolean MOCK_LOCATIONS = true;
    private LocationClient locationClient;

    @Override
    public void onCreate() {
        // Do we need to do anything here?
        super.onCreate();

        Integer response = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (response != ConnectionResult.SUCCESS) {
            Log.d("LocationUpdateService", "Google Play services unavailable");
            stopSelf();
            return;
        }

        locationClient = new LocationClient(this, this, this);
    }

    private void updateLocation(Location location) {
        RestApi api = RestApi.getInstance();

        if (!api.getLoggedIn()) {
            stopSelf();
        } else {
            api.postLocation(this, location, new OnNetworkDone() {
                @Override
                public Void call() throws Exception {
                    Log.d("LocationUpdateService", "Location successfully updated");
                    stopSelf();

                    return null;
                }
            });
        }
    }

    private void updateLocation(android.location.Location androidLocation) {
        if (androidLocation == null) {
            stopSelf();
            return;
        }

        Location location = new Location(androidLocation.getLatitude(), androidLocation.getLongitude());

        updateLocation(location);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Location location = intent.getParcelableExtra("location");
        if (location == null) {

            if (locationClient.isConnected()) {

                android.location.Location androidLocation = locationClient.getLastLocation();
                updateLocation(androidLocation);
            } else {
                locationClient.connect();
            }
        } else {
            updateLocation(location);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        locationClient.disconnect();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onNetworkCallResponse(RestApi.Endpoint networkCall, JSONObject responseData) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (MOCK_LOCATIONS) {
            locationClient.setMockMode(true);
            locationClient.setMockLocation(getMockLocation());
        }

        android.location.Location location = locationClient.getLastLocation();

        updateLocation(location);
    }

    private android.location.Location getMockLocation() {
        // Create a new Location
        android.location.Location newLocation = new android.location.Location("flp");
        newLocation.setLatitude(110);
        newLocation.setLongitude(150);
        newLocation.setAccuracy(3.0f);
        newLocation.setTime(System.currentTimeMillis());
        newLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        return newLocation;
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Do nothing
        stopSelf();
    }

}
