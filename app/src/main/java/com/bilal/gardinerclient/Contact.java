package com.bilal.gardinerclient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bilal on 27/07/14.
 */
public class Contact {
    private String name;
    private String id;
    private String requestId = null;

    public enum Type {
        REQUEST,
        FRIEND,
        STANGER
    }

    public Contact(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public Contact(String id, String name, String requestId) {
        this.name = name;
        this.id = id;
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public Contact (JSONObject object) throws JSONException {
        this.name = object.getString("name");
        this.id = object.getString("id");
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void sendRequest(NetworkActivity m_activity) {
        RestApi api = RestApi.getInstance();

        api.sendContactRequest(m_activity, this);
    }

    public void sendRequest(NetworkActivity m_activity, OnNetworkDone onNetworkDone) {
        RestApi api = RestApi.getInstance();

        api.sendContactRequest(m_activity, this, onNetworkDone);
    }

    public void acceptRequest(NetworkActivity context) {
        RestApi api = RestApi.getInstance();

        api.sendRequestReply(context, requestId, 1);
    }

    public void acceptRequest(NetworkActivity context, OnNetworkDone onDone) {
        RestApi api = RestApi.getInstance();

        api.sendRequestReply(context, requestId, 1, onDone);
    }

    @Override
    public String toString() {
        return name;
    }
}
