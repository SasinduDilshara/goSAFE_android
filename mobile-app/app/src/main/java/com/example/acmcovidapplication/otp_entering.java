package com.example.acmcovidapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class otp_entering extends AppCompatActivity {

    private EditText code_1;
    private EditText code_2;
    private EditText code_3;
    private EditText code_4;
    private EditText code_5;
    private EditText code_6;
    public String phoneNumber;
    private String verificationId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_entering);

        code_1 = findViewById(R.id.code_1);
        code_2 = findViewById(R.id.code_2);
        code_3 = findViewById(R.id.code_3);
        code_4 = findViewById(R.id.code_4);
        code_5 = findViewById(R.id.code_5);
        code_6 = findViewById(R.id.code_6);

        phoneNumber = getIntent().getStringExtra("phoneNumber");

        mAuth = FirebaseAuth.getInstance();

        sendVerificationCode(phoneNumber); //Method to send the verification code
    }

    public void appPermissions(View view) {

        String code = code_1.getText().toString().trim() +
                code_2.getText().toString().trim() +
                code_3.getText().toString().trim() +
                code_4.getText().toString().trim() +
                code_5.getText().toString().trim() +
                code_6.getText().toString().trim();

        if (code.isEmpty() || code.length() < 6) {

            code_1.setError("Enter the six digit code");
            code_1.requestFocus();
            return;
        }
        verifyCode(code);


//        Intent intent = new Intent(otp_entering.this, appPermission.class);
//        startActivity(intent);
    }


    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(otp_entering.this, appPermission.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);

                        } else {
                            Toast.makeText(otp_entering.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {
//        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
//                editText.setText(code);
                verifyCode(code);
            }
            signInWithCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(otp_entering.this, e.getMessage(), Toast.LENGTH_LONG).show();

            if (e instanceof FirebaseAuthInvalidCredentialsException) {

                Toast.makeText(getApplicationContext(),"Invalid Number "+phoneNumber,Toast.LENGTH_LONG).show();
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Toast.makeText(getApplicationContext(),"The SMS quota for the project has been exceeded",Toast.LENGTH_LONG).show();

            }
        }
    };
}
