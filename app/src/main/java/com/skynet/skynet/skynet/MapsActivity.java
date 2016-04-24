package com.skynet.skynet.skynet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

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
    private ArrayList<Drone> drones;
    private TextView flying_conditions, wind_speed_text, wind_direction_text, temperature_text, pressure_text, humidity_text;
    private TextView warnings_title, safety_reasons;
    private ImageButton changingButton, openPanelButton;
    private ImageView simpleConditionImage;

    @Bind(R.id.my_toolbar)
    public Toolbar myToolbar;

    @Bind(R.id.btn_map_toggle)
    public ImageButton btnMapToggle;


    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private Circle mCircle;

    private ArrayList<Circle> circlesAdded = new ArrayList<>() ;
    private ArrayList<Circle> droneCirclesAdded = new ArrayList<>() ;
    private ArrayList<Marker> airplaneMarkersAdded = new ArrayList<>() ;
    private LatLng mLatestLocationLatLng;
    private PopupWindow changeStatusPopUp;

    private String status;
    private String unsafeReasons;
    private double windSpeed;
    private String windDirection;
    private int temp;
    private int pressure;
    private double humidity;
    private ArrayList<MapAirplane> mapAirPlanes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);
        myToolbar.setTitle("Hermes");

        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SkynetPopupMenu popup = new SkynetPopupMenu(MapsActivity.this, item.getActionView());
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.main_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new SkynetPopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(
                                MapsActivity.this,
                                "You Clicked : " + item.getTitle(),
                                Toast.LENGTH_SHORT
                        ).show();
                        Log.v("saif", "camera change event called");
                        return true;
                    }
                });

                popup.show(); //showing popup menu
                Log.v("saif", "on menu clicked ");
                return true;
            }
        });

        setSupportActionBar(myToolbar);

        myToolbar.findViewById(R.id.filter_menu_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                int[] location = new int[2];
//                v.getLocationOnScreen(location);
                //Initialize the Point with x, and y positions
//                Point point = new Point();
//                point.x = 0;
//                point.y = 300;
//                showStatusPopup(MapsActivity.this, point);

                SkynetPopupMenu popup = new SkynetPopupMenu(MapsActivity.this, v);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.main_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new SkynetPopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_toggle_self_circle:
                                showSelfCircle = !showSelfCircle;
                                drawSelfCircle(showSelfCircle);
                                return true;
                            case R.id.action_toggle_airports:
                                showAirportCircles = !showAirportCircles;
                                drawAirportCircles(showAirportCircles);
                                return true;
                            case R.id.action_toggle_drones:
                                showDroneCircles = !showDroneCircles;
                                drawDroneCircles(showDroneCircles);
                                return true;
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
                Log.v("saif", "on menu clicked ");
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

        context = this;

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if(!showDroneCircles && ! showAirportCircles) return;
                try {
                    LatLng botleft = mMap.getProjection()
                            .getVisibleRegion().nearLeft;

                    LatLng botright = mMap.getProjection()
                            .getVisibleRegion().nearRight;

                    LatLng topleft = mMap.getProjection()
                            .getVisibleRegion().farLeft;

                    LatLng topright = mMap.getProjection()
                            .getVisibleRegion().farRight;

                    callCustomAPI(getCustomURL(botleft, botright, topleft, topright));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        flying_conditions = (TextView) findViewById(R.id.flying_conditions);
        wind_speed_text = (TextView) findViewById(R.id.wind_speed_text);
        wind_direction_text = (TextView) findViewById(R.id.wind_direction_text);
        temperature_text = (TextView) findViewById(R.id.temperature_text);
        pressure_text = (TextView) findViewById(R.id.pressure_text);
        humidity_text = (TextView) findViewById(R.id.humidity_text);
        warnings_title = (TextView) findViewById(R.id.warnings_title);
        safety_reasons = (TextView) findViewById(R.id.safety_reasons);
        simpleConditionImage = (ImageView) findViewById(R.id.simple_condition);
        changingButton = (ImageButton) findViewById(R.id.changingButton);
        openPanelButton = (ImageButton) findViewById(R.id.openPanelButton);
        final SlidingUpPanelLayout panel = (SlidingUpPanelLayout) findViewById(R.id.bottomsheet);

        changingButton.setVisibility(View.INVISIBLE);
        openPanelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        panel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    changingButton.setVisibility(View.VISIBLE);
                } else {
                    changingButton.setVisibility(View.INVISIBLE);
                }
            }
        });


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.nav_bar_color));
            getWindow().setStatusBarColor(getResources().getColor(R.color.stat_bar_color));
        }

        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        flying_conditions.setTypeface(typeFace);
        wind_speed_text.setTypeface(typeFace);
        wind_direction_text.setTypeface(typeFace);
        temperature_text.setTypeface(typeFace);
        pressure_text.setTypeface(typeFace);
        humidity_text.setTypeface(typeFace);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
