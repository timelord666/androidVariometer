package com.example.variometer;

public class KalmanFilter {

    private float errMeasure = (float)1.5;
    private float errEstimate = (float)1.5;
    private float coef = (float)0.5;



    private float currentEstimate = (float)0.0;
    private float lastEstimate = (float)0.0;
    private float kalmanGain = (float)0.0;


    public KalmanFilter(float mes, float est, float q) {
        errMeasure = mes;
        errEstimate = est;
        coef = q;

    }

    public float filter(float val) {



        kalmanGain = errEstimate / (errEstimate + errMeasure);
        currentEstimate = lastEstimate + kalmanGain * (val - lastEstimate);
        errEstimate = ((float)1.0 - kalmanGain) * errEstimate + Math.abs(lastEstimate - currentEstimate) * coef;
        lastEstimate = currentEstimate;

        return currentEstimate;

    }
}
