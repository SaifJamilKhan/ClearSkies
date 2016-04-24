package com.skynet.skynet.skynet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mohamed on 2016-04-23.
 */
public class Airport {

    public String iata;
    public String iso;
    public String continent;
    public String name;
    public String size;
    public double lat;
    public double lon;
    public int sizeLevel; // 0 = small, 1 = medium, 2 = large
    public int radius;


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
                    this.sizeLevel = 0;
                    this.radius = 5630; // 3.5 miles
                    break;
                case "medium":
                    this.sizeLevel = 1;
                    this.radius = 8046; // 6.5 miles
                    break;
                case "large":
                    this.sizeLevel = 2;
                    this.radius = 11000; // 8.5 miles
                    break;
                default:
                    this.sizeLevel = 0;
                    this.radius = 5630; // 3.5 miles
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
