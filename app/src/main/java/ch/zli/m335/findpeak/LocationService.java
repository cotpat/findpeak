package ch.zli.m335.findpeak;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private Sensor accelerometer;
    private Sensor location;

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    public LocationService() { }

    public class LocationBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();


        initializeCompassSensor();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void initializeCompassSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public Sensor getMagnetometer() {
        return magnetometer;
    }

    public Sensor getAccelerometer() {
        return accelerometer;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == magnetometer) {
            System.arraycopy(
                    event.values,
                    0,
                    lastMagnetometer,
                    0,
                    event.values.length
            );
            lastMagnetometerSet = true;
        } else if (event.sensor == accelerometer) {
            System.arraycopy(
                    event.values,
                    0,
                    lastAccelerometer,
                    0,
                    event.values.length
            );
            lastAccelerometerSet = true;
        }

        if (lastMagnetometerSet && lastAccelerometerSet) {
            SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    lastAccelerometer,
                    lastMagnetometer
            );
            SensorManager.getOrientation(rotationMatrix, orientation);

            float azimuthInRadians = orientation[0];
            float azimuthInDegrees = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.compass);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
