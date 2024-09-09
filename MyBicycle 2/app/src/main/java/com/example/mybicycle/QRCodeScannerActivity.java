package com.example.mybicycle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

public class QRCodeScannerActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private boolean isReturningBike = false;
    private int scannedInventoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the intent extra to check if the user is returning the bike
        Intent intent = getIntent();
        isReturningBike = intent.getBooleanExtra("isReturningBike", false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                initQRCodeScanner();
            }
        } else {
            initQRCodeScanner();
        }
    }

    private void initQRCodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show();
            } else {
                try {
                    scannedInventoryId = Integer.parseInt(result.getContents());
                    new CheckInventoryTask().execute(scannedInventoryId);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid QR code", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class CheckInventoryTask extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {
            int inventoryId = params[0];
            ConnectionClass connectionClass = new ConnectionClass();
            return connectionClass.checkInventoryId(inventoryId);
        }

        @Override
        protected void onPostExecute(Boolean inventoryExists) {
            if (inventoryExists) {
                Toast.makeText(QRCodeScannerActivity.this, "Inventory ID found in database!", Toast.LENGTH_LONG).show();
                if (isReturningBike) {
                    new UpdateReturnTimeTask().execute(scannedInventoryId);
                    navigateToPayment();
                } else {
                    new InsertRentRecordTask().execute(scannedInventoryId);
                    navigateToReturnBike();
                }
            } else {
                Toast.makeText(QRCodeScannerActivity.this, "Inventory ID not found in database.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class InsertRentRecordTask extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {
            int inventoryId = params[0];
            ConnectionClass connectionClass = new ConnectionClass();
            int accountId = 1; // Static accountId
            int rentSite = new Random().nextInt(5) + 1; // Random number between 1 and 5
            Timestamp rentTime = new Timestamp(new Date().getTime());

            boolean insertSuccessful = connectionClass.insertRentRecord(accountId, rentSite, rentTime, inventoryId);
            if (insertSuccessful) {
                connectionClass.updateSiteIdToNull(inventoryId);
            }
            return insertSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean insertSuccessful) {
            if (insertSuccessful) {
                Toast.makeText(QRCodeScannerActivity.this, "Rent record inserted successfully!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(QRCodeScannerActivity.this, "Failed to insert rent record.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class UpdateReturnTimeTask extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {
            int inventoryId = params[0];
            ConnectionClass connectionClass = new ConnectionClass();
            Timestamp returnTime = new Timestamp(new Date().getTime());

            boolean updateSuccessful = connectionClass.updateReturnTime(inventoryId, returnTime);
            if (updateSuccessful) {
                connectionClass.updateSiteIdToRandom(inventoryId);
            }
            return updateSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean updateSuccessful) {
            if (updateSuccessful) {
                Toast.makeText(QRCodeScannerActivity.this, "Return time updated successfully!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(QRCodeScannerActivity.this, "Failed to update return time.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void navigateToReturnBike() {
        Intent intent = new Intent(this, rtn_bicycle.class);
        startActivity(intent);
    }

    private void navigateToPayment() {
        Intent intent = new Intent(this, pay.class);

        // Get the data from the intent that started this activity
        String rentalTime = getIntent().getStringExtra("rental_time");
        int estimatedCost = getIntent().getIntExtra("cost", 0);

        // Pass the data to the pay activity
        intent.putExtra("rental_time", rentalTime);
        intent.putExtra("cost", estimatedCost);

        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initQRCodeScanner();
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void showAlertDialog(boolean isReturning) {
        isReturningBike = isReturning;

        String message = isReturning ? "Returning the bike. Scan the QR code." : "Paying for the ride. Scan the QR code.";

        new AlertDialog.Builder(this)
                .setTitle("QR Code Scanner")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> initQRCodeScanner())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
