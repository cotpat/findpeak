package ch.zli.m335.findpeak;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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

        try {
            InputStream is = getAssets().open("peaks.csv");
            List<String> records = Files.readAllLines(Paths.get(is.toString()), StandardCharsets.UTF_8);

            for (String record : records) {
                String[] fields = record.split(";");

                new PeakModel(
                        Integer.parseInt(fields[0]),
                        fields[1],
                        Double.parseDouble(fields[2]),
                        Double.parseDouble(fields[3])
                        );
                Log.d("CREATE:", fields[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
