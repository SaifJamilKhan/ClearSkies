package com.skynet.skynet.skynet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by saifkhan on 2016-04-24.
 */
public class MapAirplane {

    public double lat;
    public double lon;

    public String originLabel;
    public int heading;

    public int altitude;
    public double timestamp;

    public int radius = 2000; // Arbitrary


    public MapAirplane(JSONObject jsonObject) {
        try {
            this.lat = jsonObject.getJSONArray("coord").getDouble(1);
            this.lon = jsonObject.getJSONArray("coord").getDouble(0);

            this.altitude = Integer.valueOf(jsonObject.getString("altitude"));
            this.heading = Integer.valueOf(jsonObject.getString("heading"));
            this.timestamp = Double.valueOf(jsonObject.getString("timestamp"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
