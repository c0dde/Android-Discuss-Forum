package com.yswong.discussion;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


//This class is used to create a new thread, for a unit and discussion
public class NewThreadActivity extends AppCompatActivity {

    //Define Variables
    private Toolbar toolbar;
    private EditText subject,content;
    private String uid, unitId, authorName, thread_id, thread_subject, thread_content, strDate, forumName;
    DatabaseReference databaseThread;
    private ProgressBar progressBar;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    //Action on the activity create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_thread);
        Intent intent = getIntent();
        unitId = intent.getStringExtra("unitID");
        forumName = intent.getStringExtra("forum");

        //set up toolbar
        toolbar = findViewById(R.id.toolbar_newThread);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Thread Detail");


        //Link objects to vars
        databaseThread = FirebaseDatabase.getInstance().getReference("threads");
        subject = findViewById(R.id.editText_threadSubject);
        content = findViewById(R.id.editText_threadContent);
        progressBar = findViewById(R.id.progressBar4);


    }

    //A method to check if user inputs all the content
    private boolean checkInput()
    {
        if(!subject.getText().toString().isEmpty() && !content.getText().toString().isEmpty())
        {
            return true;
        }
        else
        {
            Toast.makeText(NewThreadActivity.this, "Please fill the Subject and Content", Toast.LENGTH_SHORT).show();
            return false;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.submit, menu);
        return true;
    }

    //When user clicked the top right toolbar send button,
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.submit)
        {
            //Check if user is signed in
            if (user != null) {
                uid = user.getUid();

                //Check user input
                if(checkInput())
                {
                    //Generate a new data from database and get the id
                    thread_id = databaseThread.push().getKey();

                    //Retrieve the thread title and content from edit text
                    thread_subject = subject.getText().toString();
                    thread_content = content.getText().toString();

                    //generate a time stamp
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                    Date date = new Date();
                    strDate = dateFormat.format(date).toString();

                    //Set up the user database, for getting the user full name
                    DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                    //Create a database listener to retrieve data from the database
                    databaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //retrieve the name
                            authorName = dataSnapshot.child("name").getValue().toString();

                            //Once everything is ready, create a thread object
                            Thread thread = new Thread(thread_subject, thread_content, uid, authorName, strDate);

                            //Push the thread object to the database
                            databaseThread.child(unitId).child(forumName).child(thread_id).setValue(thread);

                            //prompt user the thread is submitted
                            Toast.makeText(NewThreadActivity.this, "Thread submitted.", Toast.LENGTH_SHORT).show();

                            //close the activity
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }






            }
        }
        return super.onOptionsItemSelected(item);
    }







}
