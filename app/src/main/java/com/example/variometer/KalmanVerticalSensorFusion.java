package com.example.variometer;

public class KalmanVerticalSensorFusion {

    private float positionVariance;
    private float accelerationVariance;



    private float position;
    private float velocity;
    private float acceleration;
    private long time;



    private float calibrationDrift;



    private float p11;
    private float p21;
    private float p12;
    private float p22;



    public KalmanVerticalSensorFusion(float startPos, float startAccel, float sigmaP, float sigmaA, long time) {

        position = startPos;
        velocity = 0;
        acceleration = startAccel;
        this.time = time;
        calibrationDrift = (float)0.0;


        positionVariance = sigmaP * sigmaP;
        accelerationVariance = sigmaA * sigmaA;

        p11 = 0;
        p12 = 0;
        p21 = 0;
        p22 = 0;


    }


    public void update(float position, float acceleration, long time) {
        long deltaTime = time - this.time;
        float dt = ((float)deltaTime/(float)1000000000.0);
        this.time = time;

        float dtPow = dt * dt;
        this.acceleration = acceleration;
        this.position += dt * velocity + dtPow * this.acceleration / 2;
        velocity += dt * this.acceleration;



        float inc;


        dtPow *= dt;
        inc = dt * p22 + dtPow * accelerationVariance / 2;
        dtPow *= dt;

        p11 += dt * (p12 + p21 + inc) - (dtPow * accelerationVariance / 4);

        p21 += inc;
        p12 += inc;
        p22 += dt * dt * accelerationVariance;


        float s, k11, k12, y;

        s = p11 + positionVariance;
        k11 = p11 / s;
        k12 = p12 / s;
        y = position - this.position;




        this.position += k11 * y;
        velocity += k12 * y;

        p22 -= k12 * p21;
        p12 -= k12 * p11;
        p21 -= k11 * p21;
        p11 -= k11 * p11;






    }


    public float getVelocity() {
        return velocity;
    }
}
