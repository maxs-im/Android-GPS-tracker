package student.maxim.lab_3.listener;

/*
  Created by Maxim

  Class MyListener

  on 05.12.2017.
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import student.maxim.lab_3.MainActivity;
import student.maxim.lab_3.R;

import java.util.Vector;

import static java.lang.String.*;

/**
 * This class listen to the incoming new locations from GPS
 */
public class MyListener implements LocationListener{
    private MainActivity activityFrom;
    Vector<Location> vec;
    int maxsize;

    public MyListener(MainActivity in, int _size) {
        super();
        activityFrom = in;
        maxsize = _size;
        vec = new Vector<>();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onLocationChanged(Location location) {
        if(location.getAccuracy() < 0) {
            return;                     // invalid location
        }

        TextView editSpeed = activityFrom.findViewById(R.id.textSpeed);
        if(location.hasSpeed()) {
            editSpeed.setText(format("%.0f m/s", location.getSpeed()));
        }

        vec.add(location);
        if(vec.size() == maxsize) {
            Location curr = vec.get(0);
            for(Location it : vec) {
                if(it.getAccuracy() > curr.getAccuracy()) {
                    curr = it;
                }
            }
            vec.clear();

            setBetterLocation(curr);
        }
    }

    private void setBetterLocation(Location in) {
        Location oldPoint = activityFrom.pointLocation;
        if (oldPoint != null) {
            TextView editDistance = activityFrom.findViewById(R.id.textDistance);
            float newDistance = in.distanceTo(oldPoint);
            if(newDistance > activityFrom.minDistanceUpdate) {
                activityFrom.pointLocation = in;
                editDistance.setText(format("%.1f m", activityFrom.addDistance(newDistance)));
            }
        } else {
            activityFrom.pointLocation = in;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activityFrom.startActivity(intent);
    }
}
