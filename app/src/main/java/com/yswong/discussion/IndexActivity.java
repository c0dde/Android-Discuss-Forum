package com.yswong.discussion;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

//This is the activity after user signed in succefully
//It allow user to choose their unit, then direct them to the unit's discussion
public class IndexActivity extends AppCompatActivity implements MyAdapter.OnNoteListener{

    //Define Variables
    private String uid;
    private String[] unitIDs;
    private String[] unitNames;
    private int total_units;
    private MyAdapter myAdapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    //DatabaseReference databaseThread;
    DatabaseReference databaseUser;


    //Action on the activity create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Link objects to vars
        setContentView(R.layout.activity_index);
        recyclerView = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.toolbar_indexTitle);
        progressBar = findViewById(R.id.progressBar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Discussion");

        //set up firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Check if user is signed in
        if (user != null) {


            //Get the user id
            uid = user.getUid();

            //read user enrolled unit
            databaseUser = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

            //Create a database listener to retrieve data from the database
            databaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //get the total units enrolled from the database, it will stored during register steps.
                    total_units = Integer.valueOf(dataSnapshot.child("total").getValue().toString());
                    unitIDs = new  String[total_units];
                    unitNames = new  String[total_units];

                    for (int i = 0; i < total_units; i++)
                    {
                        unitIDs[i] = dataSnapshot.child("unit" + String.valueOf(i+1)).getValue().toString();
                        unitNames[i] = getResources().getStringArray(R.array.UnitName)[Arrays.asList(getResources().getStringArray(R.array.UnitID)).indexOf(unitIDs[i])];
                    }

                    //fill up the recyclerview
                    myAdapter = new MyAdapter(IndexActivity.this, unitIDs, unitNames,IndexActivity.this);
                    recyclerView.setAdapter(myAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(IndexActivity.this));

                    //dismiss the progress bar as the loading is finished
                    progressBar.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.index, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item1)
        {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNoteCLick(int position) {
        //if user choose a unit, go to that unit discussion forum.
        Intent intent = new Intent(IndexActivity.this, SelectForumActivity.class);
        intent.putExtra("unitID", unitIDs[position]);
        intent.putExtra("unitName", unitNames[position]);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    private void logout()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirmation");
        builder.setMessage("Do you want to logout? ");
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(IndexActivity.this, MainActivity.class)); //Go back to home page
                finish();
            }
        });
        builder.show();
    }


}
