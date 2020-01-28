package com.example.variometer;


import com.karlotoy.perfectune.instance.PerfectTune;

public class Beeper {

    private PerfectTune tone;
    private long time;
    private static int BeepDuration = 200;
    private static int BeepDurationX2 = BeepDuration * 2;
    private static int BeepDurationCoef = 70;
    private static int BeepDurationCoefX2 = BeepDurationCoef * 2;
    private static double UpMinSoundThreshold = 0.5;
    private static int UpMaxSoundThreshold = 10;
    private static int InitialFreqUp = 1400;
    private static int FreqCoef = 100;
    private static int DownVarioThreshold = -2;
    private static int FreqDown = 400;
    private boolean fl = true;

    Beeper() {
        tone = new PerfectTune();
        time = System.currentTimeMillis();
    }

    public void beep(float vario) {
        float duration = (BeepDuration - vario * BeepDurationCoef >= 25) ? BeepDuration - vario * BeepDurationCoef : 25;
        if (System.currentTimeMillis() - time > duration && !fl) {
            fl = !fl;
            tone.stopTune();
        }

        if ((vario > UpMinSoundThreshold) && (vario < UpMaxSoundThreshold)) {
            duration = (BeepDurationX2 - vario * BeepDurationCoefX2 >= 25) ? BeepDurationX2 - vario * BeepDurationCoefX2 : 25;
            if (fl && ((System.currentTimeMillis() - time) > duration)) {
                time = System.currentTimeMillis();
                tone.setTuneFreq(InitialFreqUp + vario * FreqCoef);
                tone.playTune();
                fl = !fl;
            }
        } else if (vario < DownVarioThreshold) {
            tone.setTuneFreq(FreqDown);
            tone.playTune();
        } else {
            tone.stopTune();
        }
    }


}
