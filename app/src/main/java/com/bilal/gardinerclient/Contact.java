package com.bilal.gardinerclient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bilal on 27/07/14.
 */
public class Contact {
    private String name;
    private String id;

    public Contact(String id, String name) {
        this.name = name;
        this.id = id;
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

    @Override
    public String toString() {
        return name;
    }
}
