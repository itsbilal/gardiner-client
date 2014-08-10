package com.bilal.gardinerclient;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by bilal on 10/08/14.
 */
public class Location implements Comparable<Location> {
    private String id;
    private Double latX;
    private Double latY;
    private Date posted;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public Location(String id, Double latX, Double latY, Date posted) {
        this.id = id;
        this.latX = latX;
        this.latY = latY;
        this.posted = posted;
    }

    public Location(JSONObject response) throws JSONException {
        this.id = response.getString("id");
        this.latX = response.getDouble("latX");
        this.latY = response.getDouble("latY");

        try {
            this.posted = dateFormat.parse(response.getString("posted"));
        } catch (ParseException e) {
            this.posted = new Date();
        }
    }

    public String getId() {
        return id;
    }

    public Double getLatX() {
        return latX;
    }

    public Double getLatY() {
        return latY;
    }

    public Date getPosted() {
        return posted;
    }

    @Override
    public int compareTo(Location location) {
        if (location.getPosted().after(this.posted)) {
            return 1;
        } else if (location.getPosted().before(this.posted)) {
            return -1;
        }

        return 0;
    }
}
