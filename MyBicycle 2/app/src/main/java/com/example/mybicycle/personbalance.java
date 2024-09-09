package com.example.mybicycle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class personbalance extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_balance);

        // 获取布局中的 ImageView 和 TextView
        ImageView easyCardImage = findViewById(R.id.easyCardImage);
        TextView nameTextView = findViewById(R.id.nameTextView);
        TextView balanceTextView = findViewById(R.id.balanceTextView);

        // 设置图片资源
        easyCardImage.setImageResource(R.drawable.easycard);

        // 设置文本内容
        nameTextView.setText("Jerry");
        balanceTextView.setText("balance: 25 dollars");

        // 显示警告对话框
        showAlertDialog();

        // 设置按钮点击事件
        Button exitButton1 = findViewById(R.id.exitbutton1);
        exitButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(personbalance.this, QRCodeScannerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Welcome")
                .setMessage("sufficient balance, click ok to start renting.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface QRCodeScannerActivity, int which) {
                        QRCodeScannerActivity.dismiss();
                    }
                })
                .show();
    }
}