package com.example.variometer;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class VerticalAcceleration implements SensorEventListener {

    public interface VertAccListener {
        void onValChanged(float VertAccel, float[] accelValues, long time);
    }



    // objects
    private Sensor accel;
    private Sensor lineAccel;
    private Sensor vector;
    private SensorManager manager;
    private VertAccListener listener;
    private KalmanFilter kalman;


    // values

    private float[] accelValues = {0, 0, 0};
    private float[] ra = {0, 0, 0};
    private float[] vectorValues = {0, 0, 0, 0};
    private float vertAccel = 0;
    private long time;




    public VerticalAcceleration(Context context) {
        manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        assert manager != null;
        accel = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lineAccel = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        vector = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        kalman = new KalmanFilter((float)0.3, (float)0.3, (float)0.8);


    }

    public void onStart(VertAccListener listener) {

        manager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        manager.registerListener(this, lineAccel, SensorManager.SENSOR_DELAY_FASTEST);
        manager.registerListener(this, vector, SensorManager.SENSOR_DELAY_FASTEST);
        this.listener = listener;
    }

    public void onStop() {
        listener = null;
        manager.unregisterListener(this);
    }

    private float[] matrixMult(float[] a, float[] b) {
        float[] result = new float[3];
        result[0] = a[0]*b[0] + a[1]*b[3] + a[2]*b[6];
        result[1] = a[0]*b[1] + a[1]*b[4] + a[2]*b[7];
        result[2] = a[0]*b[2] + a[1]*b[5] + a[2]*b[8];
        return result;
    }

    public void computeVertAccell() {

        float[] rotationMatrix = new float[9];

        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectorValues);

        ra[2] = ra[2] + (float)0.25;
        float[] accelerationInWorldFrame = matrixMult(ra, rotationMatrix);

//        final float alpha = (float)0.8;
//        float gravity = (float)9.81;

        //gravity = alpha * gravity + (1 - alpha) * accelerationInWorldFrame[2];

        vertAccel = kalman.filter(accelerationInWorldFrame[2]);

//        vertAccel = (accelerationInWorldFrame[0] * ra[0] + accelerationInWorldFrame[1] * ra[1] + accelerationInWorldFrame[2] * ra[2]) * (float)9.80665;
//        vertAccel /= 100;

    }





    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            accelValues = event.values;
            time = event.timestamp;
        } else if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            vectorValues = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            ra = event.values;
        }

        computeVertAccell();

        assert listener != null;
        listener.onValChanged(vertAccel, accelValues, time);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}


