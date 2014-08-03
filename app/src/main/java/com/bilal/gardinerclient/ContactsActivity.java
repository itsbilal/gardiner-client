package com.bilal.gardinerclient;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.bilal.gardinerclient.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends ListActivity implements NetworkActivity {

    public static int TAB_FRIENDS=1;
    public static int TAB_REQUESTS=50;

    private ContactsListAdapter friendsAdapter;
    private ContactsListAdapter requestsAdapter;

    public class TabListener implements ActionBar.TabListener {
        private int mTabType;

        public TabListener(Integer tabType) {
            mTabType = tabType;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            if (mTabType == TAB_FRIENDS) {
                Log.d("ContactsActivity", "Friends tab selected");

                ContactsActivity.this.setListAdapter(friendsAdapter);
            } else {
                Log.d("ContactsActivity", "Requests tab selected");
                ContactsActivity.this.setListAdapter(requestsAdapter);
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RestApi restApi = RestApi.getInstance();

        friendsAdapter = new ContactsListAdapter(this, R.layout.listitem_contacts_friend);
        requestsAdapter = new ContactsListAdapter(this, R.layout.listitem_contacts_friend);

        // Load stuff into adapters
        restApi.doContactSearch(this, null, null, null);
        restApi.getContactRequests(this);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        ActionBar.Tab friendsTab = actionBar.newTab()
                .setText(R.string.tab_friends_text)
                .setTabListener(new TabListener(TAB_FRIENDS))
                .setTag(TAB_FRIENDS);
        actionBar.addTab(friendsTab);

        ActionBar.Tab requestsTab = actionBar.newTab()
                .setText(R.string.tab_requests_text)
                .setTabListener(new TabListener(TAB_REQUESTS));
        actionBar.addTab(requestsTab);
    }

    @Override
    public void onNetworkCallResponse(RestApi.Endpoint networkCall, JSONObject responseData) {
        try {
            if (networkCall == RestApi.Endpoint.CONTACTS_SEARCH) {

                JSONArray users = responseData.getJSONArray("users");
                List<Contact> contactsList = new ArrayList<Contact>();

                for (int i=0;i<users.length();i++) {
                    contactsList.add(new Contact(users.getJSONObject(i)));
                }

                friendsAdapter.clear();
                friendsAdapter.addAll(contactsList);
                friendsAdapter.notifyDataSetChanged();

            } else if (networkCall == RestApi.Endpoint.CONTACTS_REQUESTS) {
                JSONArray requests = responseData.getJSONArray("requests");
                List<Contact> requestsList = new ArrayList<Contact>();

                for (int i=0;i<requests.length();i++) {
                    JSONObject request = requests.getJSONObject(i);
                    JSONObject sender = request.getJSONObject("from");
                    Contact contact = new Contact(sender);
                    contact.setRequestId(request.getString("id"));

                    requestsList.add(contact);
                }

                requestsAdapter.clear();
                requestsAdapter.addAll(requestsList);
                requestsAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contacts, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
