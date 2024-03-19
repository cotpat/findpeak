package ch.zli.m335.findpeak;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity implements LocationService.CompassCallback, LocationService.LocationCallback {
    private final static int SAMPLING_RATE = 100000;

    private PeakService peakService;
    private boolean isPeakServiceBound = false;
    private LocationService locationService;
    private boolean isLocationServiceBound = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private TextView peakName;
    private TextView peakHeight;
    private CheckBox peakClimbed;
    private CompassView compassView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        peakName = findViewById(R.id.PeakName);
        peakHeight = findViewById(R.id.PeakHeight);
        peakClimbed = findViewById(R.id.PeakClimbed);
        compassView = findViewById(R.id.CompassView);

        bindPeakService();
        if (checkLocationPermissions()) {
            bindLocationService();
        } else {
            requestLocationPermissions();
        }
    }

    private ServiceConnection peakServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PeakService.PeakBinder binder = (PeakService.PeakBinder) service;
            peakService = binder.getService();
            isPeakServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isPeakServiceBound = false;
        }
    };

    private ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocationBinder binder = (LocationService.LocationBinder) service;
            locationService = binder.getService();
            locationService.setCompassCallback(MainActivity.this);
            locationService.setLocationCallback(MainActivity.this);
            isLocationServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isLocationServiceBound = false;
        }
    };

    // update compass view when magnetometer output changes
    @Override
    public void onCompassUpdate(float[] magnetometerValues) {
        compassView.setMagneticValues(magnetometerValues);
    }

    // update location when location changes
    @Override
    public void onLocationUpdate(Location location) {
        Log.d("MainActivity", Double.toString(location.getLatitude()) + " " + Double.toString(location.getLongitude()));
    }

    // check location permissions
    private boolean checkLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // request location permissions if necessary
    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                bindLocationService();
            } else {
                // Permission denied, handle accordingly (e.g., show explanation, disable functionality, etc.)
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // bind to PeakService
    public void bindPeakService() {
        Intent bindPeakServiceIntent = new Intent(this, PeakService.class);
        bindService(bindPeakServiceIntent, peakServiceConnection, Context.BIND_AUTO_CREATE);
    }

    // bind to LocationService
    public void bindLocationService() {
        Intent bindLocationServiceIntent = new Intent(this, LocationService.class);
        bindService(bindLocationServiceIntent, locationServiceConnection, Context.BIND_AUTO_CREATE);
    }
}
