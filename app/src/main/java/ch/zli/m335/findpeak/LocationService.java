package ch.zli.m335.findpeak;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LocationService extends Service implements SensorEventListener, LocationListener {
    private final static int SAMPLING_RATE = 10000;

    private final IBinder binder = new LocationBinder();

    private SensorManager sensorManager;

    private Sensor magnetometer;
    private CompassCallback compassCallback;

    private LocationManager locationManager;
    private LocationCallback locationCallback;


    public LocationService() { }

    public class LocationBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    // init sensors on create
    @Override
    public void onCreate() {
        super.onCreate();
        initializeSensors();
        Log.d("LocationService", "service started");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("LocationService", "service bound");

        return binder;
    }

    // init magnetometer and location
    // location init involves asking for permissions
    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, magnetometer, SAMPLING_RATE);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            Log.e("LocationService", "Missing position permission");
        }
    }

    // when orientation changes, update the variables updated by compass callback
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float[] magnetometerValues = event.values;

            if (compassCallback != null) {
                compassCallback.onCompassUpdate(magnetometerValues);
            }
        }
    }

    // when location changes, update the variables obtained by location callback
    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (locationCallback != null) {
            locationCallback.onLocationUpdate(location);
        }
    }

    // we'll leave this blank
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    // register another object as compass callback recipient
    public void setCompassCallback(CompassCallback compassCallback) {
        this.compassCallback = compassCallback;
    }

    // register another object as compass callback recipient
    public void setLocationCallback(LocationCallback locationCallback) {
        this.locationCallback = locationCallback;
    }

    // Create interface for implementing compass callback in another class
    public interface CompassCallback {
        void onCompassUpdate(float[] magneticValues);
    }

    // Create interface for implementing location callback in another class
    public interface LocationCallback {
        void onLocationUpdate(Location location);
    }
}
