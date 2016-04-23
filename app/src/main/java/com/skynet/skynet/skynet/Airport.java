package com.skynet.skynet.skynet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mohamed on 2016-04-23.
 */
public class Airport {

    public double lat;
    public double lon;
    public String iata;
    public String iso;
    public String continent;
    public String name;
    public String size;


    public Airport(JSONObject jsonObject) {
        try {
            this.lat = jsonObject.getDouble("lat");
            this.lon = jsonObject.getDouble("lon");
            this.iata = jsonObject.getString("iata");
            this.iso = jsonObject.getString("iso");
            this.continent = jsonObject.getString("continent");
            this.name = jsonObject.getString("name");
            this.size = jsonObject.getString("size");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
