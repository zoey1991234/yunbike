package com.example.mybicycle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class pay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay);

        TextView rentaltime = findViewById(R.id.rental_time);
        TextView cost = findViewById(R.id.cost);
        TextView balance = findViewById(R.id.balance);
        Button confirmButton = findViewById(R.id.confirmbutton);
        ImageButton helpButton = findViewById(R.id.helpButton); // Find the ImageButton by its id

        // Get the data from the intent
        String rentalTime = getIntent().getStringExtra("rental_time");
        int estimatedCost = getIntent().getIntExtra("cost", 0);

        // Set the text views with the received data
        rentaltime.setText("Rental Time: " + rentalTime);
        cost.setText("Cost: NT$" + estimatedCost);
        balance.setText("Balance: NT$0");

        confirmButton.setOnClickListener(v -> {
            // Launch map activity
            Intent intent = new Intent(pay.this, map.class);
            startActivity(intent);
        });

        helpButton.setOnClickListener(v -> {
            // Launch customerservice activity
            Intent intent = new Intent(pay.this, customerservice.class);
            startActivity(intent);
        });
    }

}
