package com.bilal.gardinerclient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bilal on 10/08/14.
 */
public class Friend extends Contact {

    private List<Location> locations = new ArrayList<Location>();

    public Friend(JSONObject object) throws JSONException {
        super(object); // Sets nearly everything
        setType(Type.FRIEND);

        if (object.has("locations") && object.get("locations") instanceof JSONArray) {
            JSONArray locationsArray = object.getJSONArray("locations");

            for (int i=0;i<locationsArray.length();i++) {
                JSONObject currentLocation = locationsArray.getJSONObject(i);

                locations.add(new Location(currentLocation));
            }
        }
    }

    public List<Location> getLocations() {
        Collections.sort(locations);
        Collections.reverse(locations);
        return locations;
    }

}
