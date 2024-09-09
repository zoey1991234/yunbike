package com.example.mybicycle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.Chronometer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class rtn_bicycle extends AppCompatActivity {

    private Chronometer chronometer;
    private AlertDialog alertDialog;
    private final Handler handler = new Handler();
    private Runnable updateTimeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rtn_bicycle);

        chronometer = findViewById(R.id.chronometer);
        Button rtnButton = findViewById(R.id.rtnButton);

        // Start the chronometer when the activity is created
        startChronometer();

        // Set up the return button
        rtnButton.setOnClickListener(v -> showReturnDialog());
    }

    @Override
    protected void onPause() {
        super.onPause();
        chronometer.stop();
        stopUpdatingTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startChronometer();
    }

    private void startChronometer() {
        long startTime = SystemClock.elapsedRealtime();
        chronometer.setBase(startTime);
        chronometer.start();

        // Custom tick listener to format time as HH:MM:SS
        chronometer.setOnChronometerTickListener(chronometer -> {
            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
            int hours = (int) (elapsedMillis / 3600000);
            int minutes = (int) (elapsedMillis % 3600000 / 60000);
            int seconds = (int) (elapsedMillis % 60000 / 1000);
            String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
            chronometer.setText(time);
        });
    }

    private void showReturnDialog() {
        String currentTime = chronometer.getText().toString();
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        int minutes = (int) (elapsedMillis / 60000);
        int cost = (minutes / 3);

        String initialMessage = "You have been riding for " + currentTime + ". Are you ready to return the bike?";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Return Bike")
                .setMessage(initialMessage)
                .setPositiveButton("QR Code Scanner", (dialog, which) -> {
                    // Pause the chronometer
                    chronometer.stop();
                    stopUpdatingTime();
                    // Launch QR Code Scanner activity with return bike state and rental time & cost
                    Intent intent = new Intent(rtn_bicycle.this, QRCodeScannerActivity.class);
                    intent.putExtra("isReturningBike", true);
                    intent.putExtra("rental_time", currentTime);
                    intent.putExtra("cost", cost);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Dismiss the dialog and continue chronometer
                    dialog.dismiss();
                    stopUpdatingTime();
                });

        alertDialog = builder.create();
        alertDialog.show();

        updateTimeTask = new Runnable() {
            @Override
            public void run() {
                if (alertDialog != null && alertDialog.isShowing()) {
                    String currentTime = chronometer.getText().toString();
                    long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                    int minutes = (int) (elapsedMillis / 60000);
                    int cost = (minutes / 3);
                    String message = "You have been riding for " + currentTime + ".\nEstimated cost: NT$" + cost + ".\nAre you ready to return the bike?";
                    alertDialog.setMessage(message);
                    handler.postDelayed(this, 1000); // Update every second
                }
            }
        };

        handler.post(updateTimeTask);
    }


    private void stopUpdatingTime() {
        handler.removeCallbacks(updateTimeTask);
    }
}
