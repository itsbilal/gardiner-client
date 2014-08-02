package com.bilal.gardinerclient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by bilal on 20/07/14.
 */
class RestApi {

    // Constants
    public static final String BASE_URL="http://10.0.2.2:8080/";
    public static final String CHARSET="UTF-8";

    public static enum Method {
        HTTP_GET,
        HTTP_POST
    }

    public static enum Endpoint {
        USER_LOGIN,
        USER_RELOGIN,
        CONTACTS_SEARCH,
        CONTACTS_REQUEST_SEND, CONTACTS_REQUESTS
    }

    private boolean authenticated = false;
    private String token = null;

    public void setLoggedIn(String token) {
        this.token = token;
        this.authenticated = true;
    }

    public void setLoggedOut() {
        this.token = null;
        this.authenticated = false;
    }

    public boolean getLoggedIn() {
        return authenticated;
    }

    private class Request {
        private Endpoint endpoint;
        private String url;
        private Map<String, String> query;
        private Method method;

        public Request(Endpoint m_endpoint, String m_url, Map<String, String> m_query, Method m_method) {
            endpoint = m_endpoint;
            url = m_url;
            query = m_query;
            method = m_method;
        }

        public Request(Endpoint m_endpoint, String m_url, Method m_method) {
            endpoint = m_endpoint;
            url = m_url;
            query = null;
            method = null;
        }

        public Endpoint getEndpoint() {
            return endpoint;
        }

        public String getUrl() {
            return url;
        }

        public Map<String, String> getQuery() {
            return query;
        }

        public Method getMethod() {
            return method;
        }
    }

    // Classes
    private class doWork extends AsyncTask<Object, Void, JSONObject>{
        private NetworkActivity rootActivity;
        private Endpoint endpoint;
        private Request originalRequest;

        public doWork(NetworkActivity m_rootActivity, Endpoint m_endpoint) {
            rootActivity = m_rootActivity;
            endpoint = m_endpoint;
            originalRequest = null;
        }

        private doWork(NetworkActivity m_rootActivity, Endpoint m_endpoint, Request m_originalRequest) {
            rootActivity = m_rootActivity;
            endpoint = m_endpoint;
            originalRequest = m_originalRequest;
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            Request request = (Request) params[0];
            Map requestMap = null;
            HttpURLConnection connection = null;
            String endpoint = request.getUrl();

            try {
                if (request.getMethod() == Method.HTTP_POST) {
                    requestMap = request.getQuery();
                } else if (request.getMethod() == Method.HTTP_GET) {
                    endpoint += "?" + processQueryString(request.getQuery());
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                requestMap = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                URL url = new URL(BASE_URL + endpoint);

                connection = (HttpURLConnection) url.openConnection();

                if (authenticated) {
                    connection.addRequestProperty("X-WWW-Authenticate", token);
                }

                if (request.getMethod() == Method.HTTP_POST) {
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + CHARSET);

                    if (requestMap != null) {
                        OutputStream outputStream = connection.getOutputStream();
                        outputStream.write(processQueryString(requestMap).getBytes(CHARSET));
                    }
                }

                // Fire the connection
                InputStream is = null;
                try {
                    is = connection.getInputStream();
                } catch (FileNotFoundException e) {
                    Log.e("RestApi", "Response code: " + connection.getResponseCode());
                    originalRequest = request;
                    is = connection.getErrorStream();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null) {
                    responseStrBuilder.append(inputStr);
                }

                // DEbug output
                Log.d("RestApi", responseStrBuilder.toString());

                JSONObject responseJson = new JSONObject(responseStrBuilder.toString());

                return responseJson;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);

            if (endpoint == Endpoint.USER_RELOGIN) {
                // Store session var
                try {
                    RestApi.this.setLoggedIn(response.getString("token"));
                    Log.d("RestApi", "Successful relogin: " + response.getString("token"));

                    new doWork(rootActivity, originalRequest.getEndpoint()).execute(originalRequest);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();

                    // Give up
                    rootActivity.onNetworkCallResponse(originalRequest.getEndpoint(), response);
                }
            }

            try {
                if (response.has("error") && response.has("code") && response.getInt("code") == 1000) {
                    RestApi.this.setLoggedOut();

                    SharedPreferences prefs = ((Activity) rootActivity).getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    HashMap<String, String> requestMap = new HashMap<String, String>();
                    requestMap.put("email", prefs.getString("email", null));
                    requestMap.put("password", prefs.getString("password", null));

                    // Generate request
                    Request request = new Request(Endpoint.USER_RELOGIN, "user/login", requestMap, Method.HTTP_POST);

                    new doWork(rootActivity, Endpoint.USER_RELOGIN, originalRequest).execute(request);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            rootActivity.onNetworkCallResponse(endpoint, response);
        }
    };

    // Instance
    private static RestApi instance = null;

    protected RestApi() {

    }

    public static RestApi getInstance() {
        if (instance == null) {
            instance = new RestApi();
        }

        return instance;
    }

    private static String processQueryString(Map<String, String> map) {
        Iterator it = map.entrySet().iterator();
        StringBuilder builder = new StringBuilder();

        try {
            while(it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                it.remove();
                builder.append(pairs.getKey() + "=" + URLEncoder.encode(pairs.getValue().toString(), CHARSET));
                if (it.hasNext()) {
                    builder.append('&');
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    public void login(NetworkActivity m_activity, String email, String password) {
        HashMap<String, String> requestMap = new HashMap<String, String>();

        requestMap.put("email", email);
        requestMap.put("password", password);

        new doWork(m_activity, Endpoint.USER_LOGIN).execute(new Request(Endpoint.USER_LOGIN,
                "user/login",
                requestMap,
                Method.HTTP_POST));
    }

    public void doContactSearch(NetworkActivity m_activity, String email, String phone, String location) {
        HashMap<String, String> requestMap = new HashMap<String, String>();

        if (email != null) {
            requestMap.put("email", email);
        }
        if (phone != null) {
            requestMap.put("phone", phone);
        }
        if (location != null) {
            requestMap.put("location", location);
        }

        new doWork(m_activity, Endpoint.CONTACTS_SEARCH).execute(new Request(Endpoint.CONTACTS_SEARCH, "contacts/search", requestMap, Method.HTTP_GET));
    }

    public void getContactRequests(NetworkActivity m_activity) {
        new doWork(m_activity, Endpoint.CONTACTS_REQUESTS).execute(new Request(Endpoint.CONTACTS_REQUESTS, "contacts/requests", Method.HTTP_GET));
    }

    public void sendContactRequest(NetworkActivity m_activity, Contact contact) {
        new doWork(m_activity, Endpoint.CONTACTS_REQUEST_SEND).execute(new Request(Endpoint.CONTACTS_REQUEST_SEND,"contacts/user/" + contact.getId() + "/request",
                Method.HTTP_POST));
    }

}
