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

    public WeatherData(JSONObject jsonObject) {
        try {
            this.lat = jsonObject.getJSONObject("coord").getDouble("lat");
            this.lon = jsonObject.getJSONObject("coord").getDouble("lon");
            this.temp = jsonObject.getJSONObject("main").getDouble("temp");
            this.pressure = jsonObject.getJSONObject("main").getDouble("pressure");
            this.humidity = jsonObject.getJSONObject("main").getDouble("humidity");
            this.windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
            this.windDirectionDegree = jsonObject.getJSONObject("wind").getDouble("deg");
            this.windDirectionCardinal = degToCardinal(this.windDirectionDegree);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String degToCardinal(double x) {
        String directions[] = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return directions[ (int)Math.round(( (x % 360) / 45)) % 8 ];
    }
}
