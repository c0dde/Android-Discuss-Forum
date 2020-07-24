package com.yswong.discussion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;


//This activity is used to play a YouTube video
//when user click a youtube url from the thread content activity
public class YoutubeActivity extends YouTubeBaseActivity {

    //Define Variables
    private static final String API_Key = "AIzaSyB3gIZluHANJyiX4WJEXC6MVV9eZCEppsQ";
    YouTubePlayerView mYouTubePlayerView;
    YouTubePlayer.OnInitializedListener mOnInitializedListener;
    private String videoId;

    //Action on the activity create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Retrieve Youtube Id from thread content class
        setContentView(R.layout.activity_youtube);
        Intent intent = getIntent();
        videoId = intent.getStringExtra("videoId");

        //Link objects to vars
        mYouTubePlayerView = findViewById(R.id.player);

        //Set up Youtube Player
        mOnInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                //Auto play the video
                youTubePlayer.loadVideo(videoId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        };
        mYouTubePlayerView.initialize(API_Key, mOnInitializedListener);
    }

    //close the activity when user clicked backbutton
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
