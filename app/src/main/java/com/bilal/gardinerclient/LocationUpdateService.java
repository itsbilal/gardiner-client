package com.bilal.gardinerclient;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

public class LocationUpdateService extends Service implements NetworkActivity {

    @Override
    public void onCreate() {
        // Do we need to do anything here?
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Location location = intent.getParcelableExtra("location");
        if (location == null) {

        }

        RestApi api = RestApi.getInstance();

        if (!api.getLoggedIn()) {
            stopSelf();
        } else {
            api.postLocation(this, location, new OnNetworkDone() {
                @Override
                public Void call() throws Exception {
                    stopSelf(startId);

                    return null;
                }
            });
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onNetworkCallResponse(RestApi.Endpoint networkCall, JSONObject responseData) {

    }

    /*@Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences preferences = getSharedPreferences(LoginActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        RestApi api = RestApi.getInstance();

        if (!preferences.contains("email") || !preferences.contains("password") || !api.getLoggedIn()) {
            Log.d("LocationUpdateService", "API is not logged in, screw it.");
            return;
        }
    }*/
}
