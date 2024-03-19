package ch.zli.m335.findpeak;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final static int SAMPLING_RATE = 100000;
    private final static int DISPLAY_PERIOD = 30;

    private PeakService peakService;
    private boolean isPeakServiceBound = false;
    private LocationService locationService;
    private boolean isLocationServiceBound = false;

    private TextView peakName;
    private TextView peakHeight;
    private CheckBox peakClimbed;
    private ImageView compass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        peakName = findViewById(R.id.PeakName);
        peakHeight = findViewById(R.id.PeakHeight);
        peakClimbed = findViewById(R.id.PeakClimbed);
        compass = findViewById(R.id.Compass);

        Intent bindPeakServiceIntent = new Intent(this, PeakService.class);
        Intent bindLocationServiceIntent = new Intent(this, LocationService.class);
        bindService(bindPeakServiceIntent, peakServiceConnection, Context.BIND_AUTO_CREATE);
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
}
