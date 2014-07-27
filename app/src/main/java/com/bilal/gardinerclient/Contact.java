package com.bilal.gardinerclient;

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
