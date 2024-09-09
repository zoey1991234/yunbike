package com.example.mybicycle;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        ImageButton profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(v ->
                Toast.makeText(homepage.this, "Profile Button Clicked", Toast.LENGTH_SHORT).show()
        );

        ImageButton cardButton = findViewById(R.id.card_button);
        cardButton.setOnClickListener(v ->
                Toast.makeText(homepage.this, "Card Button Clicked", Toast.LENGTH_SHORT).show()
        );

        ImageButton helpButton = findViewById(R.id.help_button);
        helpButton.setOnClickListener(v ->
                Toast.makeText(homepage.this, "Help Button Clicked", Toast.LENGTH_SHORT).show()
        );
    }
}
