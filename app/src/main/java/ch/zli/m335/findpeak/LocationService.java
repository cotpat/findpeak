package ch.zli.m335.findpeak;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class LocationService extends Service implements SensorEventListener {
    private final static int SAMPLING_RATE = 10000;

    private final IBinder binder = new LocationBinder();

    private SensorManager sensorManager;

    private Sensor magnetometer;
    private float[] magnetometerValues = new float[3];
    private CompassCallback compassCallback;

    private Sensor location;


    public LocationService() { }

    public class LocationBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (magnetometer != null) {
            sensorManager.registerListener(
                    this,
                    magnetometer,
                    SAMPLING_RATE
            );
        }

        return binder;
    }

    private void initializeCompassSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetometerValues = event.values;

            if (compassCallback != null) {
                compassCallback.onCompassUpdate(magnetometerValues);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public interface CompassCallback {
        void onCompassUpdate(float[] magneticValues);
    }
}
