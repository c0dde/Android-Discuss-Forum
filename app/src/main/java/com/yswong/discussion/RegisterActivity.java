package com.yswong.discussion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

//This class is used to register a new user
public class RegisterActivity extends AppCompatActivity {

    //Define Variables
    private FirebaseAuth mAuth;
    private EditText user_name, user_email, user_password1, user_password2;
    private AutoCompleteTextView enroll1,  enroll2, enroll3, enroll4;
    private ProgressBar progressBar2;
    private Button registerBtn;
    DatabaseReference databaseUser;


    //Action on the activity create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Link objects to vars
        registerBtn = findViewById(R.id.button_Register);
        user_name = findViewById(R.id.editText_username);
        user_email = findViewById(R.id.editText_userEmail);
        user_password1 = findViewById(R.id.editText_password1);
        user_password2 = findViewById(R.id.editText_password2);
        progressBar2 = findViewById(R.id.progressBar2);

        enroll1 = findViewById(R.id.autoCompleteTextView_enrolled1);
        enroll2 = findViewById(R.id.autoCompleteTextView_enrolled2);
        enroll3 = findViewById(R.id.autoCompleteTextView_enrolled3);
        enroll4 = findViewById(R.id.autoCompleteTextView_enrolled4);

        //set up firebase auth
        mAuth = FirebaseAuth.getInstance();

        //Add Array list to the auto complete Text view
        //Allow the app auto complete the unit id
        //depends on the letter that user input
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.UnitID));

        enroll1.setThreshold(1);
        enroll2.setThreshold(1);
        enroll3.setThreshold(1);
        enroll4.setThreshold(1);

        enroll1.setAdapter(adapter);
        enroll2.setAdapter(adapter);
        enroll3.setAdapter(adapter);
        enroll4.setAdapter(adapter);

        //When the user clicked the register button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check inputs
                if(checkInputs())
                {
                    //display the progress bar
                    progressBar2.setVisibility(View.VISIBLE);

                    //use the firebase auth api
                    //to create a new user
                    mAuth.createUserWithEmailAndPassword(user_email.getText().toString(), user_password1.getText().toString() )
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    //check if the task is successful
                                    if (task.isSuccessful()) {
                                        // Register success, store the user name and enrolled unit to the database.
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(RegisterActivity.this, "Register Success.",
                                                Toast.LENGTH_SHORT).show();

                                        //Store the unit that user input to the database.
                                        storeUserinfo(user.getUid(), user_name.getText().toString());

                                        //dismiss the progressbar
                                        progressBar2.setVisibility(View.INVISIBLE);

                                        //turn to sign in activity with the registered email
                                        Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                                        intent.putExtra("email", user_email.getText().toString());
                                        startActivity(intent);

                                    } else {
                                        // If register fails, display a message to the user.
                                        Toast.makeText(RegisterActivity.this, "Register Failed. \nReason: " + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                        progressBar2.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }



            }
        });

    }

    //Since we cannot access the user name from firebase auth
    //So we need to create a database to store the user info instead
    //We will store the enrolled units and the user name
    private void storeUserinfo(String uid, String name) {
        String unit1, unit2, unit3, unit4;
        int total = 1;

        //student must have at least one unit
        unit1 = enroll1.getText().toString();

        if (!enroll2.getText().toString().isEmpty())
        {
            unit2 = enroll2.getText().toString();
            total+=1;
        }
        else
        {
            unit2 = "";
        }

        if (!enroll3.getText().toString().isEmpty())
        {
            unit3 = enroll3.getText().toString();
            total+=1;
        }
        else
        {
            unit3 = "";
        }

        if (!enroll4.getText().toString().isEmpty())
        {
            unit4 = enroll4.getText().toString();
            total+=1;
        }
        else
        {
            unit4 = "";
        }

        databaseUser = FirebaseDatabase.getInstance().getReference("users");
        User user = new User(name, unit1, unit2 ,unit3, unit4, total);
        databaseUser.child(uid).setValue(user);

    }


    //A method to check if user input all needed
    private boolean checkInputs()
    {
        if (user_name.getText().toString().isEmpty())
        {
            user_name.setError("Please input your full name.");
        }
        else if (user_email.getText().toString().isEmpty())
        {
            user_email.setError("Please input your mail box name.");
        }
        else if (enroll1.getText().toString().isEmpty())
        {
            enroll1.setError("Please at least input one enrolled unit.");
        }
        else if (!enroll1.getText().toString().isEmpty() && !Arrays.asList(getResources().getStringArray(R.array.UnitID)).contains(enroll1.getText().toString()))
        {
            enroll1.setError("Please input a valid unit id.");
        }
        else if (!enroll2.getText().toString().isEmpty() && !Arrays.asList(getResources().getStringArray(R.array.UnitID)).contains(enroll2.getText().toString()))
        {
            enroll2.setError("Please input a valid unit id.");
        }
        else if (!enroll3.getText().toString().isEmpty() && !Arrays.asList(getResources().getStringArray(R.array.UnitID)).contains(enroll3.getText().toString()))
        {
            enroll3.setError("Please input a valid unit id.");
        }
        else if (!enroll4.getText().toString().isEmpty() && !Arrays.asList(getResources().getStringArray(R.array.UnitID)).contains(enroll4.getText().toString()))
        {
            enroll4.setError("Please input a valid unit id.");
        }
        else if (user_password1.getText().toString().isEmpty())
        {
            user_password1.setError("Please input your password.");
        }
        else if (user_password2.getText().toString().isEmpty())
        {
            user_password2.setError("Please input your password.");
        }
        else if (!user_password2.getText().toString().equals(user_password1.getText().toString()))
        {
            user_password2.setError("Passwords do not match, please re-enter the password.");
        }
        else
        {
            return true;
        }
        return false;

    }







}
