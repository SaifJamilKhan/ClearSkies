package com.skynet.skynet.skynet;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends AppCompatActivity implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Context context;
    private WeatherData weatherData;
    private ArrayList<Airport> airports;

    @Bind(R.id.my_toolbar)
    public Toolbar myToolbar;

    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private Circle mCircle;

    private ArrayList<Circle> circlesAdded = new ArrayList<>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);
        myToolbar.setTitle("Hermes");
        setSupportActionBar(myToolbar);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

        context = this;


        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                try {
                    LatLng botleft = mMap.getProjection()
                            .getVisibleRegion().nearLeft;

                    LatLng botright = mMap.getProjection()
                            .getVisibleRegion().nearRight;

                    LatLng topleft = mMap.getProjection()
                            .getVisibleRegion().farLeft;

                    LatLng topright = mMap.getProjection()
                            .getVisibleRegion().farRight;

                    Log.v("saif", "camera change event called");
                    callCustomAPI(getCustomURL(botleft, botright, topleft, topright));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.detailsButton)
    public void detailsButtonPressed() {
        if(weatherData != null) {
            Intent intent = new Intent(context, DroneDetailsActivity.class);
            intent.putExtra("weatherData", weatherData);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Location and weather not yet available", Toast.LENGTH_SHORT).show();;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @OnClick(R.id.btn_map_toggle)
    public void mapTypeToggle() {
        if(mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
//        logCornerLatsAndLongs();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void logCornerLatsAndLongs() {
        LatLng botleft = mMap.getProjection()
                .getVisibleRegion().nearLeft;

        LatLng botright = mMap.getProjection()
                .getVisibleRegion().nearRight;

        LatLng topleft = mMap.getProjection()
                .getVisibleRegion().farLeft;

        LatLng topright = mMap.getProjection()
                .getVisibleRegion().farRight;

    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
    }

    private void createCircleAroundPoint(LatLng latlng) {
        if(mCircle == null) {
            mCircle = mMap.addCircle(new CircleOptions()
                    .center(latlng)
                    .radius(4000) // this is in meters
                    .strokeColor(Color.RED)
                    .fillColor(0x73DB5E5E));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        createCircleAroundPoint(latLng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
        createCircleAroundPoint(latLng);
        try {
            callWeatherAPI(getWeatherURL(latLng.latitude, latLng.longitude));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
//        mMap.animateCamera(cameraUpdate);
//        locationManager.removeUpdates(this);
//    }

    private String getWeatherURL(double lat, double lon) {
        return "http://api.openweathermap.org/data/2.5/weather?lat=" + Double.toString(lat) + "&lon="
                + Double.toString(lon) + "&APPID=7a8668b5c3f71c0608b503bdd446c3c1";
    }

    private final OkHttpClient client = new OkHttpClient();

    private void callWeatherAPI(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {
                    weatherData = new WeatherData(new JSONObject(response.body().string()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getCustomURL(LatLng botLeft, LatLng botRight, LatLng topLeft, LatLng topRight) {
        return "https://skynet-server.herokuapp.com/airportsin?lat1=" + botLeft.latitude + "&lon1=" + botLeft.longitude
                + "&lat2=" + botRight.latitude + "&lon2=" + botRight.longitude + "&lat3=" + topLeft.latitude + "&lon3=" + topLeft.longitude + "&lat4="
                + topRight.latitude + "&lon4=" + topRight.longitude;
    }

    private void callCustomAPI(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray jsonAirports = responseObject.getJSONArray("airportsin");
                    airports = new ArrayList<Airport>();
                    for (int i = 0; i < jsonAirports.length(); i++) {
                        airports.add(new Airport(jsonAirports.getJSONObject(i)));
                    }
                    drawAirports(airports);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void drawAirports(final ArrayList<Airport> airports) {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(Circle circle : circlesAdded) {
                    circle.remove();
                }
                circlesAdded.clear();

                int x = 0;
                for(int y = 0; y < airports.size(); y ++) {
                    Airport airport = airports.get(y);
                    x ++;
                    if(x > 100) {
                        break;
                    }
                    int radius = 0;
                    switch (airport.sizeLevel) {
                        case 0:
                            radius = 5630;// 3.5 miles
                            break;
                        case 1:
                            radius = 8046;// 6.5 miles
                            break;
                        case 2:
                            radius = 11000;// 8.5 miles
                            break;

                    }
                    circlesAdded.add(mMap.addCircle(new CircleOptions()
                            .center(new LatLng(airport.lat, airport.lon))
                            .radius(radius) // this is in meters
                            .strokeColor(Color.BLUE)
                            .fillColor(0x730000ff)));
                }
            }
        });

    }
}
