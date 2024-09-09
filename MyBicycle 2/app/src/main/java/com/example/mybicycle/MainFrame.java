package com.example.mybicycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainFrame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainframe);

        // 显示对话框
        showBalanceCheckDialog();
    }

    private void showBalanceCheckDialog() {
        // 创建对话框视图
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_balance_check, null);
        builder.setView(dialogView);

        // 获取对话框中的视图
        Button allowButton = dialogView.findViewById(R.id.allowButton);
        Button rejectButton = dialogView.findViewById(R.id.rejectButton);

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();

        // 设置 Allow 按钮点击事件
        allowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainFrame.this, personbalance.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        // 设置 Reject 按钮点击事件
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
