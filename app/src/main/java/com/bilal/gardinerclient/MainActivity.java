package com.bilal.gardinerclient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.bilal.gardinerclient.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements NetworkActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences(LoginActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        RestApi restApi = RestApi.getInstance();
        if (preferences.contains("email") && preferences.contains("password") && !restApi.getLoggedIn()) {
            // Try a login

            restApi.login(this, preferences.getString("email", null), preferences.getString("password", null));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_contacts) {
            Intent startContacts = new Intent(this, ContactsActivity.class);
            startActivity(startContacts);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNetworkCallResponse(RestApi.Endpoint networkCall, JSONObject responseData) {
        if (networkCall == RestApi.Endpoint.USER_LOGIN) {
            try {
                RestApi.getInstance().setLoggedIn(responseData.getString("token"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
