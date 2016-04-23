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
    public double windDirection;

    public WeatherData(JSONObject jsonObject) {
        try {
            this.lat = jsonObject.getJSONObject("coord").getDouble("lat");
            this.lon = jsonObject.getJSONObject("coord").getDouble("lon");
            this.temp = jsonObject.getJSONObject("main").getDouble("temp");
            this.pressure = jsonObject.getJSONObject("main").getDouble("pressure");
            this.humidity = jsonObject.getJSONObject("main").getDouble("humidity");
            this.windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
            this.windDirection = jsonObject.getJSONObject("wind").getDouble("deg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
