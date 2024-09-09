package com.example.mybicycle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

public class customerservice extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private EditText textEntry;
    private ImageButton addImageButton;
    private Button submitButton;
    private Spinner problemTypeSpinner;
    private Uri imageUri;
    private Bitmap bitmap;
    private String selectedProblemType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customerservice);

        textEntry = findViewById(R.id.textentry);
        addImageButton = findViewById(R.id.addimage);
        submitButton = findViewById(R.id.button);
        problemTypeSpinner = findViewById(R.id.problem_type_spinner);

        // 设置Spinner的适配器
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.problem_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        problemTypeSpinner.setAdapter(adapter);

        problemTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProblemType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedProblemType = null;
            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                // 设置选定的图像为addImageButton的图像
                addImageButton.setImageBitmap(bitmap);
                addImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP); // 调整缩放类型
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadData() {
        String text = textEntry.getText().toString();
        if (text.isEmpty() || bitmap == null || selectedProblemType == null) {
            Toast.makeText(this, "Please enter text, select an image, and choose a problem type", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert bitmap to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // Create timestamp
        Timestamp postTime = new Timestamp(new Date().getTime());

        // Insert issue into database
        new InsertIssueTask().execute(1, postTime, selectedProblemType, text, byteArray);
    }

    private class InsertIssueTask extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) {
            int accountId = (int) params[0];
            Timestamp postTime = (Timestamp) params[1];
            String issueType = (String) params[2];
            String issue = (String) params[3];
            byte[] issuePic = (byte[]) params[4];

            ConnectionClass connectionClass = new ConnectionClass();
            return connectionClass.insertIssue(accountId, postTime, issueType, issue, issuePic);
        }

        @Override
        protected void onPostExecute(Boolean insertSuccessful) {
            if (insertSuccessful) {
                Toast.makeText(customerservice.this, "Issue submitted successfully!", Toast.LENGTH_LONG).show();
                finish(); // Close the activity
            } else {
                Toast.makeText(customerservice.this, "Failed to submit issue.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
