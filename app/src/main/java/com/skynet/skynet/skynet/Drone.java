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
    public int radius;

    public Drone(JSONObject jsonObject) {
        try {
            this.lat = jsonObject.getDouble("lat");
            this.lon = jsonObject.getDouble("lon");
            this.sizeLevel = jsonObject.getInt("size");
            switch (this.sizeLevel) {
                case 0:
                    this.radius = 5630; // 3.5 miles
                    break;
                case 1:
                    this.radius = 8046; // 6.5 miles
                    break;
                case 2:
                    this.radius = 11000; // 8.5 miles
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
