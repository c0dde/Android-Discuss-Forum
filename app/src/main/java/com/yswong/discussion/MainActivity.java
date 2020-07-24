package com.yswong.discussion;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


//This is the main activity of the app
//It is two buttons, including sign in button and register button
public class MainActivity extends AppCompatActivity {

    //Define Variables
    private Button signin,register;
    private FirebaseAuth mAuth;

    //Action on the activity create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //Link objects to vars
        signin = findViewById(R.id.button_ToSignin);
        register = findViewById(R.id.button_ToRegister);


        //set onclick listener to these two buttons
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to sign in activity
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to register activity
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in, if so directly go to index activity.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(MainActivity.this, IndexActivity.class));
            finish();
        }

    }


}
