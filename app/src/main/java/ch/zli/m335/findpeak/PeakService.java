package ch.zli.m335.findpeak;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PeakService extends Service {
    private final IBinder binder = new PeakBinder();

    public class PeakBinder extends Binder {
        public PeakService getService() {
            return PeakService.this;
        }
    }

    public PeakService() { }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("PeakService", "service started");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        readAndParseCsvFile();
        return binder;
    }

    private void readAndParseCsvFile() {
        AssetManager am = getApplicationContext().getAssets();
        InputStream is = null;

        try {
            is = am.open("peaks.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            Log.d("PeakService", "peaks.csv opened");
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(";");

                new PeakModel(
                        Integer.parseInt(fields[0]),
                        fields[1],
                        Double.parseDouble(fields[2]),
                        Double.parseDouble(fields[3])
                );
                Log.d("CREATE", fields[1]);
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
