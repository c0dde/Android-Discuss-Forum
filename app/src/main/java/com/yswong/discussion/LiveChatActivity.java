package com.yswong.discussion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//This activity is for the Live chat
//App read the live chat content for a unit in real-time
//and post user chat content to the live chat database
public class LiveChatActivity extends AppCompatActivity {

    //Defines Variables
    private ImageButton sendBtn;
    private EditText content;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private String unitID;
    private MyAdapter myAdapter;
    private DatabaseReference databaseLiveChat;
    private String uid, liveThread_id, liveThread_content, strDate, authorName;

    private ArrayList<String> liveId = new ArrayList<>();
    private ArrayList<LiveThread> liveThreads = new ArrayList<>();

    //Action on the activity create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);

        //Link objects to vars
        sendBtn = findViewById(R.id.button_sendBtn);
        content = findViewById(R.id.editText_inputLive);
        recyclerView = findViewById(R.id.recyclerviewLive);

        //Retrieve Intent
        Intent intent = getIntent();
        unitID = intent.getStringExtra("unitID");

        //Set up Toolbar
        toolbar = findViewById(R.id.toolbar_liveTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(unitID + " - Live Chat");

        //set up firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Check if user is signed in
        if (user != null) {

            uid = user.getUid();

            //read the thread data in the database
            databaseLiveChat = FirebaseDatabase.getInstance().getReference().child("threads").child(unitID).child("livechat");

            //Create a database listener to retrieve data from the database
            databaseLiveChat.addChildEventListener(new ChildEventListener() {

                //a listener read the data
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //Get the thread id
                    liveId.add(dataSnapshot.getRef().getKey());

                    //Retrieve the whole thread as a class
                    liveThreads.add(dataSnapshot.getValue(LiveThread.class));

                    //Put all the available threads to the recycler view
                    myAdapter = new MyAdapter(LiveChatActivity.this, liveThreads, uid);
                    recyclerView.setAdapter(myAdapter);
                    LinearLayoutManager linearLayoutManager = new  LinearLayoutManager(LiveChatActivity.this);
                    // As it is a chat activity, so always scroll to bottom.
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //This is for user to send a chat
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Check if user input the message content
                    if(!content.getText().toString().isEmpty()){

                        //generate a thread id
                        liveThread_id = databaseLiveChat.push().getKey();

                        //read the message from the edit text
                        liveThread_content = content.getText().toString();

                        //Create a time stamp
                        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                        Date date = new Date();
                        strDate = dateFormat.format(date).toString();

                        //Set up the user database location
                        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                        //Create a listener to get the user name from database to the database
                        databaseUser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                //retrieve the user name
                                authorName = dataSnapshot.child("name").getValue().toString();

                                //Once all the information is ready
                                //Create a live thread class
                                LiveThread liveThread = new LiveThread(liveThread_content, uid, authorName, strDate);

                                //Locate to the live thread id database and push the class to it
                                databaseLiveChat.child(liveThread_id).setValue(liveThread);

                                //clean the edit text
                                content.setText("");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }



                }
            });


        }
    }
}
