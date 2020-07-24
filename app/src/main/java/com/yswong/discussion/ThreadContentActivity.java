package com.yswong.discussion;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;


//This activity is used to display a thread content to user
//When they clicked a particular thread
public class ThreadContentActivity extends AppCompatActivity {

    //Define Variables
    private String unitID;
    private Thread currentThread;
    private String threadID;
    private String forumName;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private ArrayList<String> authors = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> contents = new ArrayList<>();
    private ArrayList<String> replyID = new ArrayList<>();
    private int listSize;

    //DatabaseReference databaseThread;
    DatabaseReference databaseReplies;


    //Action on the activity create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Link objects to vars
        setContentView(R.layout.activity_thread_content);
        toolbar = findViewById(R.id.toolbar_threadTitle);
        recyclerView = findViewById(R.id.recyclerview3);

        //set title for the toolbar, used thread subject
        currentThread = (Thread) getIntent().getSerializableExtra("thread");
        threadID = getIntent().getStringExtra("threadID");
        unitID = getIntent().getStringExtra("unitID");
        forumName = getIntent().getStringExtra("forum");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentThread.getThreadSubject());

        //set up recyclerview for now, to show the author's content
        authors.add( currentThread.getAuthorName());
        dates.add( currentThread.getDatetime());
        contents.add( currentThread.getThreadContent());

        //display the author's content to the recyclerview for now
        myAdapter = new MyAdapter(ThreadContentActivity.this, authors, dates, contents);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ThreadContentActivity.this));

        //only one item in the list at the moment
        listSize = 1;

        //Set up database with correct location to load replies
        databaseReplies = FirebaseDatabase.getInstance().getReference().child("threads").child(unitID).child(forumName).child(threadID).child("replies");

        //Create a database listener to retrieve data from the database
        databaseReplies.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Create an iterator to get all the replies
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    //avoid adding duplicated item
                    if(!replyID.contains(dataSnap.getKey()))
                    {
                        //add item to the list
                        replyID.add(dataSnap.getKey());
                        authors.add(dataSnap.child("authorName").getValue().toString());
                        dates.add(dataSnap.child("datetime").getValue().toString());
                        contents.add(dataSnap.child("threadContent").getValue().toString());

                        //update the recyclerview with each reply
                        myAdapter.notifyItemInserted(authors.size() - 1);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //create a reply button on the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.thread_content, menu);
        return true;
    }

    //set up the action when user click the reply button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.reply)
        {
            //pass the thread id, unit id, and the forum to the new reply activity
            Intent intent = new Intent (ThreadContentActivity.this, NewReplyActivity.class);
            intent.putExtra("threadID", threadID);
            intent.putExtra("unitID", unitID);
            intent.putExtra("forum", forumName);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
