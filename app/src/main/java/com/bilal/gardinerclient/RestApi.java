package com.bilal.gardinerclient;

import android.app.Activity;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by bilal on 20/07/14.
 */
public class RestApi {

    // Constants
    public static final String BASE_URL="http://10.0.2.2:8080/";
    public static final String CHARSET="UTF-8";

    public static final Integer HTTP_GET=1;
    public static final Integer HTTP_POST=2;

    private boolean authenticated = false;
    private String token = null;

    // Classes
    private class doWork extends AsyncTask<Object, Void, JSONObject>{
        private NetworkActivity rootActivity;

        public doWork(NetworkActivity m_rootActivity) {
            rootActivity = m_rootActivity;
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            String endpoint = (String)params[0];
            Integer method = (Integer)params[1];
            String requestBody = "";

            if (method == HTTP_POST) {
                requestBody = (String)params[2];
            } else {
                endpoint += "?" + (String) params[2];
            }

            try {
                URL url = new URL(BASE_URL + endpoint);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                if (authenticated) {
                    connection.addRequestProperty("X-WWW-Authenticate", token);
                }

                if (method == HTTP_POST) {
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset="+CHARSET);

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(requestBody.getBytes(CHARSET));
                }

                // Fire the connection
                connection.connect();

                InputStream is = connection.getInputStream();
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null) {
                    responseStrBuilder.append(inputStr);
                }

                JSONObject responseJson = new JSONObject(responseStrBuilder.toString());

                return responseJson;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);

            rootActivity.onNetworkCallResponse(1, response);
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

    public void login(NetworkActivity m_activity, String email, String password) {
        try {
            String query = String.format("email=%s&password=%s",
                    URLEncoder.encode(email, CHARSET),
                    URLEncoder.encode(password, CHARSET));

            new doWork(m_activity).execute("user/login", HTTP_POST, query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
