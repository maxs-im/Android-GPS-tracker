package student.maxim.lab_3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import student.maxim.lab_3.listener.MyListener;

public class MainActivity extends AppCompatActivity {

    /* Variable that store connecting to location services */
    private LocationManager locationManager;
    private LocationListener locationListener;

    /* Looking for numbers */
    public Location         pointLocation                   = null;
    private float           reachedDistance                 = 0;

    /* Constants for updating GPS connection */
    private final long      maxTimeUpdate                   = 1000;
    private final long      minTimeUpdate                   = 100;
    public final float      minDistanceUpdate               = (float) 1.5;

    /* Request constant for correct permission */
    private final int       PERMISION_REQUEST_GPS_CONSTANT  = 7;

    private boolean         running                         = false;

    /* Entrance to the Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    /* function for Start Button */
    public void startPressed(View view) {
        if(running) {
            return;
        }
        running = true;
        setDefaultText();

        locationListener = new MyListener(this, (int) (maxTimeUpdate/minTimeUpdate));

        setManager(true);

        setTimer();
    }

    /* function for Stop Button */
    public void stopPressed(View view) {
        if(running) {
            running = false;
            Chronometer chrono = findViewById(R.id.chronometer);
            chrono.stop();
            if(locationManager != null) {
                locationManager.removeUpdates(locationListener);
            }
        } else {
            setDefaultText();
        }
    }

    /* Check answer on request of the permission*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISION_REQUEST_GPS_CONSTANT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setManager(false);
                    return;
                }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /* Init manager with out listener */
    private void setManager(boolean ask) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ask) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, PERMISION_REQUEST_GPS_CONSTANT);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeUpdate, minDistanceUpdate, locationListener);
        }
    }

    /* Reset text and number values */
    private void setDefaultText() {
        //Distance
        TextView toDist = findViewById(R.id.textDistance);
        toDist.setText(R.string.distance_val);
        reachedDistance = 0;
        //Speed
        TextView toSpeed = findViewById(R.id.textSpeed);
        toSpeed.setText(R.string.speed_val);
        //Time
        Chronometer toTime = findViewById(R.id.chronometer);
        toTime.setText(R.string.distance_val);

        pointLocation = null;
    }

    /* Set timer to null */
    private void setTimer() {
        Chronometer chrono = findViewById(R.id.chronometer);
        chrono.setBase(SystemClock.elapsedRealtime());
        chrono.start();
    }

    /* function to Update out distance */
    public float addDistance(float newDistance) {
        reachedDistance += newDistance;
        return reachedDistance;
    }

    /* Correctly finish application */

    @Override
    public void onDestroy() {
        if(locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        super.onDestroy();
    }
}