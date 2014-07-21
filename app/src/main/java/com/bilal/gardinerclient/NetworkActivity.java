package com.bilal.gardinerclient;

import org.json.JSONObject;

/**
 * Created by bilal on 20/07/14.
 */
public interface NetworkActivity {
    void onNetworkCallResponse(int networkCall, JSONObject responseData);
}
