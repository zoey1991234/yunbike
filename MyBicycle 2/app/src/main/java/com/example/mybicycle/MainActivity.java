package com.example.mybicycle;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(view -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();

            new LoginTask().execute(user, pass);
        });
    }

    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String user = params[0];
            String pass = params[1];
            boolean loginSuccessful = false;

            ConnectionClass connectionClass = new ConnectionClass();
            Connection connection = connectionClass.CONN();

            if (connection != null) {
                try {
                    String query = "SELECT * FROM accounts WHERE username = ? AND password = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, user);
                    preparedStatement.setString(2, pass);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        loginSuccessful = true;
                    }

                    resultSet.close();
                    preparedStatement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return loginSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean loginSuccessful) {
            if (loginSuccessful) {
                Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, map.class); // Assuming map.class represents your map functionality
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
