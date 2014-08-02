package com.bilal.gardinerclient;

import org.json.JSONObject;

/**
 * Created by bilal on 20/07/14.
 */
interface NetworkActivity {
    void onNetworkCallResponse(RestApi.Endpoint networkCall, JSONObject responseData);
}
