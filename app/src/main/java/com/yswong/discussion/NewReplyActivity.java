package com.yswong.discussion;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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


//This activity is used for create a new reply for a thread
public class NewReplyActivity extends AppCompatActivity {

    //Define Variables
    private Toolbar toolbar;
    private EditText content;
    private String forumName, unitID, uid, authorName, thread_id, reply_id, reply_content, strDate;
    DatabaseReference databaseReply;


    //Action on the activity create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reply);

        //Link objects to vars
        content = findViewById(R.id.editText_replyContent);

        //Set up Toolbar
        toolbar = findViewById(R.id.toolbar_reply);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Reply Detail");

        //receive intent
        Intent intent = getIntent();
        thread_id = intent.getStringExtra("threadID");
        unitID = intent.getStringExtra("unitID");
        forumName = intent.getStringExtra("forum");

        //Set up database location
        databaseReply = FirebaseDatabase.getInstance().getReference("threads").child(unitID).child(forumName).child(thread_id).child("replies");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.submit)
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            //Check if user is signed in
            if (user != null) {
                uid = user.getUid();

                //if the reply is not empty
                if(!content.getText().toString().isEmpty())
                {
                    //get current user name
                    DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                    //Create a database listener to retrieve data from the database
                    databaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            authorName = dataSnapshot.child("name").getValue().toString();

                            //create an unique id for the database
                            reply_id = databaseReply.push().getKey();

                            //get the content
                            reply_content = content.getText().toString();

                            //get current time
                            DateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                            Date date = new Date();
                            strDate = dateFormat.format(date).toString();

                            //create an submit object
                            Reply reply = new Reply(reply_content,uid, authorName,strDate);

                            //push the object to the database
                            databaseReply.child(reply_id).setValue(reply);

                            //response
                            Toast.makeText(NewReplyActivity.this, "Successfully replied.", Toast.LENGTH_SHORT).show();

                            //close the activity when submit is done
                            finish();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
                else
                {
                    //Prompt the user to fill the content

                    Toast.makeText(NewReplyActivity.this, "Please fill the submit content", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
