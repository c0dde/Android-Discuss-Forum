package com.yswong.discussion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


//This activity is used to view the thread list for a particular discussion board
public class ViewThreadActivity extends AppCompatActivity implements MyAdapter.OnNoteListener{

    //Define Variables
    private String unitID, forumName;
    private Toolbar toolbar;
    private Button newThreadBtn;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MyAdapter myAdapter;
    private DatabaseReference databaseThread;
    private ArrayList<String> replies = new ArrayList<>();
    private ArrayList<String> threadIDs = new ArrayList<>();
    private ArrayList<Thread> threads = new ArrayList<>();


    //Action on the activity create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Link objects to vars
        setContentView(R.layout.activity_view_thread);
        newThreadBtn = findViewById(R.id.button_start_new_thread);
        recyclerView = findViewById(R.id.recyclerview2);
        progressBar = findViewById(R.id.progressBar6);

        //setup the toolbar
        toolbar = findViewById(R.id.viewtoolbar);
        setSupportActionBar(toolbar);

        //Set up toolbar title from intent's unit info
        Intent intent = getIntent();
        unitID = intent.getStringExtra("unitID");
        forumName = intent.getStringExtra("forum");
        getSupportActionBar().setTitle(unitID + " - " + forumName);

        //Set up progressbar
        progressBar.setVisibility(View.VISIBLE);

        //set up firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Check if user is signed in
        if (user != null) {

            //read the thread data in the database
            databaseThread = FirebaseDatabase.getInstance().getReference().child("threads").child(unitID).child(forumName);


            //Create a database listener to retrieve data from the database
            databaseThread.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //check if the forum is empty
                    if (!dataSnapshot.exists())
                    {
                        //prompt user there is no any thread in this discussion board at the moment
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(ViewThreadActivity.this,"No new thread for this forum at the moment.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        //count the reply number for each thread
                        for (DataSnapshot Snap: dataSnapshot.getChildren()) {
                            replies.add(String.valueOf(Snap.child("replies").getChildrenCount()));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //Create a database listener to retrieve data from the database
            databaseThread.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    //get all the related thread ids
                    threadIDs.add(dataSnapshot.getRef().getKey());

                    //add the exist thread to the thread class list
                    threads.add(dataSnapshot.getValue(Thread.class));

                    //display the thread to the recycler view
                    myAdapter = new MyAdapter(ViewThreadActivity.this, threads, replies, ViewThreadActivity.this);
                    recyclerView.setAdapter(myAdapter);
                    LinearLayoutManager LinearLayoutManager = new LinearLayoutManager (ViewThreadActivity.this);
                    //set the recycler view is reverse display
                    //we want the newest post on the top
                    LinearLayoutManager.setReverseLayout(true);
                    LinearLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(LinearLayoutManager);

                    //dismiss the progressbar after loading is finished
                    progressBar.setVisibility(View.INVISIBLE);
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
        }



        //when user clicked the new thread button
        newThreadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start the new thread activity
                Intent intent = new Intent (ViewThreadActivity.this, NewThreadActivity.class);
                intent.putExtra("unitID",unitID);
                intent.putExtra("forum",forumName);
                startActivity(intent);
            }
        });

    }

    //this is a click listener for the recycler view
    @Override
    public void onNoteCLick(int position) {
        //when one of the thread is clicked
        //open the thread content activity with that thread's detail
        //so the the thread content activity will able to display that thread's content to the user
        Intent intent = new Intent(ViewThreadActivity.this, ThreadContentActivity.class);
        intent.putExtra("thread", threads.get(position));
        intent.putExtra("threadID", threadIDs.get(position));
        intent.putExtra("unitID",unitID);
        intent.putExtra("forum",forumName);
        startActivity(intent);
    }
}
