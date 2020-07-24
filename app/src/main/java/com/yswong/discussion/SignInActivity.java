package com.yswong.discussion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    //Define Variables
    private Button signInBtn;
    private EditText loginEmail, loginPassword;
    private FirebaseAuth mAuth;
    private ProgressBar pBar;
    private TextView toResister;


    //Action on the activity create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Define a Firebase Auth instance
        mAuth = FirebaseAuth.getInstance();

        //Linking object to vars
        toResister = findViewById(R.id.textView12);
        signInBtn = findViewById(R.id.button_signIn);
        loginEmail = findViewById(R.id.editText_login_userEmail);
        loginPassword = findViewById(R.id.editText_login_password);
        pBar = findViewById(R.id.progressBar);


        //Register Text view
        //If user realised they don't have an account, they can click this text view
        //to register a new account
        toResister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to the register activity
                Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //Retrieve email address from register activity
        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            loginEmail.setText(intent.getStringExtra("email"));
        }


        //Sign in button onclick action
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Login", "Sign in button clicked");
                //Valid if user input all the loing data
                if(checkInput())
                {
                    Log.d("Login", "Input Passed");

                    //Make progress bar visible
                    pBar.setVisibility(View.VISIBLE);


                    //Use firebase auth api to sign in the user
                    mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPassword.getText().toString())
                            .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("Login", "Authorising");

                                    //if task is successful
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Login", "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        startActivity(new Intent(SignInActivity.this, IndexActivity.class));
                                        pBar.setVisibility(View.INVISIBLE);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("Login", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        pBar.setVisibility(View.INVISIBLE);
                                    }

                                }
                            });














                }
            }
        });

    }


    //A method Check inputs
    public boolean checkInput(){


        if (loginEmail.getText().toString().isEmpty())
        {
            loginEmail.setError("Please Input your E-mail address.");
            return false;
        }
        else if(loginPassword.getText().toString().isEmpty()){
            loginEmail.setError("Please Input your password.");
            return false;
        }

        return true;

    }
}
