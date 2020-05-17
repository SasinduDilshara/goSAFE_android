package com.example.acmcovidapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;


import com.example.acmcovidapplication.db.DatabaseHelper;
import com.example.acmcovidapplication.db.DeviceModel;
import com.example.acmcovidapplication.services.CustomService;
import com.google.firebase.auth.FirebaseAuth;


import android.view.WindowManager;


import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";



    private static int SPLASH_SCREEN_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.

        setContentView(R.layout.activity_main);

        //this will bind your MainActivity.class file with activity_main.

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            Toast.makeText(getApplicationContext(),FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString(),Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, appPermission.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                else{
                    Intent i=new Intent(MainActivity.this,
                            home.class);
                    //Intent is used to switch from one activity to another.

                    startActivity(i);
                    //invoke the SecondActivity.
                }

                Intent i = new Intent(MainActivity.this,
                        Home.class);
                //Intent is used to switch from one activity to another.

                startActivity(i);
                //invoke the SecondActivity.


                finish();
                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);


    }


    public void goToLogin(View view) {
        Intent intent = new Intent(MainActivity.this, login.class);
        startActivity(intent);

    }





}

