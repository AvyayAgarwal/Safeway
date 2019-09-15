package com.example.safeway;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class SOSActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView countdownText;
    private Button btnStop, btnRestart;
    private CountDownTimer timer;
    private long timeLeftInSecs = 10;
    private boolean isRunning;
//    private FusedLocationProviderClient fusedLocationClient;

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
                // Access a Cloud Firestore instance from your Activity
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,this);
//                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Date currentTime = Calendar.getInstance().getTime();

//                LocationManager locationManager = (LocationManager)
//                        getSystemService(Context.LOCATION_SERVICE);
//                LocationListener locationListener = new MyLocationListener();
//                locationManager.requestLocationUpdates(
//                        LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

                // Update one field, creating the document if it does not already exist.
                Map<String, Object> data = new HashMap<>();
                data.put("latitude", "43.4731688");
                data.put("longitude", "-80.5394869");
                data.put("timestamp", currentTime.toString());

                db.collection("emergency")
                        .add(data);
                Toast.makeText(SOSActivity.this, "Emergency alert sent to authorities", Toast.LENGTH_LONG).show();
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
