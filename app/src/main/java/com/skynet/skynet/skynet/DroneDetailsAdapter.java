package com.skynet.skynet.skynet;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mohamed on 2016-04-23.
 */
public class DroneDetailsAdapter extends RecyclerView.Adapter<DroneDetailsAdapter.MyViewHolder> {

    private ArrayList<SingleWeatherStat> singleWeatherStats;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView statTitleTV, statNumTV;

        public MyViewHolder(View view, int viewType) {
            super(view);
            if (viewType == 0) {

            } else {
                statTitleTV = (TextView) view.findViewById(R.id.statTitle);
                statNumTV = (TextView) view.findViewById(R.id.statNum);
            }
        }
    }


    public DroneDetailsAdapter(ArrayList<SingleWeatherStat> singleWeatherStats) {
        this.singleWeatherStats = singleWeatherStats;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.drone_details_picture_row, parent, false);
                return new MyViewHolder(itemView, viewType);
            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.drone_details_info_row, parent, false);
                return new MyViewHolder(itemView, viewType);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position == 0) {

        } else {
            SingleWeatherStat singleWeatherStat = singleWeatherStats.get(position - 1);
            holder.statTitleTV.setText(singleWeatherStat.title + ":");
            if (singleWeatherStat.title.equals("Wind Direction")) {
                holder.statNumTV.setText(degToCardinal(singleWeatherStat.value));
            } else {
                holder.statNumTV.setText(Double.toString(singleWeatherStat.value));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return singleWeatherStats.size() + 1; // + 1 for the drone image
    }

    private String degToCardinal(double x) {
        String directions[] = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return directions[ (int)Math.round(( (x % 360) / 45)) % 8 ];
    }
}
