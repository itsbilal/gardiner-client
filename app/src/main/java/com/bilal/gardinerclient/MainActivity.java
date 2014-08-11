package com.bilal.gardinerclient;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bilal.gardinerclient.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity implements NetworkActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        ListView listView = getListView();
        listView.setBackgroundColor(getResources().getColor(R.color.mainactivity_background));
        listView.setDivider(null);
        listView.setDividerHeight(15);
        listView.setPadding(15,15,15,15);

        SharedPreferences preferences = getSharedPreferences(LoginActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        RestApi restApi = RestApi.getInstance();
        if (preferences.contains("email") && preferences.contains("password") && !restApi.getLoggedIn()) {
            // Try a login

            restApi.login(this, preferences.getString("email", null), preferences.getString("password", null));
        } else if (!preferences.contains("email")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Get back to this activity later
        } else if (restApi.getLoggedIn()) {
            refreshLocationsList();
        }
    }

    private void refreshLocationsList() {
        RestApi restApi = RestApi.getInstance();

        restApi.getLocations(this);
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

            refreshLocationsList();
        } else if (networkCall == RestApi.Endpoint.LOCATIONS_HOME) {
            List<Friend> friends = new ArrayList<Friend>();

            try {
                if (responseData.has("users") && responseData.get("users") instanceof JSONArray) {
                    JSONArray users = responseData.getJSONArray("users");
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);

                        Friend friend = new Friend(user);
                        friends.add(friend);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ListAdapter adapter = new HomeListAdapter(this, R.layout.listitem_home_list, friends);
            setListAdapter(adapter);
        }
    }
}
