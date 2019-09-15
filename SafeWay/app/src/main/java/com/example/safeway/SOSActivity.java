package com.example.safeway;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SOSActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView countdownText;
    private Button btnStop, btnRestart;
    private CountDownTimer timer;
    private long timeLeftInSecs = 10;
    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        countdownText = (TextView) findViewById(R.id.countdown_text);

        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(this);
        btnRestart = (Button) findViewById(R.id.btnRestart);
        btnRestart.setOnClickListener(this);
        startTimer();
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnStop:
                if(isRunning)
                    stopTimer();
                    finish();
                break;

            case R.id.btnRestart:
                stopTimer();
                timeLeftInSecs = 10;
                startTimer();
                break;

            default:
                break;
        }
    }

    public void startTimer(){
        timer = new CountDownTimer(timeLeftInSecs*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInSecs = millisUntilFinished/1000;
                updateTimer();

            }

            @Override
            public void onFinish() {

            }
        }.start();
        isRunning=true;
    }

    public void stopTimer(){
        timer.cancel();
        isRunning=false;
    }

    public void updateTimer(){
        int mins = (int)timeLeftInSecs/60;
        int secs = (int)timeLeftInSecs%60;

        String timeLeftText = "" + mins;
        timeLeftText += ":";
        if(secs<10)
            timeLeftText+="0";
        if(mins<10)
            timeLeftText = "0"+timeLeftText;
        timeLeftText += secs;

        countdownText.setText(timeLeftText);
    }
}
