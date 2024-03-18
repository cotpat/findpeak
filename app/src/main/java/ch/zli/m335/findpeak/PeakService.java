package ch.zli.m335.findpeak;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class PeakService extends Service {
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
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
