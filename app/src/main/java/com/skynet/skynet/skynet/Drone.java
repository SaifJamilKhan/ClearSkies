package com.skynet.skynet.skynet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by saifkhan on 2016-04-23.
 */
public class Drone {

    public double lat;
    public double lon;
    public int sizeLevel;

    public Drone(JSONObject jsonObject) {
        try {
            this.lat = jsonObject.getDouble("lat");
            this.lon = jsonObject.getDouble("lon");
            this.sizeLevel = jsonObject.getInt("size");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
