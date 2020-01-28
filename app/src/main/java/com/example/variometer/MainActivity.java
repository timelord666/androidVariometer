package com.example.variometer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;


import com.karlotoy.perfectune.instance.PerfectTune;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements VerticalAcceleration.VertAccListener, Altitude.AltitudeListener {

    private TextView vertAccelerationTextView;
    private TextView xTextView;
    private TextView yTextView;
    private TextView zTextView;
    private TextView altTextView;
    private TextView startAltTextView;
    private TextView varioTextView;
    private VerticalAcceleration vertAccel;
    private KalmanVerticalSensorFusion kalman;
    private Altitude alt;
    private float startAlt;
    private float acceleration;
    private float altitude;
    private float vario;
    private boolean flag = true;
    private Beeper beeper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        vertAccel = new VerticalAcceleration(this);
        alt = new Altitude(this);
        beeper = new Beeper();


        vertAccelerationTextView = findViewById(R.id.vertAccel);
        xTextView = findViewById(R.id.x);
        yTextView = findViewById(R.id.y);
        zTextView = findViewById(R.id.z);
        altTextView = findViewById(R.id.altitude);
        startAltTextView = findViewById(R.id.startAltitude);
        varioTextView = findViewById(R.id.vario);


    }

    @Override
    protected void onStart() {
        super.onStart();


        vertAccel.onStart(this);
        alt.onStart(this);

    }


    @Override
    protected void onStop() {
        super.onStop();

        vertAccel.onStop();
        alt.onStop();
    }

    @Override
    public void onValChanged(float VertAccel, float[] accelValues, long time) {

        acceleration = VertAccel;
        vertAccelerationTextView.setText(String.format(Locale.getDefault(), "%02.3f", VertAccel));
        xTextView.setText(String.format(Locale.getDefault(), "%02.3f", accelValues[0]));
        yTextView.setText(String.format(Locale.getDefault(), "%02.3f", accelValues[1]));
        zTextView.setText(String.format(Locale.getDefault(), "%02.3f", accelValues[2]));
        altTextView.setText(String.format(Locale.getDefault(), "%02.3f", altitude));
        startAltTextView.setText(String.format(Locale.getDefault(), "%02.3f", startAlt));
    }

    @Override
    public void onAltChanged(float filteredAltitude, float altitude, long time) {
        if (flag) {
            startAlt = altitude;
            kalman = new KalmanVerticalSensorFusion(startAlt, (float)0.0, (float)0.1, (float)0.3, time);
            flag = false;
        } else {
            kalman.update(filteredAltitude, acceleration, time);
            vario = kalman.getVelocity();

            varioTextView.setText(String.format(Locale.getDefault(), "%02.3f", vario));

            beeper.beep(vario);
        }
        this.altitude = filteredAltitude;
    }















}


