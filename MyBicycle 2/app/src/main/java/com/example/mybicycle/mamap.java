// mamap.java
package com.example.mybicycle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class mamap extends AppCompatActivity {

    private ConnectionClass connectionClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mamap);

        connectionClass = new ConnectionClass();

        Button button1 = findViewById(R.id.mabutton1);
        Button button2 = findViewById(R.id.mabutton2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetParkingInfoTask().execute(1);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetParkingInfoTask().execute(2);
            }
        });
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton("Balance inquiry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(mamap.this, MainFrame.class);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("cancel", null);
        builder.show();
    }

    private class GetParkingInfoTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            int siteId = params[0];
            return connectionClass.getParkingInfo(siteId);
        }

        @Override
        protected void onPostExecute(String result) {
            showAlertDialog(result + "\nPlease click the button below to scan the QR code.");
        }
    }
}
