package com.example.variometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;



public class Altitude implements SensorEventListener {



    public interface AltitudeListener {
        void onAltChanged(float filteredAltitude, float altitude, long time);
    }





    private Sensor baro;
    private SensorManager manager;
    private AltitudeListener listener;
    private KalmanFilter kalman;
    private float altitude;
    private float filteredAltitude;
    private float medianAlt;
    private long timer;
    private int index = 0;
    private float[] altitudes = {0, 0, 0};
    private static int filterStep = 50;
    private static float filterCoef = (float)0.02;
    private boolean flag = true;








    public Altitude(Context context) {
        manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        assert manager != null;
        baro = manager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        kalman = new KalmanFilter((float)2.0, (float)2.0, (float)0.4);
    }


    public void onStart(AltitudeListener listener) {
        manager.registerListener(this, baro, SensorManager.SENSOR_DELAY_FASTEST);
        this.listener = listener;
    }


    public void onStop() {
        listener = null;
        manager.unregisterListener(this);
    }

//    public void filterMedian() {
//
//        if ((altitudes[0] <= altitudes[1]) && (altitudes[0] <= altitudes[2])) {
//            medianAlt = (altitudes[1] <= altitudes[2]) ? altitudes[1] : altitudes[2];
//        } else {
//            if ((altitudes[1] <= altitudes[0]) && (altitudes[1] <= altitudes[2])) {
//                medianAlt = (altitudes[0] <= altitudes[2]) ? altitudes[0] : altitudes[2];
//            } else {
//                medianAlt = (altitudes[0] <= altitudes[1]) ? altitudes[0] : altitudes[1];
//            }
//        }
//    }
//
//    public void filterRunningMiddle(long time) {
//        if (time - timer > filterStep) {
//            timer = time;
//            filteredAltitude = (medianAlt * filterCoef + filteredAltitude * (1 - filterCoef));
//        }
//    }
//











    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            if (index > 2) {
                index = 0;
            }

            altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, event.values[0]);

            if (flag) {
                altitudes[0] = altitude;
                altitudes[1] = altitude;
                altitudes[2] = altitude;
                filteredAltitude = altitude;
                flag = false;
            }


            altitudes[index] = altitude;
//            filterMedian();
//            filterRunningMiddle(event.timestamp);
            filteredAltitude = kalman.filter(altitude);
            listener.onAltChanged(filteredAltitude, altitude, event.timestamp);
            index++;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
