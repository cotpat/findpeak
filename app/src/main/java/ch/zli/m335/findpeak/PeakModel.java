package ch.zli.m335.findpeak;

import java.util.ArrayList;

public class PeakModel {
    private int height;
    private String name;
    private double lati;
    private double longi;
    private boolean checked;
    public static ArrayList<PeakModel> PeakModels = new ArrayList<>();

    public PeakModel(
            int height,
            String name,
            double lati,
            double longi
            ) {

        this.checked = false;
        this.name = name;
        this.height = height;

        double[] transformedCoordinates = transformCoordinates(lati, longi);
        this.lati = transformedCoordinates[0];
        this.longi = transformedCoordinates[1];

        PeakModels.add(this);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public double[] transformCoordinates(double chLatitude, double chLongitude) {
        double x, y, lati, longi;

        x = (chLatitude - 1200000) / 1000000;
        y = (chLongitude - 2600000) / 1000000;

        lati = 16.9023892
                + 3.238272 * x
                - 0.270978 * Math.pow(y, 2)
                - 0.002528 * Math.pow(x, 2)
                - 0.0447 * Math.pow(y, 2) * x
                - 0.0140 * Math.pow(x, 3)
        * 100 / 36;

        longi = 2.6779094
                + 4.728982 * y
                + 0.791484 * y * x
                + 0.1306 * y * Math.pow(x, 2)
                - 0.0436 * Math.pow(y, 3)
        * 100 / 36;

        return new double[]{lati, longi};
    }
}
