package com.bilal.gardinerclient;

import org.json.JSONObject;

import java.util.concurrent.Callable;

/**
 * Created by bilal on 03/08/14.
 *
 * Hacky class to get JavaScript-like asynchronous callbacks
 *
 */
public abstract class OnNetworkDone implements Callable<Void> {
    protected JSONObject response;

    public JSONObject getResponse() {
        return response;
    }

    public OnNetworkDone setResponse(JSONObject response) {
        this.response = response;
        return this;
    }
}
