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
    private Type type;

    public enum Type {
        REQUEST,
        REQUESTED,
        FRIEND,
        STANGER
    }

    public Contact(String id, String name) { // Avoid using this constructor
        this.name = name;
        this.id = id;
        this.requestId = null;

        this.type = Type.STANGER;
    }

    public Contact(String id, String name, String requestId) {
        this.name = name;
        this.id = id;
        this.requestId = requestId;

        this.type = Type.REQUEST;
    }

    public String getRequestId() {
        return requestId;
    }

    public Contact (JSONObject object) throws JSONException { // This function is only used in searches
        this.name = object.getString("name");
        this.id = object.getString("id");

        if (object.has("isFriend") && object.getInt("isFriend") == 1) {
            this.type = Type.FRIEND;
        } else if (object.has("request")) {
            if (object.getString("request").equals("1")) {
                this.type = Type.REQUESTED;
                this.requestId = null;
            } else {
                this.type = Type.REQUEST;
                this.requestId = object.getString("request");
            }
        } else {
            this.type = Type.STANGER;
            this.requestId = null;
        }
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
        this.type = Type.REQUEST;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
