package com.bilal.gardinerclient;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Location implements Comparable<Location>, Parcelable {
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

    public Location(Double latX, Double latY) {
        this.id = null;
        this.latX = latX;
        this.latY = latY;
        this.posted = null;
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
        return this.posted.compareTo(location.getPosted());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Location> CREATOR
            = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    private Location(Parcel parcel) {
        this.id = parcel.readString();
        this.latX = parcel.readDouble();
        this.latY = parcel.readDouble();
        this.posted = new Date(parcel.readLong());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeDouble(this.latX);
        parcel.writeDouble(this.latY);
        parcel.writeLong(this.posted.getTime());
    }

    public void onPost(String id) {
        this.id = id;
        this.posted = new Date();
    }
}
