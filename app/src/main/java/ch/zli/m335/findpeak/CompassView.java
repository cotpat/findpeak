package ch.zli.m335.findpeak;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View {
    private Paint paint;
    private float[] magneticValues;

    public CompassView(Context context) {
        super(context);
        init();
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int length = Math.min(width, height) / 2;

        if (magneticValues != null) {
            float angle = (float) Math.toDegrees(Math.atan2(magneticValues[0], magneticValues[1]));
            canvas.rotate(-angle, centerX, centerY);
            canvas.drawLine(centerX, centerY, centerX, centerY - length, paint);
        }
    }

    public void setMagneticValues(float[] magneticValues) {
        this.magneticValues = magneticValues;
        invalidate();
    }
}