package com.kapok.brianramirez.kapok;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class userVerifyActivity extends AppCompatActivity {
    Button verify;
    Button resend;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_verify);
        verify = (Button) findViewById(R.id.login_btn);
        resend = (Button) findViewById(R.id.resend_btn);
        mAuth = Database.mAuth;
        String currentUser = mAuth.getCurrentUser().getEmail();
        FirebaseUser fu = mAuth.getCurrentUser();
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fu.isEmailVerified()){
                    Intent goToProfileSetupIntent = new Intent(userVerifyActivity.this, ProfileSetupActivity.class);
                    startActivity(goToProfileSetupIntent);
                }
                else{
                    Toast.makeText(userVerifyActivity.this, "Verification failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fu.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(userVerifyActivity.this, "Email Sent",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}