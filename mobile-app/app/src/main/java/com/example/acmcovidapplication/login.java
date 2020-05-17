package com.example.acmcovidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class login extends AppCompatActivity {

    private EditText phoneNumber;
    private final String countryCode = "+94";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneNumber = findViewById(R.id.number);
    }

    public void OTPEnter(View view) {

        String number = phoneNumber.getText().toString().trim();

        if (number.isEmpty() || number.length() != 9) {
            phoneNumber.setError("Valid number is required");
            phoneNumber.requestFocus();
            return;
        }

        String phoneNumber = countryCode + number;
        Intent intent = new Intent(login.this, otp_entering.class);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivity(intent);
    }
}
