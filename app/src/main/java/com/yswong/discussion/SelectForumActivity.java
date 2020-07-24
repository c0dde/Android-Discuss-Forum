package com.yswong.discussion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


//This activity doesn't involve database operation
//only let user to choose their desire discussion board
//e.g. Student discussion and Question of Unit Chair
public class SelectForumActivity extends AppCompatActivity implements MyAdapter.OnNoteListener {

    //Define variables
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private String unitID, unitName;
    private MyAdapter myAdapter;
    private String[] forums = new String[] {"Student Discussion", "Question for Unit Chair","live"};


    //Action on the activity create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_forum);
        recyclerView = findViewById(R.id.recyclerviewForum);

        //retrieve intents from index activity
        Intent intent = getIntent();
        unitID = intent.getStringExtra("unitID");
        unitName = intent.getStringExtra("unitName");

        //set up toolbar and its title
        toolbar = findViewById(R.id.toolbar_selectForumTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(unitID + " - " + unitName);


        //put all the enrolled units to the recycler view
        myAdapter = new MyAdapter(SelectForumActivity.this, SelectForumActivity.this);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(SelectForumActivity.this));

    }

    @Override
    public void onNoteCLick(int position) {


        // If user choose the first two board, directly turn to those two activity
        // since they are using same activity but different parameters
        Intent intent;
        if(position <=1){
            intent = new Intent(SelectForumActivity.this, ViewThreadActivity.class);
            intent.putExtra("unitID", unitID);
            intent.putExtra("unitName", unitName);
            intent.putExtra("forum", forums[position]);
            startActivity(intent);
        }
        else
        {
            // If user choose the live chat option, turn to the live chat activity
            // and pass the unit id
            intent = new Intent(SelectForumActivity.this, LiveChatActivity.class);
            intent.putExtra("unitID", unitID);
            startActivity(intent);
        }



    }
}
