package com.skynet.skynet.skynet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by mohamed on 2016-04-23.
 */
@SuppressWarnings("serial")
public class WeatherData implements Serializable {

    public double lat;
    public double lon;
    public double temp;
    public double pressure;
    public double humidity;
    public double windSpeed;
    public double windDirectionDegree;
    public String windDirectionCardinal;
    public double visibility;
    public double precipProbability;

    public WeatherData(JSONObject jsonObject) {
        try {
            this.lat = jsonObject.getDouble("latitude");
            this.lon = jsonObject.getDouble("longitude");
            this.temp = jsonObject.getJSONObject("currently").getDouble("temperature");
            this.pressure = jsonObject.getJSONObject("currently").getDouble("pressure");
            this.humidity = jsonObject.getJSONObject("currently").getDouble("humidity");
            this.windSpeed = jsonObject.getJSONObject("currently").getDouble("windSpeed");
            this.windDirectionDegree = jsonObject.getJSONObject("currently").getDouble("windBearing");
            this.windDirectionCardinal = degToCardinal(this.windDirectionDegree);
            this.visibility = jsonObject.getJSONObject("currently").getDouble("visibility");
            this.precipProbability = jsonObject.getJSONObject("currently").getDouble("precipProbability");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String degToCardinal(double x) {
        String directions[] = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return directions[ (int)Math.round(( (x % 360) / 45)) % 8 ];
    }
}
