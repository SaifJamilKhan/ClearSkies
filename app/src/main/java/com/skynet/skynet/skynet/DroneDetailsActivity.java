package com.skynet.skynet.skynet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;

public class DroneDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DroneDetailsAdapter adapter;
    private WeatherData weatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_details);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setElevation(0);
            ab.setDisplayShowTitleEnabled(false);
        }

        weatherData = (WeatherData) getIntent().getSerializableExtra("weatherData");
        ArrayList<SingleWeatherStat> singleWeatherStats = new ArrayList<>();
        singleWeatherStats.add(new SingleWeatherStat("Temperature", weatherData.temp));
        singleWeatherStats.add(new SingleWeatherStat("Pressure", weatherData.pressure));
        singleWeatherStats.add(new SingleWeatherStat("Humidity", weatherData.humidity));
        singleWeatherStats.add(new SingleWeatherStat("Wind Speed", weatherData.windSpeed));
        singleWeatherStats.add(new SingleWeatherStat("Wind Direction", weatherData.windDirection));

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new DroneDetailsAdapter(singleWeatherStats);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
