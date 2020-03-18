package com.example.systemeamdiltr.utils;

import android.os.CountDownTimer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MyCountDown extends CountDownTimer {

    DateFormat dateFormat;
    String dateTime;
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public MyCountDown(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    }

    public String getDateTime(){
        dateTime= dateFormat.format(System.currentTimeMillis());
        return dateTime;
    }

    @Override
    public void onTick(long millisUntilFinished) {

    }

    @Override
    public void onFinish() {

    }
}