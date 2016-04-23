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
    public int sizeLevel; // 0 = small, 1 = medium, 2 = large


    public Airport(JSONObject jsonObject) {
        try {
            this.lat = jsonObject.getDouble("lat");
            this.lon = jsonObject.getDouble("lon");
            this.iata = jsonObject.getString("iata");
            this.iso = jsonObject.getString("iso");
            this.continent = jsonObject.getString("continent");
            this.name = jsonObject.getString("name");
            this.size = jsonObject.getString("size");
            switch (size) {
                case "small":
                    sizeLevel = 0;
                    break;
                case "medium":
                    sizeLevel = 1;
                    break;
                case "large":
                    sizeLevel = 2;
                    break;
                default:
                    sizeLevel = 0;
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