//        return true;
//    }

    boolean showSelfCircle = true;
    boolean showDroneCircles = true;
    boolean showAirportCircles = true;
    boolean showAirPlanes = true;

    // The method that displays the popup.
    private void showStatusPopup(final Activity context, Point p) {

        // Inflate the popup_layout.xml
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popover_view, null);

        // Creating the PopupWindow
        changeStatusPopUp = new PopupWindow(context);
        changeStatusPopUp.setContentView(layout);
        changeStatusPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setFocusable(true);

        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
        int OFFSET_X = -20;
        int OFFSET_Y = 50;

        //Clear the default translucent background
        changeStatusPopUp.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
//        PopupMenu popup = new PopupMenu(MapsActivity.this, item.getActionView());
//        //Inflating the Popup using xml file
//        popup.getMenuInflater()
//                .inflate(R.menu.main_menu, popup.getMenu());
//
//        //registering popup with OnMenuItemClickListener
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            public boolean onMenuItemClick(MenuItem item) {
//                Toast.makeText(
//                        MapsActivity.this,
//                        "You Clicked : " + item.getTitle(),
//                        Toast.LENGTH_SHORT
//                ).show();
//                return true;
//            }
//        });
//
//        popup.show(); //showing popup menu

//
        switch (item.getItemId()) {
            case R.id.action_toggle_self_circle:
                showSelfCircle = !showSelfCircle;
                drawSelfCircle(showSelfCircle);
                return true;
            case R.id.action_toggle_airports:
                showAirportCircles = !showAirportCircles;
                drawAirportCircles(showAirportCircles);
                return true;
            case R.id.action_toggle_drones:
                showDroneCircles = !showDroneCircles;
                drawDroneCircles(showDroneCircles);
                return true;
            case R.id.action_toggle_airplanes:
                showAirPlanes = !showAirPlanes;
                drawAirplaneCircles(showAirPlanes);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void drawAirplaneCircles(boolean showAirPlanes) {
        if(showAirPlanes) {
            drawAirplanes(mapAirPlanes);
        } else {
            for(Marker marker : airplaneMarkersAdded) {
                marker.remove();
            }
            airplaneMarkersAdded.clear();
        }
    }

    private void drawDroneCircles(boolean shouldDraw) {
        if(shouldDraw) {
            drawDrones(drones);
        } else {
            for(Circle circle : droneCirclesAdded) {
                circle.remove();
            }
            droneCirclesAdded.clear();
        }
    }

    private void drawAirportCircles(boolean shouldDraw) {
        if(shouldDraw) {
            drawAirports(airports);
        } else {
            for(Circle circle : circlesAdded) {
                circle.remove();
            }
            circlesAdded.clear();
        }
    }

    private void drawSelfCircle(boolean shouldDraw) {
        if(shouldDraw) {
            createCircleAroundPoint(mLatestLocationLatLng);
        } else {
            if(mCircle != null) {
                mCircle.remove();
                mCircle = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @OnClick(R.id.btn_map_toggle)
    public void mapTypeToggle(ImageButton button) {
        if(mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            button.setImageResource(R.drawable.google_maps_img);
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            button.setImageResource(R.drawable.google_earth_mdpi);
        }
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

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
    }

    private void createCircleAroundPoint(LatLng latlng) {
        if(!showSelfCircle) return;

            mCircle = mMap.addCircle(new CircleOptions()
                    .center(latlng)
                    .radius(4000) // this is in meters
                    .strokeWidth(0)
                    .fillColor(0x8038a4dc));

    }

    @Override
    public void onLocationChanged(Location location) {
        mLatestLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        createCircleAroundPoint(mLatestLocationLatLng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatestLocationLatLng, 10);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
        createCircleAroundPoint(mLatestLocationLatLng);
        try {
            callWeatherAPI(getWeatherURL(mLatestLocationLatLng.latitude, mLatestLocationLatLng.longitude));
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
                + Double.toString(lon) + "&units=metric&APPID=7a8668b5c3f71c0608b503bdd446c3c1";
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
                    fillBottomSheet();
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
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray jsonAirports = responseObject.getJSONArray("airportsin");
                    JSONArray jsonDrones = responseObject.getJSONArray("dronesin");
                    JSONArray jsonFlights = responseObject.getJSONArray("flightsin");
                    airports = new ArrayList<>();
                    for (int i = 0; i < jsonAirports.length(); i++) {
                        if (jsonAirports.getJSONObject(i).has("lat") && jsonAirports.getJSONObject(i).has("lon")) {
                            airports.add(new Airport(jsonAirports.getJSONObject(i)));
                        }
                    }

                    drones = new ArrayList<Drone>();
                    for (int i = 0; i < jsonAirports.length(); i++) {
                        drones.add(new Drone(jsonDrones.getJSONObject(i)));
                    }

                    mapAirPlanes = new ArrayList<MapAirplane>();
                    for (int i = 0; i < jsonFlights.length(); i++) {
                        mapAirPlanes.add(new MapAirplane(jsonFlights.getJSONObject(i)));
                    }

                    drawAirports(airports);
                    drawDrones(drones);
                    drawAirplanes(mapAirPlanes);
                    fillBottomSheet();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void drawAirplanes(final ArrayList<MapAirplane> mapAirPlanes) {
        Log.v("saif", "airplanes " + mapAirPlanes.size());

        if(!showAirPlanes) return;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ArrayList<MapAirplane> airplanesToAdd = new ArrayList<>();
                int b = 0;
                while(airplanesToAdd.size() < 100 && b < mapAirPlanes.size()) {
                    airplanesToAdd.add(mapAirPlanes.get(b));
                    b++;
                }
                for(Marker marker : airplaneMarkersAdded) {
                    for(int x = 0; x < airplanesToAdd.size(); x ++) {
                        MapAirplane airplane = airplanesToAdd.get(x);
                        if(marker.getPosition().longitude == airplane.lon && marker.getPosition().latitude == airplane.lat) {
                            airplanesToAdd.remove(airplane);
                        }
                    }
                }
                for (int y = 0; y < airplanesToAdd.size(); y++) {
                    MapAirplane airplane = airplanesToAdd.get(y);
                    if (y > 20) {
                        return;
                    }

                    Log.v("saif", "added airplane " + airplane.toString() + " " + airplane.lat + " " + airplane.lon + " " + airplane.heading);
                    airplaneMarkersAdded.add(mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(airplane.lat, airplane.lon)).rotation(airplane.heading).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_air_plane))));
                }
            }
        });
    }

    private void drawAirports(final ArrayList<Airport> airports) {

        if(!showAirportCircles) return;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ArrayList<Airport> airportsToAdd = new ArrayList<>();
                int b = 0;
                while(airportsToAdd.size() < 100 && b < airports.size()) {
                    airportsToAdd.add(airports.get(b));
                    b++;
                }
                for(Circle circle : circlesAdded) {
                    for(int x = 0; x < airportsToAdd.size(); x ++) {
                        Airport airport = airportsToAdd.get(x);
                        if(circle.getCenter().longitude == airport.lon && circle.getCenter().latitude == airport.lat) {
                            airportsToAdd.remove(airport);
                        }
                    }
                }
                int x = 0;
                for (int y = 0; y < airportsToAdd.size(); y++) {
                    Airport airport = airportsToAdd.get(y);
                    x++;
                    if (x > 20) {
                        break;
                    }

                    circlesAdded.add(mMap.addCircle(new CircleOptions()
                            .center(new LatLng(airport.lat, airport.lon))
                            .radius(airport.radius) // this is in meters
                            .strokeWidth(0)
                            .fillColor(0x80f37c20)));
                }
            }
        });

    }

    private void drawDrones(final ArrayList<Drone> drones) {

        if(!showDroneCircles) return;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ArrayList<Drone> dronesToAdd = new ArrayList<>();
                int b = 0;
                while(dronesToAdd.size() < 100 && b < drones.size()) {
                    dronesToAdd.add(drones.get(b));
                    b ++;
                }

                for(Circle circle : droneCirclesAdded) {
                    for(int x = 0; x < dronesToAdd.size(); x ++) {
                        Drone airport = dronesToAdd.get(x);
                        if(circle.getCenter().longitude == airport.lon && circle.getCenter().latitude == airport.lat) {
                            dronesToAdd.remove(airport);
                        }
                    }
                }

                for(int y = 0; y < dronesToAdd.size(); y ++) {
                    Drone drone = dronesToAdd.get(y);

                    droneCirclesAdded.add(mMap.addCircle(new CircleOptions()
                            .center(new LatLng(drone.lat, drone.lon))
                            .radius(drone.radius) // this is in meters
                            .strokeWidth(0)
                            .fillColor(0x80db3126)));
                }
            }
        });
    }

    private void fillBottomSheet() {
        status = "safe";
        unsafeReasons = "";

        windSpeed = 0.0;
        temp = 0;
        pressure = 0;
        humidity = 0.0;
        windDirection = "";

        if (weatherData != null) {
            windSpeed = weatherData.windSpeed;
            windDirection = weatherData.windDirectionCardinal;
            temp = (int) Math.round(weatherData.temp);
            pressure = (int) Math.round(weatherData.pressure);
            humidity = weatherData.humidity;

            // Wind speed no more than 20
            // Can't handle precipitation
            // Can't handle temperature less than 0 or above 50
            // Can't handle humidity above 90%

            if (windSpeed >= 20) {
                status = "unsafe";
                unsafeReasons += "- Wind speeds are too high\n";
            }
            if (temp >= 50) {
                status = "unsafe";
                unsafeReasons += "- The temperature is too high\n";
            } else if (temp <= 0) {
                status = "unsafe";
                unsafeReasons += "- The temperature is too low\n";
            }
            if (humidity >= 90) {
                status = "unsafe";
                unsafeReasons += "- The humidity is too high\n";
            }
        }

        if ((airports != null) && (mLatestLocationLatLng != null)) {
            for (Airport airport : airports) {
                double distBetween = DistanceCalculator.distance(mLatestLocationLatLng.latitude, mLatestLocationLatLng.longitude, airport.lat, airport.lon, "k");
                if (distBetween <= (4000 + airport.radius)) {
                    status = "unsafe";
                    unsafeReasons += "- You are too close to an airport\n";
                    break;
                }
            }
        }

        if ((drones != null) && (mLatestLocationLatLng != null)) {
            for (int i = 0; i < drones.size(); i++) {
                Drone drone = drones.get(i);
                double distBetween = DistanceCalculator.distance(mLatestLocationLatLng.latitude, mLatestLocationLatLng.longitude, drone.lat, drone.lon, "k");
                if (distBetween <= (4000 + drone.radius)) {
                    if (!status.equals("unsafe")) {
                        status = "warning";
                    }
                    unsafeReasons += "- You are too close to another drone operator\n";
                    break;
                }
            }
        }

        MapsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status.equals("safe")) {
                    flying_conditions.setText("Safe flying conditions");
                    changingButton.setImageResource(R.drawable.icon_green_drone);
                    openPanelButton.setImageResource(R.drawable.icon_green_drone);
                    simpleConditionImage.setImageResource(R.drawable.icon_checkmark);
                    warnings_title.setVisibility(View.INVISIBLE);
                } else if (status.equals("warning")) {
                    flying_conditions.setText("Non-ideal flying conditions");
                    changingButton.setImageResource(R.drawable.icon_yellow_drone);
                    openPanelButton.setImageResource(R.drawable.icon_yellow_drone);
                    simpleConditionImage.setImageResource(R.drawable.icon_slightly_risky);
                    warnings_title.setVisibility(View.VISIBLE);
                } else {
                    flying_conditions.setText("Dangerous flying conditions");
                    changingButton.setImageResource(R.drawable.icon_red_drone);
                    openPanelButton.setImageResource(R.drawable.icon_red_drone);
                    simpleConditionImage.setImageResource(R.drawable.icon_risky);
                    warnings_title.setVisibility(View.VISIBLE);
                }

                wind_speed_text.setText("Wind speed: " + Double.toString(windSpeed) + " km/h");
                wind_direction_text.setText("Wind direction: " + windDirection);
                temperature_text.setText("Temperature: " + Integer.toString(temp) + "°C");
                pressure_text.setText("Pressure: " + Integer.toString(pressure) + " hpa");
                humidity_text.setText("Humidity: " + Double.toString(humidity) + "%");
                safety_reasons.setText(unsafeReasons);
            }
        });
    }
}
